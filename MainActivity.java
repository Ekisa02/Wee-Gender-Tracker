package com.Joseph.WEE_GEnder_Tracker;

import com.google.android.gms.ads.MobileAds;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;
    private ViewPagerAdapter viewPagerAdapter;
    private SharedPreferences sharedPref;
    private int currentStep = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


      MobileAds.initialize(this, initializationStatus -> {});


        // Initialize SharedPreferences
        sharedPref = getSharedPreferences("APP_PREFS", MODE_PRIVATE);

        // Check terms acceptance
        if (!sharedPref.getBoolean("terms_accepted", true)) {
            startActivity(new Intent(this, TermsActivity.class));
            finish();
            return;
        }

        initializeViews();
        setupViewPager();
        setupBottomNavigation();

        // Show app tour on first launch
        if (sharedPref.getBoolean("first_launch", true)) {
            new Handler().postDelayed(this::showSimpleTour, 500);
        }
    }

    private void initializeViews() {
        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void setupViewPager() {
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setUserInputEnabled(true);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int position = 0;
            if (item.getItemId() == R.id.nav_home) {
                position = 0;
            } else if (item.getItemId() == R.id.nav_resources) {
                position = 1;
            } else if (item.getItemId() == R.id.nav_gender_tracker) {
                position = 2;
            } else if (item.getItemId() == R.id.nav_community) {
                position = 3;
            }
            viewPager.setCurrentItem(position, true);
            return true;
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
            }
        });
    }
    private void showSimpleTour() {
        final int[] tourSteps = {
                R.id.nav_home,
                R.id.nav_resources,
                R.id.nav_gender_tracker,
                R.id.nav_community
        };

        final String[] titles = {
                "🏠 Home Tab",
                "📚 Resources",
                "🌱 Gender Tracker",
                "👥 Community"
        };

        final String[] descriptions = {
                "Your personalized dashboard with quick access to all features.",
                "Discover materials to enhance your understanding about Gender. ",
                "Monitor,Tracker Gender growth stages and gender development.",
                "Connect and ask Questions relating to gender activities and also share  your experiences."
        };

        final int[] colors = {
                Color.parseColor("#4CAF50"),  // Green
                Color.parseColor("#2196F3"),  // Blue
                Color.parseColor("#FF9800"),  // Orange
                Color.parseColor("#9C27B0")  // Purple
        };

        final int[] icons = {
                R.drawable.baseline_home_24,      // Replace with your icons
                R.drawable.ic_survey,
                R.drawable.ic_trending_up,
                R.drawable.ic_people
        };

        if (currentStep < tourSteps.length) {
            View target = findViewById(tourSteps[currentStep]);

            // Highlight the target view
            highlightTarget(target, colors[currentStep]);

            // Create custom dialog
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.CustomTourDialog);

            // Inflate custom layout
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.custom_tour_dialog, null);

            // Set custom view
            builder.setView(dialogView);

            // Configure dialog elements
            TextView titleView = dialogView.findViewById(R.id.tour_title);
            TextView descView = dialogView.findViewById(R.id.tour_description);
            ImageView iconView = dialogView.findViewById(R.id.tour_icon);
            Button nextButton = dialogView.findViewById(R.id.tour_next);
            Button skipButton = dialogView.findViewById(R.id.tour_skip);
            ProgressBar progressBar = dialogView.findViewById(R.id.tour_progress);

            // Set values
            titleView.setText(titles[currentStep]);
            descView.setText(descriptions[currentStep]);
            iconView.setImageResource(icons[currentStep]);
            dialogView.setBackgroundColor(colors[currentStep]);
            progressBar.setMax(tourSteps.length);
            progressBar.setProgress(currentStep + 1);

            // Create dialog
            AlertDialog dialog = builder.create();

            // Set button actions
            nextButton.setOnClickListener(v -> {
                dialog.dismiss();
                currentStep++;
                removeHighlight(target);
                showSimpleTour();
            });

            skipButton.setOnClickListener(v -> {
                dialog.dismiss();
                removeHighlight(target);
                sharedPref.edit().putBoolean("first_launch", false).apply();
            });

            dialog.setCancelable(false);
            dialog.show();

            // Add window animations
            dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        } else {
            sharedPref.edit().putBoolean("first_launch", false).apply();
        }
    }

    private void highlightTarget(View target, int color) {
        // Create a highlight effect (you can customize this further)
        if (target != null) {
            target.setBackgroundColor(color);
            target.animate()
                    .scaleX(1.1f)
                    .scaleY(1.1f)
                    .setDuration(300)
                    .start();
        }
    }

    private void removeHighlight(View target) {
        if (target != null) {
            target.setBackgroundResource(0); // Reset background
            target.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(300)
                    .start();
        }
    }
}