package com.Joseph.WEE_GEnder_Tracker;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.service.controls.actions.FloatAction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.HashMap;
import java.util.Map;

public class QASectionFragment extends Fragment {

    private TextInputLayout questionInputLayout;
    private TextInputEditText questionEditText;
    private RecyclerView questionsRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView emptyStateText;
    private FirebaseFirestore db;
    private CollectionReference questionsCollection;
    private QAAdapter qaAdapter;
    private FirebaseAuth auth;
    private InterstitialAd mInterstitialAd;
    private ProgressDialog progressDialog;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        initializeFirestore();
    }

    private void initializeFirestore() {
        try {
            db = FirebaseFirestore.getInstance();
            questionsCollection = db.collection("questions");
        } catch (Exception e) {
            Log.e("QAFragment", "Firestore init failed", e);
            showToast("Database error. Restart app.");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_q_a_section, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupRecyclerView();
        setupListeners();

        // Initialize views for Ai Agent
        FloatingActionButton Aiagentfloatingbtn =view.findViewById(R.id.Aiagentfloatingbtn);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading,Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show(); // Show while loading the ad


        // Load the interstitial ad
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(getContext(), "ca-app-pub-6877348684255207/2228628348", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        progressDialog.dismiss(); // Dismiss when ad is ready
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                        mInterstitialAd = null;
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Ad failed to load", Toast.LENGTH_SHORT).show();
                    }
                });

        Aiagentfloatingbtn.setOnClickListener(v -> {
            if (mInterstitialAd != null) {
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Wait 5 seconds after the ad is dismissed
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            Intent intent = new Intent(getActivity(), AiAssistantActivity.class);
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.slide_up1, R.anim.no_animation);
                        }, 1000);
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        mInterstitialAd = null;
                        Toast.makeText(getContext(), "Ad failed to show", Toast.LENGTH_SHORT).show();
                    }
                });

                mInterstitialAd.show(getActivity());
            } else {
                // Ad not ready — navigate directly
                Intent intent = new Intent(getActivity(), AiAssistantActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_up1, R.anim.no_animation);
            }
        });




        // Initialize views for notice banner
        MaterialCardView noticeBanner = view.findViewById(R.id.noticeBanner);
        MaterialButton closeNoticeButton = view.findViewById(R.id.closeNoticeButton);

        // Set click listener for close button
        closeNoticeButton.setOnClickListener(v -> {
            noticeBanner.setVisibility(View.GONE);
        });

    }

    private void initViews(View view) {
        questionInputLayout = view.findViewById(R.id.questionInputLayout);
        questionEditText = view.findViewById(R.id.questionEditText);
        questionsRecyclerView = view.findViewById(R.id.questionsRecyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        emptyStateText = view.findViewById(R.id.emptyStateText);

    }

    private void setupRecyclerView() {
        Query query = questionsCollection.orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<QAItem> options = new FirestoreRecyclerOptions.Builder<QAItem>()
                .setQuery(query, QAItem.class)
                .build();

        qaAdapter = new QAAdapter(options, new QAAdapter.QuestionActionListener() {
            @Override
            public void onSubmitAnswer(String documentId, String answer) {
                submitAnswer(documentId, answer);
            }

            @Override
            public void onEditQuestion(String documentId, String currentQuestion) {
                showEditDialog(documentId, currentQuestion);
            }

            @Override
            public void onDeleteQuestion(String documentId) {
                confirmDelete(documentId);
            }
        });

        questionsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        questionsRecyclerView.setAdapter(qaAdapter);

        // Auto-scroll to new questions
        qaAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                if (positionStart == 0 && itemCount == 1) {
                    questionsRecyclerView.smoothScrollToPosition(0);
                }
            }
        });

        // Empty state handler
        qaAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                checkEmptyState();
            }
        });
    }

    private void setupListeners() {
        questionInputLayout.setEndIconOnClickListener(v -> submitQuestion());
        swipeRefreshLayout.setOnRefreshListener(() -> {
            qaAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void submitQuestion() {
        String questionText = questionEditText.getText().toString().trim();
        if (questionText.isEmpty()) {
            showToast("Question cannot be empty");
            return;
        }

        if (auth.getCurrentUser() == null) {
            showToast("You must be logged in");
            return;
        }

        QAItem newQuestion = new QAItem(
                questionText,
                "",
                null, // Server will set timestamp
                auth.getCurrentUser().getUid()
        );

        questionsCollection.add(newQuestion)
                .addOnSuccessListener(documentReference -> questionEditText.setText(""))
                .addOnFailureListener(e -> showToast("Failed to send question"));
    }

    private void submitAnswer(String questionId, String answer) {
        // Get current authenticated user
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            showToast("You must be logged in to answer questions");
            return;
        }

        // Get user email safely
        String userEmail = currentUser.getEmail();
        if (userEmail == null) {
            showToast("No email associated with this account");
            return;
        }

        // Check against allowed emails
        if (!isEmailAllowed(userEmail)) {
            showToast("Only authorized users can answer questions");
            return;
        }

        // Prepare answer data
        Map<String, Object> updates = new HashMap<>();
        updates.put("answer", answer);
        updates.put("answerUserId", currentUser.getUid());
        updates.put("answerEmail", userEmail); // Store email for reference
        updates.put("answerTimestamp", FieldValue.serverTimestamp());

        // Submit to Firestore
        questionsCollection.document(questionId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    // Optional: Add any success handling
                })
                .addOnFailureListener(e -> {
                    Log.e("QA", "Answer submission failed", e);
                    showToast("Failed to send answer: " + e.getMessage());
                });
    }

    private boolean isEmailAllowed(String email) {
        if (email == null || email.isEmpty()) {
            Log.d("AUTH", "Email is null or empty");
            return false;
        }

        String normalizedInput = normalizeEmail(email.trim());
        Log.d("AUTH", "Normalized input: " + normalizedInput);

        String[] allowedEmails = {
                "josephekisaopurongo@gmail.com",
                "ronojenni@gmail.com",
                "kodhiambo@uoeld.ac.ke",
                "lolimofestus@gmail.com"

        };

        for (String allowed : allowedEmails) {
            String normalizedAllowed = normalizeEmail(allowed.trim());
            if (normalizedInput.equalsIgnoreCase(normalizedAllowed)) {
                Log.d("AUTH", "Email matched: " + allowed);
                return true;
            }
        }
        Log.d("AUTH", "No match found");
        return false;
    }

    private String normalizeEmail(String email) {
        if (email == null) return null;
        email = email.trim().toLowerCase();

        // Handle Gmail quirks
        if (email.endsWith("@gmail.com")) {
            String[] parts = email.split("@");
            String username = parts[0]
                    .replace(".", "")      // Remove dots
                    .split("\\+")[0];     // Remove + suffixes
            return username + "@gmail.com";
        }
        return email;
    }

    private void showEditDialog(String documentId, String currentQuestion) {
        EditText input = new EditText(requireContext());
        input.setText(currentQuestion);

        new AlertDialog.Builder(requireContext())
                .setTitle("Edit Question")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newQuestion = input.getText().toString().trim();
                    if (!newQuestion.isEmpty()) {
                        updateQuestion(documentId, newQuestion);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateQuestion(String documentId, String newQuestion) {
        questionsCollection.document(documentId)
                .update("question", newQuestion)
                .addOnSuccessListener(aVoid -> showToast("Question updated"))
                .addOnFailureListener(e -> showToast("Update failed"));
    }

    private void confirmDelete(String documentId) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Question")
                .setMessage("Are you sure? This cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteQuestion(documentId))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteQuestion(String documentId) {
        questionsCollection.document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> showToast("Question deleted"))
                .addOnFailureListener(e -> showToast("Delete failed"));
    }

    private void checkEmptyState() {
        emptyStateText.setVisibility(qaAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (qaAdapter != null) qaAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (qaAdapter != null) qaAdapter.stopListening();
    }
}