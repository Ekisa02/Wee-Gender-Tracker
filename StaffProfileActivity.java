package com.Joseph.WEE_GEnder_Tracker;

import android.graphics.Color;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

public class StaffProfileActivity extends AppCompatActivity {

    // Modern pastel color palette
    private final int[] ROW_COLORS = {
            Color.parseColor("#FFEEF7"),  // Light pink
            Color.parseColor("#F0F7FF"),  // Light blue
            Color.parseColor("#F5FFEE"),  // Light green
            Color.parseColor("#FFF5EE")   // Light orange
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_profile);

        LinearLayout staffContainer = findViewById(R.id.staffContainer);
        List<StaffProfile> staffList = getStaffProfiles();

        // Load animations
        Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        for (int i = 0; i < staffList.size(); i++) {
            StaffProfile staff = staffList.get(i);

            // 1. Create Card Container
            CardView cardView = new CardView(this);
            cardView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            cardView.setCardElevation(12);
            cardView.setRadius(20);
            cardView.setCardBackgroundColor(ROW_COLORS[i % ROW_COLORS.length]);
            cardView.setContentPadding(0, 0, 0, 0);
            cardView.setPreventCornerOverlap(true);

            // 2. Main Content Layout
            LinearLayout cardContent = new LinearLayout(this);
            cardContent.setOrientation(LinearLayout.VERTICAL);
            cardContent.setPadding(0, 0, 0, 0);

            // 3. Header Row (Number + Photo + Name)
            LinearLayout headerRow = new LinearLayout(this);
            headerRow.setOrientation(LinearLayout.HORIZONTAL);
            headerRow.setPadding(16, 24, 16, 24);
            headerRow.setWeightSum(4);

            // 3.1 Number Column
            TextView tvNumber = createStyledTextView(staff.getNumber(), 0.5f, true);
            tvNumber.setTextColor(Color.parseColor("#7B1FA2")); // Purple
            tvNumber.setTextSize(16);
            headerRow.addView(tvNumber);

            // 3.2 Photo Column
            ImageView imgProfile = createProfileImage(staff.getPhotoUrl());
            headerRow.addView(imgProfile);

            // 3.3 Name Column
            TextView tvName = createStyledTextView(staff.getName(), 2.5f, false);
            styleNameTextView(tvName);
            headerRow.addView(tvName);

            // 4. Details Section
            TextView tvDetails = createDetailsTextView(staff.getFullProfile());

            // 5. Set Click Listener
            tvName.setOnClickListener(v -> toggleProfileDetails(tvDetails, tvName, slideDown, fadeIn));

            // 6. Assemble Card
            cardContent.addView(headerRow);
            cardContent.addView(tvDetails);
            cardView.addView(cardContent);
            staffContainer.addView(cardView);
        }
    }

    private ImageView createProfileImage(String photoUrl) {
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        params.setMargins(8, 0, 8, 0);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setAdjustViewBounds(true);
        imageView.setMaxHeight(dpToPx(100));
        imageView.setBackgroundResource(R.drawable.circle_border_accent);

        int imgRes = getResources().getIdentifier(photoUrl, "drawable", getPackageName());
        imageView.setImageResource(imgRes != 0 ? imgRes : R.drawable.ic_person_modern);

        return imageView;
    }

    private TextView createStyledTextView(String text, float weight, boolean center) {
        TextView tv = new TextView(this);
        tv.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, weight));
        tv.setText(text);
        tv.setPadding(8, 8, 8, 8);
        tv.setTextSize(16);
        if (center) tv.setGravity(Gravity.CENTER);
        return tv;
    }

    private void styleNameTextView(TextView tvName) {
        tvName.setTextColor(Color.parseColor("#2E7D32")); // Dark green
        tvName.setTextSize(18);
        tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more, 0);
        tvName.setCompoundDrawablePadding(16);
    }

    private TextView createDetailsTextView(String text) {
        TextView tv = new TextView(this);
        tv.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        tv.setText(text);
        tv.setAutoLinkMask(Linkify.ALL);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        tv.setTextColor(Color.parseColor("#424242")); // Dark gray
        tv.setTextSize(14);
        tv.setPadding(32, 8, 32, 24);
        tv.setVisibility(View.GONE);
        tv.setLinkTextColor(ContextCompat.getColor(this, R.color.accent_purple));
        return tv;
    }

    private void toggleProfileDetails(TextView details, TextView name,
                                      Animation slideDown, Animation fadeIn) {
        if (details.getVisibility() == View.VISIBLE) {
            details.startAnimation(fadeIn);
            details.setVisibility(View.GONE);
            name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more, 0);
        } else {
            details.setVisibility(View.VISIBLE);
            details.startAnimation(slideDown);
            name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_less, 0);
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
    private List<StaffProfile> getStaffProfiles() {
        List<StaffProfile> staffList = new ArrayList<>();
        // 3. Dr. Kenneth Opiyo Odhiambo
        staffList.add(new StaffProfile(
                "1",
                "profile3",
                "Army Pennington",
                "Investment Owner Gates Foundation",
                "Email: amy.pennington@gatesfoundation.org"

        ));
        // 3. Dr. Kenneth Opiyo Odhiambo
        staffList.add(new StaffProfile(
                "2",
                "profile3",
                "Leesa Shrader",
                "Investment Coodinater Gates foundation",
                " " +
                        "Email:leesa.shrader@gatesfoundation.org"

        ));



        // 3. Dr. Kenneth Opiyo Odhiambo
        staffList.add(new StaffProfile(
                "2",
                "mipmap/daktari",
                "Dr. Kenneth Opiyo Odhiambo\nPrimary Contact",
                "Lecturer of Tropical Forest Biology",
                "Dr Odhiambo is a Lecturer of Tropical Forest Biology and Silviculture in the School of " +
                        "Environmental Sciences and Natural Resource Management. He formerly served as Chairperson-" +
                        "Departmental Graduate Committee (DGC), and Head of Department of Forestry and Wood Science. " +
                        "He holds a PhD in Zoology (Entomology) from University of Eldoret (Kenya). He is the project " +
                        "lead of Grand Challenges C-LaSAIR project, an initiative of the Bill & Melinda Gates " +
                        "Foundation, to design and implement scalable, climate-smart, labor-saving agricultural " +
                        "practices and technologies to improve women's livelihoods in drylands of three counties in Kenya.\n\n" +
                        "University of Eldoret/ Dept. of Forestry and Wood Science\n\n" +
                        "Email: Kodhiambo@uoeld.ac.ke"

        ));

        // 1. Prof. Julius Ochuodho
        staffList.add(new StaffProfile(
                "4",
                "",
                "Prof. Julius Ochuodho",
                "Professor of Agriculture (Seed Science)",
                "Prof. Julius Onyango Ochuodho is a Professor of Agriculture (Seed Science) at the " +
                        "University of Eldoret (UoE), Kenya, specialized in Plant Pathology and Seed Science & " +
                        "Technology. He holds a PhD. in Plant Science (University of KwaZulu Natal, South Africa). " +
                        "He is a former Dean, School of Agriculture and Biotechnology (UoE) and has supervised to " +
                        "graduation about 20 Ph.D. and 40 MSc to completion, published more than 70 papers in " +
                        "refereed journals and 4 book chapters, examined about 40 Ph.D. and Masters Theses. He has " +
                        "been involved in many collaborative research projects including RUFORUM CARP and " +
                        "Intra-Africa academic mobility (SCIFSA) that have brought into the University more than " +
                        "3 million Euros since 2006. Currently, Prof Ochuodho is the Director, Resource " +
                        "Mobilization and Strategic Initiatives, Lead scientist at the Outreach centre\n\n" +
                        "University of Eldoret/ Dept. of Seed, Crop and Horticultural Sciences; and Coordinator, " +
                        "ENABLE Youth Kenya and RUFORUM TAGDev project.\n\n" +
                        "Email: juliusochuodho@uoeld.ac.ke"
        ));
        // 4. Prof. Boaz Kaunda-Arara
        staffList.add(new StaffProfile(
                "5",
                "profile_placeholder",
                "Prof. Boaz Kaunda-Arara",
                "University of Eldoret/ Dept. of Fisheries",
                "University of Eldoret/ Dept. of Fisheries and Aquatic Science\n\n" +
                        "Email: b_kaunda@yahoo.com"
        ));


        // 2. Ms. Jennifer Rono
        staffList.add(new StaffProfile(
                "6",
                "mipmap/madam",
                "Ms. Jennifer Rono",
                "Lecturer in Agroforestry/Forestry",
                "Ms. Rono is a lecturer in Agroforestry/Forestry and currently pursuing studies in " +
                        "Environmental Planning and Management. She is an Expert in moodle courseware and materials " +
                        "development.\n\n" +
                        "University of Eldoret/ Dept. of Forestry and Wood Science\n\n" +
                        "Email: jeniffer.rono@uoeld.ac.ke"
        ));




        // 5. Dr. Frank Onderi Masese
        staffList.add(new StaffProfile(
                "7",
                "profile_placeholder",
                "Dr. Frank Onderi Masese",
                "University of Eldoret/ Dept. of Fisheries",
                "University of Eldoret/ Dept. of Fisheries and Aquatic Science\n\n" +
                        "Email: f.masese@gmail.com"
        ));

        // 6. Dr. Geraldine K. Matolla
        staffList.add(new StaffProfile(
                "8",
                "profile_placeholder",
                "Dr. Geraldine K. Matolla",
                "University of Eldoret/ Dept. of Fisheries",
                "University of Eldoret/ Dept. of Fisheries and Aquatic Science\n\n" +
                        "Email: gmatolla@uoeld.ac.ke"
        ));

        // 7. Dr. Abigael Otinga
        staffList.add(new StaffProfile(
                "9",
                "profile_placeholder",
                "Dr. Abigael Otinga",
                "University of Eldoret/ Dept. of Soil Science",
                "University of Eldoret/ Dept. of Soil Science\n\n" +
                        "Email: abigaelno@uoeld.ac.ke"
        ));

        // 8. Ms. Rosemary Bargerei
        staffList.add(new StaffProfile(
                "10",
                "profile_placeholder",
                "Ms. Rosemary Bargerei",
                "University of Eldoret/ Dept. of Forestry",
                "University of Eldoret/ Dept. of Forestry and Wood Science\n\n" +
                        "Email: rosemary.bargerei@uoeld.ac.ke"
        ));

        // 9. Mr. Festus Napokol Lolimo
        staffList.add(new StaffProfile(
                "11",
                "mipmap/lolimo",
                "Mr. Festus Napokol Lolimo",
                "University of Eldoret/ Dept. of Forestry",
                "University of Eldoret/ Dept. of Forestry and Wood Science\n\n" +
                        "Email: lolimofestus@uoeld.ac.ke"
        ));

        // 10. Mr. Augustin Kai
        staffList.add(new StaffProfile(
                "12",
                "profile_placeholder",
                "Mr. Augustin Kai",
                "Lotus Kenya Action for Development (LOKADO)",
                "Lotus Kenya Action for Development (LOKADO), an NGO, Turkana County\n\n" +
                        "Phone: 0722652268\n\n" +
                        "Email: akailopie@gmail.com"
        ));

        // 11. Ms. Nancy Najula
        staffList.add(new StaffProfile(
                "13",
                "profile_placeholder",
                "Ms. Nancy Najula",
                "Lotus Kenya Action for Development (LOKADO)",
                "Lotus Kenya Action for Development (LOKADO), an NGO, Turkana County\n\n" +
                        "Phone: 0723133545\n\n" +
                        "Email: nancynajula@gmail.com"
        ));

        // 12. Ms. Betty Sike
        staffList.add(new StaffProfile(
                "14",
                "profile_placeholder",
                "Ms. Betty Sike",
                "Turkana Ecogreen CBO",
                "Turkana Ecogreen CBO\n\n" +
                        "Phone: 0711500142\n\n" +
                        "Email: bettysike@gmail.com"
        ));

        // 13. Dr. Catherine Sang
        staffList.add(new StaffProfile(
                "15",
                "profile_placeholder",
                "Dr. Catherine Sang",
                "Environmental Studies Expert",
                "Dr Sang holds a BED(Arts), M. PHIL in Environmental Studies (Environmental Information " +
                        "Systems), D. PHIL in Environmental Studies (Environmental Information Systems). She is an " +
                        "expert in GIS and remote sensing, water resources management, EIA, climate change " +
                        "mitigation and adaptation.\n\n" +
                        "University of Eldoret\n\n" +
                        "Email: catherine.sang@uoeld.ac.ke"
        ));

        // 14. Mr. Ismael Kemboi
        staffList.add(new StaffProfile(
                "16",
                "profile_placeholder",
                "Mr. Ismael Kemboi",
                "Ngoswet Community Based Organization",
                "Ngoswet Community Based Organization (CBO), P.O. Box 45 code 30129 Chepkorio " +
                        "(Elgeyo Marakwet)\n\n" +
                        "Phone: 0706944366\n\n" +
                        "Email: Kemboiismael04@gmail.com"
        ));

        // 15. Mr. Mathew Korir
        staffList.add(new StaffProfile(
                "17",
                "profile_placeholder",
                "Mr. Mathew Korir",
                "World Vision - Elgeyio North",
                "World Vision - Elgeyio North\n\n" +
                        "Phone: 0723889631\n\n" +
                        "Email: mathewkorir1993@gmail.com"
        ));

        // 16. Mr. Cosmas Kinda
        staffList.add(new StaffProfile(
                "18",
                "profile_placeholder",
                "Mr. Cosmas Kinda",
                "Project Coordinator/ World Vision",
                "Project Coordinator/ World Vision - Elgeyio South\n\n" +
                        "Phone: 0725956589\n\n" +
                        "Email: Cosmas_kinda@wvi.org"
        ));

        return staffList;
    }

    private static class StaffProfile {
        private final String number;
        private final String photoUrl;
        private final String name;
        private final String shortProfile;
        private final String fullProfile;

        public StaffProfile(String number, String photoUrl, String name,
                            String shortProfile, String fullProfile) {
            this.number = number;
            this.photoUrl = photoUrl;
            this.name = name;
            this.shortProfile = shortProfile;
            this.fullProfile = fullProfile;
        }

        // Getters
        public String getNumber() { return number; }
        public String getPhotoUrl() { return photoUrl; }
        public String getName() { return name; }
        public String getShortProfile() { return shortProfile; }
        public String getFullProfile() { return fullProfile; }
    }
}