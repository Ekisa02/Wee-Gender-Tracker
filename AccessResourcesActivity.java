package com.Joseph.WEE_GEnder_Tracker;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class AccessResourcesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_access_resources);

        String location = getIntent().getStringExtra("location");

        String category = getIntent().getStringExtra("category");

        // Example: Display in a TextView
        TextView textView = findViewById(R.id.textView);
        textView.setText("Location: " + location + "\ncategory: " + category);

    }

}