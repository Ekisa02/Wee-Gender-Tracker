package com.Joseph.WEE_GEnder_Tracker;

import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    EditText emailedittext, passwordedittext, confirmpasswordedittext;
    Button createaccount;
    CheckBox showpassword;
    ProgressBar progressbar;
    TextView loginbutton;
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Initialize views
        emailedittext = findViewById(R.id.emailcreateaccount);
        passwordedittext = findViewById(R.id.passwordcreateaccount);
        confirmpasswordedittext = findViewById(R.id.confirmpasswordcreateaccount);
        createaccount = findViewById(R.id.btncreateaccount);
        progressbar = findViewById(R.id.createaccountprogressbar);
        loginbutton = findViewById(R.id.btncreateaccountlogin);
        showpassword = findViewById(R.id.showpasswordcheckbox);
        logo = findViewById(R.id.logo);

        // ============= NEW ANIMATION CODE START =============
        // Start logo animation
        startLogoAnimation();

        // Setup button animations
        setupButtonAnimations();

        // Setup input field animations
        setupInputFieldAnimations();
        // ============= NEW ANIMATION CODE END =============

        showpassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                passwordedittext.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                confirmpasswordedittext.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                passwordedittext.setTransformationMethod(PasswordTransformationMethod.getInstance());
                confirmpasswordedittext.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            passwordedittext.setSelection(passwordedittext.getText().length());
            confirmpasswordedittext.setSelection(confirmpasswordedittext.getText().length());
        });

        createaccount.setOnClickListener(v -> createAccount());
        loginbutton.setOnClickListener(v -> finish());
    }

    // ============= NEW ANIMATION METHODS START =============
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
        // Create account button animation
        createaccount.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        v.performClick();
                    }
                    return true;
            }
            return false;
        });

        // Login text animation
        loginbutton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        v.performClick();
                    }
                    return true;
            }
            return false;
        });
    }

    private void setupInputFieldAnimations() {
        // Email field focus animation
        emailedittext.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                v.animate().translationZ(8f).setDuration(200).start();
            } else {
                v.animate().translationZ(0f).setDuration(200).start();
            }
        });

        // Password field focus animation
        passwordedittext.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                v.animate().translationZ(8f).setDuration(200).start();
            } else {
                v.animate().translationZ(0f).setDuration(200).start();
            }
        });

        // Confirm password field focus animation
        confirmpasswordedittext.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                v.animate().translationZ(8f).setDuration(200).start();
            } else {
                v.animate().translationZ(0f).setDuration(200).start();
            }
        });
    }

    private void shakeAnimation(View view) {
        ObjectAnimator shake = ObjectAnimator.ofFloat(view, "translationX", 0, 25, -25, 25, -25, 15, -15, 6, -6, 0);
        shake.setDuration(500);
        shake.start();
    }
    // ============= NEW ANIMATION METHODS END =============

    // YOUR EXISTING METHODS - ALL UNCHANGED
    void createAccount() {
        String email = emailedittext.getText().toString();
        String password = passwordedittext.getText().toString();
        String confirmpassword = confirmpasswordedittext.getText().toString();

        boolean isValidated = ValidateData(email, password, confirmpassword);
        if (!isValidated) {
            // ============= NEW ANIMATION CODE START =============
            shakeAnimation(emailedittext);
            shakeAnimation(passwordedittext);
            shakeAnimation(confirmpasswordedittext);
            // ============= NEW ANIMATION CODE END =============
            return;
        }
        createAccountInFirebase(email, password);
    }

    void createAccountInFirebase(String email, String password) {
        changeInProgress(true);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, task -> {
                    changeInProgress(false);
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Successfully created an account, please check your email to verify!", Toast.LENGTH_SHORT).show();
                        firebaseAuth.getCurrentUser().sendEmailVerification();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        if (task.getException() instanceof com.google.firebase.auth.FirebaseAuthUserCollisionException) {
                            Toast.makeText(RegisterActivity.this, "This email is already registered. Redirecting to login...", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void changeInProgress(boolean inProgress) {
        if (inProgress) {
            progressbar.setVisibility(VISIBLE);
            createaccount.setVisibility(View.GONE);
        } else {
            progressbar.setVisibility(View.GONE);
            createaccount.setVisibility(VISIBLE);
        }
    }

    boolean ValidateData(String email, String password, String confirmpassword) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailedittext.setError("Email is invalid!");
            return false;
        }
        if (password.length() < 6) {
            passwordedittext.setError("Invalid password length!");
            return false;
        }
        if (!password.equals(confirmpassword)) {
            confirmpasswordedittext.setError("Password don't match!");
            return false;
        }
        return true;
    }
}