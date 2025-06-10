package com.Joseph.WEE_GEnder_Tracker;

import android.location.Location;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

public class UserLocationManager {
    private static final String COLLECTION_USERS = "users";
    private final FirebaseFirestore db;
    private final String userId;

    public UserLocationManager(String userId) {
        this.db = FirebaseFirestore.getInstance();
        this.userId = userId;
    }

    public void updateUserLocation(Location location) {
        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());

        db.collection(COLLECTION_USERS)
                .document(userId)
                .update(
                        "location", geoPoint,
                        "lastUpdated", System.currentTimeMillis()
                )
                .addOnSuccessListener(aVoid -> {
                    // Location updated successfully
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }
}