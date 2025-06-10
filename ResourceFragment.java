package com.Joseph.WEE_GEnder_Tracker;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class ResourceFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_resources_fragment, container, false);

        CardView cardebaslineservey = view.findViewById(R.id.cardBaselineSurvey);
        CardView cardGenderpolicies = view.findViewById(R.id.cardGenderpolicies);
        CardView cardGallary = view.findViewById(R.id.cardGallary);
        CardView cardVideoLibrary = view.findViewById(R.id.cardVideoLibrary);


        // Set up adapter for fragments

       cardebaslineservey.setOnClickListener(view1 -> startActivity(new Intent(requireContext(), SurveyRecordsActivity.class)));
        cardGenderpolicies.setOnClickListener(view1 -> startActivity(new Intent(requireContext(), GenderPoliciesActivity.class)));
        cardGallary.setOnClickListener(view1 -> startActivity(new Intent(requireContext(), GallaryActivity.class)));
        cardVideoLibrary .setOnClickListener(view1 -> startActivity(new Intent(requireContext(), VideoActivity.class)));


        //ADS

        AdView adView = view.findViewById(R.id.adView1);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        return view;
    }
}
