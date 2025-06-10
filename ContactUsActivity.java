package com.Joseph.WEE_GEnder_Tracker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ContactUsActivity extends AppCompatActivity {

    Button callUsBtn, emailUsBtn;
    FloatingActionButton fabEmail, fabphone;
    LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        callUsBtn = findViewById(R.id.call_us_btn);
        emailUsBtn = findViewById(R.id.email_us_btn);
        fabEmail = findViewById(R.id.fab_email);
        fabphone = findViewById(R.id.fabphone);
        mainLayout = findViewById(R.id.mainLayout);

        // Apply fade-in animation
        mainLayout.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));


        callUsBtn.setOnClickListener(v -> {


            new AlertDialog.Builder(this)
                    .setTitle("Call Us")
                    .setMessage("Do you want to call +254717519134?")
                    .setPositiveButton("Call", (dialog, which) -> {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:+254717519134"));
                        startActivity(callIntent);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
        fabphone.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Call Us")
                    .setMessage("Do you want to call +254717519134?")
                    .setPositiveButton("Call", (dialog, which) -> {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:+254717519134"));
                        startActivity(callIntent);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        emailUsBtn.setOnClickListener(v -> openEmailIntent());

        fabEmail.setOnClickListener(v -> {
            Toast.makeText(this, "Opening Email...", Toast.LENGTH_SHORT).show();
            openEmailIntent();
        });

    }

    private void openEmailIntent() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:kodhiambo@uoeeld.ac.ke"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Help Needed");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello, I need assistance with...");
        try {
            startActivity(Intent.createChooser(emailIntent, "Send Email"));
        } catch (Exception e) {
            Toast.makeText(this, "No email app installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
