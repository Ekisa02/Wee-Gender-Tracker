package com.Joseph.WEE_GEnder_Tracker;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    EditText emailedittext,passwordedittext;
    Button Loginaccount,btnnext;
    ProgressBar progressbar;
    CheckBox showpassword;
    TextView SignUpbutton , forgotpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Initialize views
        emailedittext = findViewById(R.id.emailloginaccount);
        passwordedittext = findViewById(R.id.passwordloginaccount);
        Loginaccount = findViewById(R.id.btnloginaccount);
        forgotpassword = findViewById(R.id.btnloginaccountforget);
        progressbar = findViewById(R.id.loginprogressbar);
        SignUpbutton = findViewById(R.id.btnloginaccountsignup);
       showpassword = findViewById(R.id.showpasswordcheckbox);

        // ============= NEW ANIMATION CODE START =============
        // Button press animations
        setupButtonAnimations();

        // Input field focus animations
        setupInputFieldAnimations();

        // Logo animation (if you have a logo ImageView in your layout)
        startLogoAnimation();
        // ============= NEW ANIMATION CODE END =============

        showpassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                passwordedittext.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                passwordedittext.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            passwordedittext.setSelection(passwordedittext.getText().length());
        });

        forgotpassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });

        Loginaccount.setOnClickListener(v -> Loginuser());
        SignUpbutton.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this,RegisterActivity.class)));
    }

    // ============= NEW ANIMATION METHODS START =============

    private void setupButtonAnimations() {
        // Create a reusable touch listener
        View.OnTouchListener touchListener = (v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
                    return true; // Consume the event

                case MotionEvent.ACTION_UP:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(150)
                            .withEndAction(() -> v.performClick()).start();
                    return true;

                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
                    return true;
            }
            return false;
        };

        // Apply to all interactive elements
        Loginaccount.setOnTouchListener(touchListener);
        SignUpbutton.setOnTouchListener(touchListener);
        forgotpassword.setOnTouchListener(touchListener);
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
    }

    private void startLogoAnimation() {
         //Uncomment if you have a logo ImageView in your layout
        ImageView logo = findViewById(R.id.logo); // Make sure this ID exists
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
    // ============= NEW ANIMATION METHODS END =============

    //login the user ()
    void Loginuser(){
        String email = emailedittext.getText().toString();
        String password = passwordedittext.getText().toString();

        boolean isValidated = ValidateData(email,password);
        if (!isValidated){
            // ============= NEW ANIMATION CODE START =============
            shakeAnimation(emailedittext);
            shakeAnimation(passwordedittext);
            // ============= NEW ANIMATION CODE END =============
            return;
        }
        LoginaccountFirebase(email,password);
    }

    // ============= NEW ANIMATION METHOD START =============
    private void shakeAnimation(View view) {
        ObjectAnimator shake = ObjectAnimator.ofFloat(view, "translationX", 0, 25, -25, 25, -25, 15, -15, 6, -6, 0);
        shake.setDuration(500);
        shake.start();
    }
    // ============= NEW ANIMATION METHOD END =============


    void LoginaccountFirebase(String email, String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        changeInProgress(true);
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    changeInProgress(false);
                    if (task.isSuccessful()) {
                        if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                            String uid = firebaseAuth.getCurrentUser().getUid();

                            FirebaseFirestore.getInstance().collection("users")
                                    .whereEqualTo("uid", uid)
                                    .get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                        if (!queryDocumentSnapshots.isEmpty()) {
                                            SharedPreferences.Editor editor = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
                                            editor.putBoolean("isLoggedIn", true);
                                            editor.apply();
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Complete your profile info", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(LoginActivity.this, PersonalinfoActivity.class));
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(LoginActivity.this, "Failed to check user info", Toast.LENGTH_SHORT).show();
                                    });

                        } else {
                            Toast.makeText(LoginActivity.this, "Email not verified. Please verify!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void changeInProgress(boolean inProgress){
        if (inProgress){
            progressbar.setVisibility(View.VISIBLE);
            Loginaccount.setVisibility(View.GONE);
        }
        else {
            progressbar.setVisibility(View.GONE);
            Loginaccount.setVisibility(View.VISIBLE);
        }
    }

    boolean ValidateData(String email,String password){
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailedittext.setError("Email is invalid!");
            return false;
        }
        if (password.length()<6){
            passwordedittext.setError("Invalid password length!");
            return false;
        }
        return true;
    }


}