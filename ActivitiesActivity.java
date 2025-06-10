package com.Joseph.WEE_GEnder_Tracker;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class ActivitiesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_activities);
        String location = getIntent().getStringExtra("selected_location");
        String category = getIntent().getStringExtra("Select Category");

        // Example: Display in a TextView
        TextView textView = findViewById(R.id.textView);
        textView.setText("Location: " + location + "\ncategory: " + category);
    }
}