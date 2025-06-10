package com.Joseph.WEE_GEnder_Tracker;

import com.google.firebase.firestore.GeoPoint;

public class AppUser {
    private String userId;
    private String username;
    private String email;
    private GeoPoint location;
    private String profileImageUrl;
    private long lastUpdated;

    // Required empty constructor for Firestore
    public AppUser() {}

    public AppUser(String userId, String username, String email, GeoPoint location) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.location = location;
        this.lastUpdated = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    // Helper method to get latitude
    public double getLatitude() {
        return location != null ? location.getLatitude() : 0;
    }

    // Helper method to get longitude
    public double getLongitude() {
        return location != null ? location.getLongitude() : 0;
    }

    // Method to update location
    public void updateLocation(double latitude, double longitude) {
        this.location = new GeoPoint(latitude, longitude);
        this.lastUpdated = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "AppUser{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", location=" + location +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}