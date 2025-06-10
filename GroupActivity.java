package com.Joseph.WEE_GEnder_Tracker;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.Gravity;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.view.ViewGroup;
public class GroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_group);

        // Data for Keiyo North Sub-County
        String[][] keiyoNorthData = {
                {"1", "Setek Kiptorok Women Group", "Poultry management"},
                {"2", "Chepkogin FFS", "Bee Keeping"},
                {"3", "Soiwo youth 27", "Bee Keeping"},
                {"4", "Leltai W. G", "Poultry management"},
                {"5", "Kerembet A W.G", "Poultry management"},
                {"6", "Kerembet B Youth Group", "Bee Keeping"},
                {"7", "Orapset Lead Farmers G", "Poultry management"},
                {"8", "Set up Empowerment SHG", "Poultry management"},
                {"9", "Songeto-Taunet WG", "Bee keeping/ Mango value addition"},
                {"10", "Anyiny Tai Songeto WG", "Poultry management/ Mango value chain"},
                {"11", "Songeto Judea WG", "Bee keeping/ Mango value addition"},
                {"12", "Kapsobon SHG", "Bee keeping/ Mango value addition"},
                {"13", "Chepanyiny Songeto WG", "Poultry management/ Mango value chain"},
                {"14", "Mama Mboga WG", "Bee keeping/ Mango value addition"},
                {"15", "Upper Songeto Neighbour WG", "Mango value addition"},
                {"16", "Songeto Exodus SHG", "Bee keeping"},
                {"17", "4K Club -- Songeto comprehensive school", "Living Lab for Mango value chain/ poultry and bee keeping"}
        };

        // Data for Keiyo South Sub-County
        String[][] keiyoSouthData = {
                {"18", "Kapsogom PWD SHG", "Water harvesting"},
                {"19", "Koimugul SHG", "Bee Keeping"},
                {"20", "Kapkeita Tum Gaa SHG", "Bee keeping"},
                {"21", "Set Kobor SHG", "Bee keeping"},
                {"22", "Tang'go WG", "Bee keeping"},
                {"23", "Ng'oswet CBO", "A Living Lab for Poultry production"}
        };

        // Populate Keiyo North table
        LinearLayout keiyoNorthTable = findViewById(R.id.keiyo_north_table);
        populateTable(keiyoNorthTable, keiyoNorthData);

        // Populate Keiyo South table
        LinearLayout keiyoSouthTable = findViewById(R.id.keiyo_south_table);
        populateTable(keiyoSouthTable, keiyoSouthData);
    }

    private void populateTable(LinearLayout tableLayout, String[][] data) {
        for (int i = 0; i < data.length; i++) {
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            row.setOrientation(LinearLayout.HORIZONTAL);

            // Alternate row colors for better readability
            if (i % 2 == 0) {
                row.setBackgroundColor(Color.WHITE);
            } else {
                row.setBackgroundColor(Color.parseColor("#E8F5E9"));
            }

            // Add padding and divider
            row.setPadding(8, 8, 8, 8);

            // Create and add No. column
            TextView noText = createTableCell(data[i][0], 0.2f);
            row.addView(noText);

            // Create and add Group Name column
            TextView nameText = createTableCell(data[i][1], 0.8f);
            row.addView(nameText);

            // Create and add Activity column
            TextView activityText = createTableCell(data[i][2], 1f);
            row.addView(activityText);

            tableLayout.addView(row);

            // Add divider between rows
            if (i < data.length - 1) {
                View divider = new android.view.View(this);
                divider.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        1));
                divider.setBackgroundColor(Color.parseColor("#BDBDBD"));
                tableLayout.addView(divider);
            }
        }
    }

    private TextView createTableCell(String text, float weight) {
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                weight);
        params.setMargins(4, 0, 4, 0);
        textView.setLayoutParams(params);
        textView.setText(text);
        textView.setTextColor(Color.BLACK);
        textView.setGravity(Gravity.START);
        return textView;
    }




}



