package com.Joseph.WEE_GEnder_Tracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TermsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        TextView termsTextView = findViewById(R.id.termsTextView);
        Button acceptButton = findViewById(R.id.acceptButton);
        Button declineButton = findViewById(R.id.declineButton);

        // Set your terms and conditions text
        String termsText = "Please read these terms and conditions carefully before using Our App.\n\n" +
                "1. Acceptance of Terms\n" +
                "By accessing or using the App, you agree to be bound by these Terms.\n\n" +
                "2. User Responsibilities\n" +
                "You agree not to use the App for any unlawful purpose or in any way that might harm the App.\n\n" +
                "3. Privacy Policy\n" +
                "Your use of the App is also governed by our Privacy Policy.\n\n" +
                "4. Modifications\n" +
                "We reserve the right to modify these terms at any time. Continued use constitutes acceptance.\n\n" +
                "5. Termination\n" +
                "We may terminate or suspend access to our App immediately for any violation.\n\n" +
                "6. Limitation of Liability\n" +
                "We shall not be liable for any damages resulting from the use or inability to use the App.\n\n" +
                "7. Governing Law\n" +
                "These terms shall be governed by the laws of your country/state.";

        termsTextView.setText(termsText);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save acceptance in SharedPreferences
                getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                        .putBoolean("terms_accepted", true).apply();

                // Proceed to main activity
                startActivity(new Intent(TermsActivity.this, MainActivity.class));
                finish();
            }
        });

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the app if terms are declined
                finishAffinity();
            }
        });
    }
}