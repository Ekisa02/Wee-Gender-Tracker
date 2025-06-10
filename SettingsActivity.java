package com.Joseph.WEE_GEnder_Tracker;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    private SwitchMaterial themeSwitch, notificationSwitch; // Changed from Switch to SwitchMaterial
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize preferences
        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);

        // Apply saved language
        applySavedLanguage();

        setContentView(R.layout.activity_settings);

        try {
            // Initialize views with null checks
            initializeViews();

            // Setup listeners
            setupSwitchListeners();
            setupClickListeners();

        } catch (Exception e) {
            Log.e(TAG, "Initialization error", e);
            Toast.makeText(this, "Error initializing settings", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void applySavedLanguage() {
        try {
            String language = sharedPreferences.getString("App_Lang", "en");
            Locale locale = new Locale(language);
            Locale.setDefault(locale);

            Configuration config = new Configuration();
            config.setLocale(locale);
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        } catch (Exception e) {
            Log.e(TAG, "Language application failed", e);
        }
    }

    private void initializeViews() {
        themeSwitch = findViewById(R.id.themeSwitch);
        notificationSwitch = findViewById(R.id.notificationSwitch);

        // Validate critical views
        if (themeSwitch == null || notificationSwitch == null) {
            throw new IllegalStateException("Critical views not found in layout");
        }

        // Set initial states
        themeSwitch.setChecked(sharedPreferences.getBoolean("DarkMode", false));
        notificationSwitch.setChecked(sharedPreferences.getBoolean("Notifications", true));
    }

    private void setupSwitchListeners() {
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            try {
                int nightMode = isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
                AppCompatDelegate.setDefaultNightMode(nightMode);
                sharedPreferences.edit().putBoolean("DarkMode", isChecked).apply();

                // Only recreate if necessary
                if (isChecked != (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)) {
                    recreate();
                }
            } catch (Exception e) {
                Log.e(TAG, "Theme change failed", e);
            }
        });

        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("Notifications", isChecked).apply();
        });
    }

    private void setupClickListeners() {
        // Safe click listener setup
        setClickListener(R.id.changeLanguage, () -> showLanguageDialog());
        setClickListener(R.id.ProjectTeam, () -> startActivity(new Intent(this, StaffProfileActivity.class)));
        setClickListener(R.id.termsConditions, () -> startActivity(new Intent(this, TermsActivity.class)));
        setClickListener(R.id.imageGallery, () -> startActivity(new Intent(this, GallaryActivity.class)));
        setClickListener(R.id.aboutUs, () -> startActivity(new Intent(this, AboutCLaSAIRActivity.class)));
        setClickListener(R.id.contactus, () -> startActivity(new Intent(this, ContactUsActivity.class)));
        setClickListener(R.id.surveyRecords, () -> startActivity(new Intent(this,SurveyRecordsActivity.class)));
        setClickListener(R.id.shareApp, this::shareApp);
    }

    private void setClickListener(int viewId, Runnable action) {
        View view = findViewById(viewId);
        if (view != null) {
            view.setOnClickListener(v -> {
                try {
                    action.run();
                } catch (Exception e) {
                    Log.e(TAG, "Click action failed", e);
                }
            });
        }
    }

    private void showLanguageDialog() {
        try {
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.language_dialog);

            Button btnEnglish = dialog.findViewById(R.id.btnEnglish);
            Button btnSwahili = dialog.findViewById(R.id.btnSwahili);

            if (btnEnglish == null || btnSwahili == null) {
                throw new IllegalStateException("Language buttons not found");
            }

            btnEnglish.setOnClickListener(v -> updateLanguage("en", dialog));
            btnSwahili.setOnClickListener(v -> updateLanguage("sw", dialog));

            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Language dialog error", e);
            Toast.makeText(this, "Cannot show language options", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateLanguage(String code, Dialog dialog) {
        try {
            setAppLocale(code);
            dialog.dismiss();
        } catch (Exception e) {
            Log.e(TAG, "Language update failed", e);
        }
    }

    private void setAppLocale(String localeCode) {
        try {
            // Update configuration
            Locale locale = new Locale(localeCode);
            Locale.setDefault(locale);

            Configuration config = new Configuration();
            config.setLocale(locale);
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());

            // Save preference
            sharedPreferences.edit().putString("App_Lang", localeCode).apply();

            // Restart activity
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Locale change failed", e);
        }
    }

    private void shareApp() {
        try {
            String shareText = "Check out this app: " + getPackageName();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        } catch (Exception e) {
            Log.e(TAG, "Share failed", e);
            Toast.makeText(this, "Cannot share app", Toast.LENGTH_SHORT).show();
        }
    }
}