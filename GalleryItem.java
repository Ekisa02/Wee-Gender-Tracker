package com.Joseph.WEE_GEnder_Tracker;

public class GalleryItem {
    private final String title;
    private final String assetPath;
    private final String description;

    public GalleryItem(String title, String assetPath, String description) {
        this.title = title;
        this.assetPath = assetPath;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getAssetPath() {
        return assetPath;
    }

    public String getDescription() {
        return description;
    }
}