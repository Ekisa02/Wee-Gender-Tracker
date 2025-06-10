package com.Joseph.WEE_GEnder_Tracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LandingActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        mAuth = FirebaseAuth.getInstance();

        // Set up the summary text
        TextView summaryText = findViewById(R.id.summaryText);
        String summary = "C-LaSAIR is a climate-smart initiative to improve women's livelihoods in Kenya through scalable agricultural technologies like solar-powered irrigation, beekeeping, and rice paddy fish farming. Funded by the Bill & Melinda Gates Foundation.";
        summaryText.setText(summary);

        // Set up the proceed button with pulse animation
        Button proceedButton = findViewById(R.id.proceedButton);
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        proceedButton.startAnimation(pulse);

        proceedButton.setOnClickListener(v -> {
            // Start fade out animation
            Animation fadeOut = AnimationUtils.loadAnimation(LandingActivity.this, R.anim.fade_out);
            findViewById(R.id.rootLayout).startAnimation(fadeOut);

            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    checkAuthStatusAndRedirect();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        });
    }

    private void checkAuthStatusAndRedirect() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            // Case 1: No user is currently logged in (regardless of verification status)
            redirectToLogin();
        } else if (!currentUser.isEmailVerified()) {
            // Case 2: User is logged in but not verified
            handleUnverifiedUser();
        } else {
            // Case 3: User is logged in AND verified
            redirectToMain();
        }
    }

    private void redirectToLogin() {
        Intent intent = new Intent(LandingActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    private void redirectToMain() {
        Intent intent = new Intent(LandingActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    private void handleUnverifiedUser() {
        Toast.makeText(this, "Please verify your email before proceeding.", Toast.LENGTH_LONG).show();
        mAuth.signOut();
        redirectToLogin();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Optional: Auto-redirect if user is already logged in and verified
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            redirectToMain();
        }
    }
}