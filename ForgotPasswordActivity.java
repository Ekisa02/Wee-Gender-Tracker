package com.Joseph.WEE_GEnder_Tracker;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgotPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private com.google.android.material.textfield.TextInputLayout emailContainer;
    private com.google.android.material.textfield.TextInputEditText emailEditText;
    private MaterialButton resetButton;
    private ProgressBar progressBar;
    private TextView statusMessage;
    private TextView backToLogin;
    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize views
        initializeViews();
        setupFirebase();
        setupAnimations();
        setClickListeners();
    }

    private void initializeViews() {
        emailContainer = findViewById(R.id.emailContainer);
        emailEditText = findViewById(R.id.emailEditText);
        resetButton = findViewById(R.id.resetButton);
        progressBar = findViewById(R.id.progressBar);
        statusMessage = findViewById(R.id.statusMessage);
        backToLogin = findViewById(R.id.backToLogin);
        logo = findViewById(R.id.logo);
    }

    private void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void setupAnimations() {
        // Logo animation
        startLogoAnimation();

        // Button animations
        setupButtonAnimations();

        // Input field animation
        setupInputFieldAnimations();
    }

    private void startLogoAnimation() {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(logo, "scaleX", 1f, 1.1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(logo, "scaleY", 1f, 1.1f);

        scaleX.setDuration(1000);
        scaleY.setDuration(1000);
        scaleX.setRepeatCount(ValueAnimator.INFINITE);
        scaleY.setRepeatCount(ValueAnimator.INFINITE);
        scaleX.setRepeatMode(ValueAnimator.REVERSE);
        scaleY.setRepeatMode(ValueAnimator.REVERSE);
        scaleX.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleY.setInterpolator(new AccelerateDecelerateInterpolator());

        scaleX.start();
        scaleY.start();
    }

    private void setupButtonAnimations() {
        View.OnTouchListener touchListener = (v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
                    return true;
                case MotionEvent.ACTION_UP:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
                    v.performClick();
                    return true;
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
                    return true;
            }
            return false;
        };

        resetButton.setOnTouchListener(touchListener);
        backToLogin.setOnTouchListener(touchListener);
    }

    private void setupInputFieldAnimations() {
        emailEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                v.animate().translationZ(8f).setDuration(200).start();
            } else {
                v.animate().translationZ(0f).setDuration(200).start();
            }
        });
    }

    private void setClickListeners() {
        resetButton.setOnClickListener(v -> resetPassword());
        backToLogin.setOnClickListener(v -> handleBackAction());
    }

    private void handleBackAction() {
        String email = emailEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(email)) {
            new AlertDialog.Builder(this)
                    .setTitle("Discard Changes?")
                    .setMessage("You've entered an email address. Are you sure you want to go back?")
                    .setPositiveButton("Yes", (dialog, which) -> finish())
                    .setNegativeButton("No", null)
                    .show();
        } else {
            finish();
        }
    }

    private void resetPassword() {
        String email = emailEditText.getText().toString().trim();

        if (!validateEmail(email)) {
            return;
        }

        showProgress(true);
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    showProgress(false);
                    if (task.isSuccessful()) {
                        handleSuccess();
                    } else {
                        handleError(task.getException());
                    }
                });
    }

    private boolean validateEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            emailContainer.setError("Email is required");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailContainer.setError("Please enter a valid email");
            return false;
        }

        emailContainer.setError(null);
        return true;
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        resetButton.setEnabled(!show);
        statusMessage.setVisibility(View.GONE);
    }

    private void handleSuccess() {
        statusMessage.setText("Password reset email sent successfully");
        statusMessage.setTextColor(ContextCompat.getColor(this, R.color.green));
        statusMessage.setVisibility(View.VISIBLE);
        emailEditText.setText("");
    }

    private void handleError(Exception exception) {
        String errorMessage = "An error occurred. Please try again.";

        if (exception instanceof FirebaseAuthInvalidUserException) {
            errorMessage = "No account found with this email address";
        }

        statusMessage.setText(errorMessage);
        statusMessage.setTextColor(ContextCompat.getColor(this, R.color.red));
        statusMessage.setVisibility(View.VISIBLE);
    }
}