package com.Joseph.WEE_GEnder_Tracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class EndTermDataFragment extends Fragment {
    CardView cardElgeiyogender,cardKisumugender,cardTurukanagender;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_end_term_data_fragment, container, false);

        // Initialize CardView and set click listener
        cardElgeiyogender = view.findViewById(R.id.cardElgeyomarakwetgenger);
        cardKisumugender = view.findViewById(R.id.cardKisumugender);
        cardTurukanagender= view.findViewById(R.id.cardTurukanagender);
        //declaration
        cardElgeiyogender.setOnClickListener(v -> showSelectionDialogElgeiyogender());
        cardKisumugender.setOnClickListener(v -> showSelectionDialogcardKisumugender());
        cardTurukanagender.setOnClickListener(v -> showSelectionDialogcardTurukanagender());


        return view;
    }

    private void showSelectionDialogElgeiyogender() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Location");

        // First-level options (Location selection)
        String[] options = {"Keiyo North", "Keiyo South"};
        builder.setSingleChoiceItems(options, -1, (dialog, which) -> {
            String selectedLocation = options[which];
            dialog.dismiss(); // Close location selection dialog

            // Show second dialog with category options
            showCategorySelectionDialog(selectedLocation);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showSelectionDialogcardKisumugender() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Location");

        // First-level options (Location selection)
        String[] options = {"Kisumu East", "Nyando"};
        builder.setSingleChoiceItems(options, -1, (dialog, which) -> {
            String selectedLocation = options[which];
            dialog.dismiss(); // Close location selection dialog

            // Show second dialog with category options
            showCategorySelectionDialog(selectedLocation);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void showSelectionDialogcardTurukanagender() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Location");

        // First-level options (Location selection)
        String[] options = {"Turkana South", "Turkana Central"};
        builder.setSingleChoiceItems(options, -1, (dialog, which) -> {
            String selectedLocation = options[which];
            dialog.dismiss(); // Close location selection dialog

            // Show second dialog with category options
            showCategorySelectionDialog(selectedLocation);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void showCategorySelectionDialog(String selectedLocation) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Category for " + selectedLocation);

        // Second-level options (Category selection)
        String[] categories = {
                "1. Gender Roles profile",
                "2. Access to resources",
                "3. Influencing factors",
                "4. Capacity and vulnerability analysis",
                "5. Women's Resilience Index"
        };

        builder.setItems(categories, (dialog, which) -> {
            String selectedCategory = categories[which];
            // Handle category selection
            handleCategorySelection(selectedLocation, selectedCategory);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void handleCategorySelection(String location, String category) {
        if (category.contains("Gender Roles profile")) {
            showGenderRolesDialog(location);
        } else {
            // Redirect to respective pages for other categories
            navigateToCategoryPage(location, category);
        }
    }
    private void showGenderRolesDialog(String selectedLocation) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Gender Role for " + selectedLocation);

        // Options for Gender Roles Profile
        String[] genderRoles = {"1. Reproductive Roles", "2. Productive Roles", "3. Community Roles"};

        builder.setItems(genderRoles, (dialog, which) -> {
            String selectedRole = genderRoles[which];

            // Navigate to the corresponding activity
            navigateToGenderRolePage(selectedLocation, selectedRole);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void navigateToCategoryPage(String location, String category) {
        Intent intent;

        if (category.contains("Access to resources")) {
            intent = new Intent(requireContext(), AccessResourcesActivity.class);
        } else if (category.contains("Influencing factors")) {
            intent = new Intent(requireContext(), InfluencingFactorsActivity.class);
        } else if (category.contains("Capacity and vulnerability analysis")) {
            intent = new Intent(requireContext(), CapacityVulnerabilityActivity.class);
        } else if (category.contains("Women's Resilience Index")) {
            intent = new Intent(requireContext(), WomensResilienceActivity.class);
        } else {
            Toast.makeText(requireContext(), "Invalid selection!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Pass location and category data to the activity
        intent.putExtra("location", location);
        intent.putExtra("category", category);
        startActivity(intent);
    }

    private void navigateToGenderRolePage(String location, String role) {
        Intent intent;

        if (role.contains("Reproductive Roles")) {
            intent = new Intent(requireContext(), ReproductiveRolesActivity.class);
        } else if (role.contains("Productive Roles")) {
            intent = new Intent(requireContext(), ProductiveRolesActivity.class);
        } else if (role.contains("Community Roles")) {
            intent = new Intent(requireContext(), CommunityRolesActivity.class);
        } else {
            Toast.makeText(requireContext(), "Invalid selection!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Pass location and role data to the activity
        intent.putExtra("location", location);
        intent.putExtra("role", role);
        startActivity(intent);
    }

}
