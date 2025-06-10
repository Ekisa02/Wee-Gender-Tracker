package com.Joseph.WEE_GEnder_Tracker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FragmentAdapter extends FragmentStateAdapter {

    public FragmentAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new QASectionFragment();
            case 1:
                return new NearbyUsersFragment();
            default:
                return new QASectionFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
