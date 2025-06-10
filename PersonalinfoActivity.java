package com.Joseph.WEE_GEnder_Tracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PersonalinfoActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etPhoneNumber;
    private Spinner spinnerGender, spinnerCounty;
    private Button btnSubmit;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalinfo);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        initializeViews();
        setupSpinners();
        setupSubmitButton();
    }

    private void initializeViews() {
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        spinnerGender = findViewById(R.id.spinnerGender);
        spinnerCounty = findViewById(R.id.spinnerCounty);
        btnSubmit = findViewById(R.id.btnSubmit);
    }

    private void setupSpinners() {
        // Gender Spinner
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);

        // County Spinner
        ArrayAdapter<CharSequence> countyAdapter = ArrayAdapter.createFromResource(this,
                R.array.county_array, android.R.layout.simple_spinner_item);
        countyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCounty.setAdapter(countyAdapter);
    }

    private void setupSubmitButton() {
        btnSubmit.setOnClickListener(v -> {
            String firstName = etFirstName.getText().toString().trim();
            String lastName = etLastName.getText().toString().trim();
            String phoneNumber = etPhoneNumber.getText().toString().trim();

            if (validateInputs(firstName, lastName, phoneNumber)) {
                saveUserData(firstName, lastName, phoneNumber);
            }
        });
    }

    private boolean validateInputs(String firstName, String lastName, String phoneNumber) {
        if (firstName.isEmpty()) {
            etFirstName.setError("First name is required");
            return false;
        }
        if (lastName.isEmpty()) {
            etLastName.setError("Last name is required");
            return false;
        }
        if (phoneNumber.isEmpty()) {
            etPhoneNumber.setError("Phone number is required");
            return false;
        }
        return true;
    }

    private void saveUserData(String firstName, String lastName, String phoneNumber) {
        String uid = mAuth.getCurrentUser().getUid();
        String gender = spinnerGender.getSelectedItem().toString();
        String county = spinnerCounty.getSelectedItem().toString();

        Map<String, Object> user = new HashMap<>();
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        user.put("phoneNumber", phoneNumber);
        user.put("gender", gender);
        user.put("county", county);
        user.put("timestamp", System.currentTimeMillis());

        // Save using the UID as the document ID
        db.collection("users").document(uid)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Information saved successfully", Toast.LENGTH_SHORT).show();
                    navigateToMainActivity();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save information: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(PersonalinfoActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}