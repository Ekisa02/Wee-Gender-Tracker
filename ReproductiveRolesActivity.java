package com.Joseph.WEE_GEnder_Tracker;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class ReproductiveRolesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reproductive_roles);

        // Get data from intent
        String location = getIntent().getStringExtra("location");
        String role = getIntent().getStringExtra("role");
        String category = getIntent().getStringExtra("1. Gender Roles profile");

        // Example: Display in a TextView
        TextView textView = findViewById(R.id.textView);
        textView.setText("Location: " + location +"\nCategory: "+"1.Gender Roles profile"+ "\nRole: " + role);


    }
}