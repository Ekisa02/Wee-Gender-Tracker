package com.Joseph.WEE_GEnder_Tracker;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GallaryActivity extends AppCompatActivity {
    private static final String TAG = "GallaryActivity";
    private InterstitialAd mInterstitialAd;
    private ProgressDialog progressDialog;
    private boolean shouldExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallary);

        // Show loading spinner
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading gallery and ads...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Initialize AdMob
        MobileAds.initialize(this, initializationStatus -> {});

        // Load Interstitial Ad
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, "ca-app-pub-6877348684255207/2228628348", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        Log.d(TAG, "Interstitial ad loaded.");
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                        mInterstitialAd = null;
                        Log.e(TAG, "Interstitial ad failed to load: " + adError.getMessage());
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                });

        // Load assets
        listAssetsInImagesFolder();

        // Set up RecyclerView
        RecyclerView galleryRecyclerView = findViewById(R.id.galleryRecyclerView);
        galleryRecyclerView.setHasFixedSize(true);
        galleryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        galleryRecyclerView.setAdapter(new GalleryAdapter(this, createGalleryItems()));
    }

    @Override
    public void onBackPressed() {
        if (mInterstitialAd != null) {
            shouldExit = true;
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    mInterstitialAd = null;
                    if (shouldExit) {
                        GallaryActivity.super.onBackPressed();
                    }
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                    if (shouldExit) {
                        GallaryActivity.super.onBackPressed();
                    }
                }
            });
            mInterstitialAd.show(this);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private List<GalleryItem> createGalleryItems() {
        List<GalleryItem> items = new ArrayList<>();
        items.add(new GalleryItem("Kapkeita Tum Gaa SHG, Keiyo South,\n Elgeiyo Marakwet", "gallery/img6.jpeg", "Group engagement and activity identification - Kapkeita Tum Gaa SHG, Keiyo South, Elgeiyo Marakwet"));
        items.add(new GalleryItem("Chepkogin FFS, Keiyo North", "gallery/img2.jpeg", "Group engagement and activity identification - Chepkogin FFS, Keiyo North."));
        items.add(new GalleryItem("Setek Kiptorok Women Group", "gallery/img3.jpeg", "Group engagement and activity identification - Setek Kiptorok Women Group, Keiyo North"));
        items.add(new GalleryItem("Set Kobkr SHG, Keiyo South,\n Elgeiyo Marakwet.", "gallery/img5.jpeg", "Group engagement and activity identification - Kapkeita Tum Gaa SHG, Keiyo South, Elgeiyo Marakwet"));
        items.add(new GalleryItem("Set Up Empowerment SHG, Keiyo North,\n Elgeiyo Marakwet", "gallery/img4.jpeg", "Group engagement and activity identification - Set Up Empowerment SHG, Keiyo North, Elgeiyo Marakwet"));
        items.add(new GalleryItem("Songeto Comprehensive School farm,\n Keiyo North, Elgeiyo Marakwet", "gallery/img9.jpeg", "Group engagement and activity identification - Anyiny Tai Songeto Women Group, Keiyo North, Elgeiyo Marakwet"));
        items.add(new GalleryItem("Set Up Empowerment SHG, Keiyo North, Elgeiyo Marakwet", "gallery/img_1.png", "Group engagement and activity identification - Set Up Empowerment SHG, Keiyo North, Elgeiyo Marakwet"));
        items.add(new GalleryItem("Elgeiyo Marakwet County", "gallery/img.png", "Group identification in Keiyo North, Elgeiyo Marakwet County, December 2024."));
        return items;
    }

    private void listAssetsInImagesFolder() {
        try {
            String[] files = getAssets().list("gallery");
            if (files == null || files.length == 0) {
                Log.e(TAG, "No files found in assets/gallery folder!");
            } else {
                for (String file : files) {
                    Log.d(TAG, "- " + file);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error accessing assets folder", e);
        }
    }
}
