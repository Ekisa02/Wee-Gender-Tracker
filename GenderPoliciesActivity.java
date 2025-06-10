package com.Joseph.WEE_GEnder_Tracker;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.LoadAdError;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class GenderPoliciesActivity extends AppCompatActivity {

    private static final String PDF_NAME = "NATIONAL-POLICY-ON-GENDER-AND-DEVELOPMENT.pdf";
    private InterstitialAd mInterstitialAd;
    private boolean adShownOnLaunch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobileAds.initialize(this, initializationStatus -> {});
        loadInterstitialAd();
    }

    private void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, "ca-app-pub-6877348684255207/2228628348", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        if (!adShownOnLaunch) {
                            showAdOnLaunch();
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError adError) {
                        mInterstitialAd = null;
                        setupContent();
                    }
                });
    }

    private void showAdOnLaunch() {
        if (mInterstitialAd != null) {
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    adShownOnLaunch = true;
                    mInterstitialAd = null;
                    loadInterstitialAd(); // 🔁 Reload for future use (e.g., back press)
                    setupContent();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    setupContent();
                }
            });
            mInterstitialAd.show(this);
        } else {
            setupContent();
        }
    }

    @Override
    public void onBackPressed() {
        if (mInterstitialAd != null) {
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    mInterstitialAd = null;
                    loadInterstitialAd(); // 🔁 Reload ad after it's shown
                    GenderPoliciesActivity.super.onBackPressed();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    GenderPoliciesActivity.super.onBackPressed();
                }
            });
            mInterstitialAd.show(this);
        } else {
            super.onBackPressed();
        }
    }

    private void setupContent() {
        setContentView(R.layout.activity_gender_policies);

        Button downloadButton = findViewById(R.id.downloadButton);
        Button openButton = findViewById(R.id.openButton);

        downloadButton.setOnClickListener(v -> savePdfToDownloads());
        openButton.setOnClickListener(v -> openPdf());

        setupLinkClickListeners();
    }

    private void setupLinkClickListeners() {
        setLinkClickListener(R.id.link_agricultural_policy1,
                "https://kilimo.go.ke/wp-content/uploads/2024/10/Agricultural-Policy-2021.pdf");
        setLinkClickListener(R.id.link_water_act1,
                "https://www.awwda.go.ke/wp-content/uploads/2020/04/WATER-ACT-2016.pdf");
        setLinkClickListener(R.id.link_forest_act1,
                "https://kenyalaw.org/kl/fileadmin/pdfdownloads/Acts/2016/No._34_of_2016.pdf");
        setLinkClickListener(R.id.link_sdgs1,
                "https://sdgs.un.org/goals");
        setLinkClickListener(R.id.link_agricultural_policy2,
                "https://kilimo.go.ke/wp-content/uploads/2024/10/Agricultural-Policy-2021.pdf");
        setLinkClickListener(R.id.link_water_act2,
                "https://www.awwda.go.ke/wp-content/uploads/2020/04/WATER-ACT-2016.pdf");
        setLinkClickListener(R.id.link_sdgs2,
                "https://sdgs.un.org/goals");
    }

    private void setLinkClickListener(int viewId, String url) {
        TextView textView = findViewById(viewId);
        if (textView != null) {
            textView.setOnClickListener(v -> openUrlInBrowser(url));
        }
    }

    private void openUrlInBrowser(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);

            Intent chooser = Intent.createChooser(intent, "Open with");
            if (chooser.resolveActivity(getPackageManager()) != null) {
                startActivity(chooser);
            } else {
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    showToast("No browser could open this link");
                    openPlayStoreForBrowser();
                }
            }
        } catch (Exception e) {
            showToast("Error opening link");
            openPlayStoreForBrowser();
        }
    }

    private void openPlayStoreForBrowser() {
        try {
            Intent playStoreIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=com.android.chrome"));
            startActivity(playStoreIntent);
        } catch (Exception e) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=com.android.chrome"));
            startActivity(webIntent);
        }
    }

    private void savePdfToDownloads() {
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.national_policy);
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File outFile = new File(downloadsDir, PDF_NAME);

            try (FileOutputStream outputStream = new FileOutputStream(outFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
            }

            inputStream.close();
            showToast("PDF saved to Downloads folder!");
        } catch (Exception e) {
            showToast("Failed to save PDF: " + e.getMessage());
        }
    }

    private void openPdf() {
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), PDF_NAME);
            if (!file.exists()) {
                showToast("File not found. Please download first.");
                return;
            }

            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            Intent chooser = Intent.createChooser(intent, "Open PDF with");
            if (chooser.resolveActivity(getPackageManager()) != null) {
                startActivity(chooser);
            } else {
                offerToInstallPdfViewer();
            }
        } catch (Exception e) {
            showToast("Error opening PDF");
        }
    }

    private void offerToInstallPdfViewer() {
        try {
            Intent playStoreIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=com.adobe.reader"));
            startActivity(playStoreIntent);
        } catch (Exception e) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=com.adobe.reader"));
            startActivity(webIntent);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
