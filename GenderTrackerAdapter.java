package com.Joseph.WEE_GEnder_Tracker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class GenderTrackerAdapter extends FragmentStateAdapter {
    public GenderTrackerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new BaselineSurveyFragment();
            case 1:
                return new GenderSensitiveIndicatorFragment();
            case 2:
                return new EndTermDataFragment();
            default:
                return new BaselineSurveyFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Three tabs
    }
}
