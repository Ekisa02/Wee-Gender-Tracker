package com.Joseph.WEE_GEnder_Tracker;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GenderTrackerFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_gender_tracker_fragment, container, false);

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager2 viewPager = view.findViewById(R.id.viewPager);

        // Set up adapter for fragments
        GenderTrackerAdapter adapter = new GenderTrackerAdapter(this);
        viewPager.setAdapter(adapter);

        // Connect tabs with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Baseline Survey");
                    break;
                case 1:
                    tab.setText("Gender Sensitive Indicator");
                    break;
                case 2:
                    tab.setText("End Term Data");
                    break;
            }
        }).attach();

        return view;
    }
}
