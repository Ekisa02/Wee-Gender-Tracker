package com.Joseph.WEE_GEnder_Tracker;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class AiAssistantActivity extends AppCompatActivity implements FaqAdapter.OnDeleteClickListener {

    private static final int REQUEST_CODE_SPEECH_INPUT = 100;

    private RecyclerView recyclerViewFaq;
    private FaqAdapter adapter;
    private SearchView searchView;
    private ImageButton voiceButton;

    // Store combined list of FAQs (local + Firestore)
    private final List<FaqItem> faqList = new ArrayList<>();

    private FirebaseFirestore firestore;
    private CollectionReference faqCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_assistant);

        recyclerViewFaq = findViewById(R.id.recyclerViewFaq);
        searchView = findViewById(R.id.searchView);
        voiceButton = findViewById(R.id.voiceButton);

        firestore = FirebaseFirestore.getInstance();
        faqCollection = firestore.collection("user_questions");

        adapter = new FaqAdapter(faqList, this);
        recyclerViewFaq.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFaq.setAdapter(adapter);

        loadFaqsFromJson();  // Load local FAQs from assets/faq.json

        setupSearch();
        setupVoiceInput();
        listenToFirestoreUpdates();
    }

    // Load FAQs from local faq.json file in assets folder
    private void loadFaqsFromJson() {
        try {
            InputStream is = getAssets().open("faq.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String jsonStr = new String(buffer, StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(jsonStr);

            faqList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String question = obj.getString("question");
                String answer = obj.getString("answer");
                faqList.add(new FaqItem(null, question, answer));
            }

            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load FAQs from JSON", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupSearch() {
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterFaqs(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterFaqs(newText);
                return true;
            }
        });
    }

    private void filterFaqs(String text) {
        if (TextUtils.isEmpty(text)) {
            adapter.filterList(new ArrayList<>(faqList));
            return;
        }
        List<FaqItem> filteredList = new ArrayList<>();
        for (FaqItem item : faqList) {
            if (item.getQuestion().toLowerCase().contains(text.toLowerCase()) ||
                    item.getAnswer().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }

    private void setupVoiceInput() {
        voiceButton.setOnClickListener(v -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Ask your question by voice");
            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
            } catch (Exception e) {
                Toast.makeText(this, "Speech input not supported on your device", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                searchView.setQuery(result.get(0), true);
            }
        }
    }

    // Delete question from Firestore and remove from list, if possible
    @Override
    public void onDelete(FaqItem item) {
        if (item.getId() != null) {
            faqCollection.document(item.getId()).delete()
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Question deleted", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete question", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Cannot delete default/local question", Toast.LENGTH_SHORT).show();
        }
    }

    // Listen to Firestore collection for user questions updates
    private void listenToFirestoreUpdates() {
        faqCollection.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.e("Firestore", "Listen failed.", e);
                return;
            }

            if (snapshots != null) {
                // Remove Firestore FAQs manually for min API 23 compatibility
                Iterator<FaqItem> iterator = faqList.iterator();
                while (iterator.hasNext()) {
                    FaqItem item = iterator.next();
                    if (item.getId() != null) {
                        iterator.remove();
                    }
                }

                // Add updated Firestore questions
                for (QueryDocumentSnapshot doc : snapshots) {
                    if (doc.exists()) {
                        String id = doc.getId();
                        String question = doc.getString("question");
                        String answer = doc.getString("answer");
                        if (question != null && answer != null) {
                            faqList.add(new FaqItem(id, question, answer));
                        }
                    }
                }

                runOnUiThread(() -> adapter.notifyDataSetChanged());
            }
        });
    }

}
