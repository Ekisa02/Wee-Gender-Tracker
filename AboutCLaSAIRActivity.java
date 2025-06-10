package com.Joseph.WEE_GEnder_Tracker;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;



public class AboutCLaSAIRActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_cla_sairactivity);

        // Make links clickable
        TextView linkTextView = findViewById(R.id.grandChallengesLink);
        linkTextView.setMovementMethod(LinkMovementMethod.getInstance());

        TextView emailTextView = findViewById(R.id.contactEmail);
        emailTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }
}