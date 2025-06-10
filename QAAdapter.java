package com.Joseph.WEE_GEnder_Tracker;

import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class QAAdapter extends FirestoreRecyclerAdapter<QAItem, QAAdapter.QAViewHolder> {

    public interface QuestionActionListener {
        void onSubmitAnswer(String documentId, String answer);
        void onEditQuestion(String documentId, String currentQuestion);
        void onDeleteQuestion(String documentId);
    }

    private boolean canCurrentUserAnswer() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.getEmail() == null) {
            return false;
        }

        // List of allowed exact email addresses
        String[] allowedEmails = {
                "josephekisaopurongo@gmail.com",  // Your specific email
                "admin@company.com",             // Other authorized emails
                "professor@university.edu"
        };

        String currentEmail = user.getEmail().toLowerCase().trim();

        // Special handling for Gmail variations
        if (currentEmail.endsWith("@gmail.com")) {
            currentEmail = currentEmail.split("@")[0]
                    .replace(".", "")  // Remove dots in username
                    .split("\\+")[0]   // Remove + suffixes
                    + "@gmail.com";
        }

        // Check against allowed emails
        for (String allowedEmail : allowedEmails) {
            if (currentEmail.equals(allowedEmail.toLowerCase().trim())) {
                return true;
            }
        }
        return false;
    }

    private final QuestionActionListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault());
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public QAAdapter(@NonNull FirestoreRecyclerOptions<QAItem> options, QuestionActionListener listener) {
        super(options);
        this.listener = listener;
    }

    protected void onBindViewHolder(@NonNull QAViewHolder holder, int position, @NonNull QAItem model) {
        DocumentSnapshot snapshot = getSnapshots().getSnapshot(position);
        model.setDocumentId(snapshot.getId());
        holder.currentItem = model;

        // Set question content
        holder.questionText.setText(model.getQuestion());
        holder.questionTime.setText(formatTime(model.getTimestamp()));

        // Set answer content if exists
        if (model.getAnswer() != null && !model.getAnswer().isEmpty()) {
            holder.answerText.setText(model.getAnswer());
            holder.answerTime.setText(formatTime(model.getAnswerTimestamp()));
            holder.answerContainer.setVisibility(View.VISIBLE);
            holder.answerInputLayout.setVisibility(View.GONE);
        } else {
            holder.answerContainer.setVisibility(View.GONE);
            holder.answerInputLayout.setVisibility(View.VISIBLE);
        }

        boolean canAnswer = canCurrentUserAnswer();

        holder.answerInputLayout.setEnabled(canAnswer);
        holder.answerEditText.setHint(canAnswer ?
                "" :
                "Answers restricted to authorized emails");

        if (!canAnswer) {
            holder.answerEditText.setOnClickListener(v ->
                    Toast.makeText(holder.itemView.getContext(),
                            "Only specific email domains can answer",
                            Toast.LENGTH_SHORT).show());
        }

        // Load user names
        loadUserNames(holder, model);
        // Set long click listener for question actions
        holder.itemView.setOnLongClickListener(v -> {
            if (auth.getCurrentUser() != null &&
                    auth.getCurrentUser().getUid().equals(model.getUserId())) {
                showQuestionOptionsDialog(holder, model);
                return true;
            }
            return false;
        });
    }

    private void loadUserNames(QAViewHolder holder, QAItem model) {
        // Load question asker's name
        if (model.getUserId() != null) {
            loadUserName(model.getUserId(), holder.questionUserName, true);
        } else {
            holder.questionUserName.setText("Anonymous");
        }

        // Load answerer's name if answer exists
        if (model.getAnswer() != null && model.getAnswerUserId() != null) {
            loadUserName(model.getAnswerUserId(), holder.answerUserName, false);
            holder.answerUserName.setVisibility(View.VISIBLE);
        } else {
            holder.answerUserName.setVisibility(View.GONE);
        }
    }

    private void loadUserName(String userId, TextView textView, boolean isQuestion) {
        if (userId == null || userId.isEmpty()) {
            textView.setText(isQuestion ? "Anonymous" : "");
            return;
        }

        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String firstName = documentSnapshot.getString("firstName");
                        String lastName = documentSnapshot.getString("lastName");
                        String displayName = (firstName != null ? firstName : "") +
                                (lastName != null ? " " + lastName : "");
                        textView.setText(displayName.isEmpty() ? "Anonymous" : displayName);
                    } else {
                        textView.setText(isQuestion ? "Anonymous" : "");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("QAAdapter", "Error loading user name", e);
                    textView.setText(isQuestion ? "Anonymous" : "");
                });
    }

    @NonNull
    @Override
    public QAViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_qa, parent, false);
        return new QAViewHolder(view, listener);
    }

    private void showQuestionOptionsDialog(QAViewHolder holder, QAItem model) {
        new AlertDialog.Builder(holder.itemView.getContext())
                .setTitle("Question Options")
                .setItems(new String[]{"Edit", "Delete"}, (dialog, which) -> {
                    if (which == 0) {
                        listener.onEditQuestion(model.getDocumentId(), model.getQuestion());
                    } else {
                        listener.onDeleteQuestion(model.getDocumentId());
                    }
                })
                .show();
    }

    private String formatTime(Timestamp timestamp) {
        return timestamp != null ? dateFormat.format(timestamp.toDate()) : "";
    }

    static class QAViewHolder extends RecyclerView.ViewHolder {
        final TextView questionText, questionTime, answerText, answerTime;
        final TextView questionUserName, answerUserName;
        final TextInputLayout answerInputLayout;
        final TextInputEditText answerEditText;
        final View answerContainer;
        QAItem currentItem;

        QAViewHolder(@NonNull View itemView, QuestionActionListener listener) {
            super(itemView);
            questionText = itemView.findViewById(R.id.questionText);
            questionTime = itemView.findViewById(R.id.questionTime);
            answerText = itemView.findViewById(R.id.answerText);
            answerTime = itemView.findViewById(R.id.answerTime);
            questionUserName = itemView.findViewById(R.id.questionUserName);
            answerUserName = itemView.findViewById(R.id.answerUserName);
            answerInputLayout = itemView.findViewById(R.id.answerInputLayout);
            answerEditText = itemView.findViewById(R.id.answerEditText);
            answerContainer = itemView.findViewById(R.id.answerContainer);

            answerInputLayout.setEndIconOnClickListener(v -> {
                String answer = answerEditText.getText().toString().trim();
                if (!answer.isEmpty() && currentItem != null) {
                    listener.onSubmitAnswer(currentItem.getDocumentId(), answer);
                    answerEditText.setText("");
                }
            });
        }
    }
}