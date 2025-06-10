package com.Joseph.WEE_GEnder_Tracker;

import android.Manifest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.TilesOverlay;
import org.osmdroid.util.GeoPoint;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.osmdroid.views.MapView;


public class
HomeFragment extends Fragment {

    private static final String API_KEY = "b5586b728090ed0fdb173b8f98d9f585";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private TextView tvDate, tvLocation, tvTemperature, tvWeatherCondition, tvHumidity, tvWind, tvPressure,title_CurrentLocationWeather;
    private ImageView ivWeatherIcon;
    private FusedLocationProviderClient fusedLocationClient;
    private MapView mapView;
    private Location lastKnownLocation;
    private IMapController mapController;

    private ImageButton btn_menu;
    private TextView titleTextView;
    private final String fullText = "Your Current Location's Weather";
    private int currentIndex = 0;
    private boolean isTyping = true;
    private final long typingDelay = 100;
    private final long erasingDelay = 50;
    private Handler handler;
    private final Random random = new Random();
    private final String[] colors = {
            "#FF5722", "#4CAF50", "#2196F3",
            "#9C27B0", "#FF9800", "#795548"
    };
    private TextView nameTextView;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private final double currentLatitude = 0.0;
    private final double currentLongitude = 0.0;
    private static final String TAG = "HomeFragment";
    //private AdView adView;



    public HomeFragment() {
        // Required empty public constructor
    }

    @SuppressLint("CutPasteId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.activity_home_fragment, container, false);


    // Initialize UI Elements
    tvDate = view.findViewById(R.id.tv_date);
    tvLocation = view.findViewById(R.id.tv_location);
    tvTemperature = view.findViewById(R.id.tv_temperature);
    tvWeatherCondition = view.findViewById(R.id.tv_weather_condition);
    tvHumidity = view.findViewById(R.id.tv_humidity);
    tvWind = view.findViewById(R.id.tv_wind);
    tvPressure = view.findViewById(R.id.tv_pressure);
    ivWeatherIcon = view.findViewById(R.id.iv_weather_icon);
    CardView cardElgeyoMarakwet = view.findViewById(R.id.cardElgeyomarakwet);
    CardView cardKisumu=view.findViewById(R.id.cardKisumu);
    CardView cardTurukana=view.findViewById(R.id.cardTurukana);
    CardView cardsupport=view.findViewById(R.id.cardsupport);


        titleTextView = view.findViewById(R.id.title_CurrentLocationWeather);
        handler = new Handler();
        startAnimation();

    // Initialize OSMDroid configuration
        Configuration.getInstance().load(requireContext(), androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext()));


    // Set up OSMDroid MapView
    mapView = view.findViewById(R.id.osm_map_view);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
       mapController = mapView.getController();

        // Set default view to Kenya (centered approximately on Kenya)
        GeoPoint kenyaCenter = new GeoPoint(0.0236, 37.9062); // Approximate center of Kenya
        mapController.setCenter(kenyaCenter);
        mapController.setZoom(6.0); // Zoom level that shows most of Kenya

        mapView.invalidate(); // Force map to refresh
        // Add overlays initially



    // Add OpenWeatherMap tile layer
    addWeatherLayer();


    // Set current date


    setCurrentDate();


    // Initialize Location Services
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
    requestUserLocation();


        // Views from layout

        btn_menu = view.findViewById(R.id.btn_menu);

        // Set up the animated profile initials



    //cards declaration
        cardElgeyoMarakwet.setOnClickListener(v -> showSelectionDialogElgeiyo());
        cardKisumu.setOnClickListener(v -> showSelectionDialogKisumu());
        cardTurukana.setOnClickListener(v -> showSelectionDialogTurukana());
        cardsupport.setOnClickListener(view1 -> startActivity(new Intent(requireContext(),SettingsActivity.class)));






        btn_menu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(requireContext(), v, Gravity.END);
            popup.getMenuInflater().inflate(R.menu.menu_dropdown, popup.getMenu());

            // Force show icons
            try {
                Field[] fields = popup.getClass().getDeclaredFields();
                for (Field field : fields) {
                    if ("mPopup".equals(field.getName())) {
                        field.setAccessible(true);
                        Object menuPopupHelper = field.get(popup);
                        Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                        Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                        setForceIcons.invoke(menuPopupHelper, true);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();

                if (itemId == R.id.menu_contact_us) {
                    Toast.makeText(requireContext(), "Contact us clicked user redirected to page........!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(requireContext(), ContactUsActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                    return true;
                } else if (itemId == R.id.menu_about_us) {
                    Toast.makeText(requireContext(), "About us clicked user redirected to page........!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(requireContext(), AboutCLaSAIRActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                    return true;
                } else if (itemId == R.id.menu_account_settings) {
                    Toast.makeText(requireContext(), "Account settings clickeduser redirected to page........!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(requireContext(), SettingsActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                    return true;

                } else if (itemId == R.id.menu_logout) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                    return true;
                } else {
                    return false;
                }

            });

            popup.setOnDismissListener(menu -> {
                btn_menu.animate().rotation(0).setDuration(200);
            });
            btn_menu.animate().rotation(90).setDuration(200);

            popup.show();
        });


        // Initialize views
        nameTextView = view.findViewById(R.id.fName_and_lName);

        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        // Set temporary text while loading
        nameTextView.setText("Loading...");

        // Load user data
        loadUserData();


       //ADS

        AdView adView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);




        return view;

}

    private void startAnimation() {
        handler.postDelayed(animationRunnable, 500);
    }

    private final Runnable animationRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (titleTextView == null) return;

                if (isTyping) {
                    if (currentIndex <= fullText.length()) {
                        String displayText = fullText.substring(0, currentIndex);
                        titleTextView.setText(displayText);
                        changeTextColor();
                        currentIndex++;
                        handler.postDelayed(this, typingDelay);
                    } else {
                        isTyping = false;
                        handler.postDelayed(this, 1000);
                    }
                } else {
                    if (currentIndex > 0) {  // Changed condition to prevent negative index
                        String displayText = fullText.substring(0, currentIndex - 1);
                        titleTextView.setText(displayText);
                        changeTextColor();
                        currentIndex--;
                        handler.postDelayed(this, erasingDelay);
                    } else {
                        isTyping = true;
                        handler.postDelayed(this, 500);
                    }
                }
            } catch (Exception e) {
                // Fallback to prevent crashes
                titleTextView.setText(fullText);
                titleTextView.setTextColor(Color.BLACK);
            }
        }
    };

    private void changeTextColor() {
        try {
            if (titleTextView != null) {
                int colorIndex = random.nextInt(colors.length);
                titleTextView.setTextColor(Color.parseColor(colors[colorIndex]));
            }
        } catch (Exception e) {
            titleTextView.setTextColor(Color.BLACK);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null) {
            handler.removeCallbacks(animationRunnable);
        }
        titleTextView = null; // Prevent leaks
    }

    private void loadUserData() {
        // Check if user is logged in
        if (mAuth.getCurrentUser() == null) {
            nameTextView.setText("Please sign in");
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                if (task.getResult().exists()) {
                    String firstName = task.getResult().getString("firstName");
                    String lastName = task.getResult().getString("lastName");

                    // Check if names exist and update TextView
                    if (firstName != null && lastName != null) {
                        nameTextView.setText(firstName + " " + lastName);
                    } else {
                        nameTextView.setText("Name not available");
                    }
                } else {
                    nameTextView.setText("User data not found");
                }
            } else {
                nameTextView.setText("Error loading data");
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Refresh data when fragment becomes visible
        if (mAuth.getCurrentUser() != null) {
            loadUserData();
        }
    }
    // Method to handle Elgeiyo Marakwet
    private void showSelectionDialogElgeiyo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Location");

        String[] options = {"Keiyo North", "Keiyo South"};
        builder.setSingleChoiceItems(options, -1, (dialog, which) -> {
            String selectedOption = options[which];
            dialog.dismiss();

            // Show the second dialog: category (Activities or Group)
            showCategoryDialog(selectedOption);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Show Activities or Group selection
    private void showCategoryDialog(String location) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Category");

        String[] categories = {"Activities", "Groups"};
        builder.setSingleChoiceItems(categories, -1, (dialog, which) -> {
            String selectedCategory = categories[which];
            dialog.dismiss();

            if (selectedCategory.equals("Groups")) {
                // Navigate to the Group activity
                Intent intent = new Intent(requireContext(), GroupActivity.class);
               // intent.putExtra("location", location); // Pass location if needed
                startActivity(intent);
            } else {
                // Show the details dialog for Activities
                showDetailsDialog(location, selectedCategory);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    // Final dialog: displays dynamic content and buttons
    private void showDetailsDialog(String location, String category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(category.equals("Activities") ? "Available Activities" : "Available Groups");

        String[] itemsToDisplay;

        // Logic for what to show
        if (category.equals("Activities")) {
            if (location.equals("Keiyo North")) {
                itemsToDisplay = new String[]{"1.Water Harvesting", "2.Bee Keeping", "3.Poultry Management"};
            } else if (location.equals("Keiyo South")) {
                itemsToDisplay = new String[]{"1.Bee Keeping", "1.Poultry Management"};

            }
            else if (location.equals("Kisumu East")) {
                itemsToDisplay = new String[]{ "1.Poultry Management","2.Bee keeping"};

            }
            else if (location.equals("Nyando")) {
                itemsToDisplay = new String[]{"1.Rice-Fish paddies"};

            }
            else if (location.equals("Turkana South")) {
                itemsToDisplay = new String[]{"1.Achukule Farm Groundnuts productions","2.Nehema Women group Poultry"};

            }
            else if (location.equals("Turkana Central")) {
                itemsToDisplay = new String[]{"1.Tiya  irrigation scheme sorghum Production"};

            }
            else {
                itemsToDisplay = new String[]{"No activities found"};
            }
        } else {
            // Category is "Group"
            itemsToDisplay = new String[]{"For More informations about Groups, click View Groups Page to navigate to Groups page "};
        }

        // List view in dialog
        builder.setItems(itemsToDisplay, null); // just for display
/*
        // Buttons
       builder.setPositiveButton("View Activities Page", (dialog, which) -> {
            Intent intent = new Intent(requireContext(), ActivitiesActivity.class);
            intent.putExtra("selected_location", location);
            intent.putExtra("Select Category", category);
            startActivity(intent);
        });

        builder.setNegativeButton("View Groups Page", (dialog, which) -> {
            Intent intent = new Intent(requireContext(), GroupActivity.class);
            intent.putExtra("selected_location", location);
            intent.putExtra("Select Category", category);
            startActivity(intent);
        });

 */

        AlertDialog dialog = builder.create();
        dialog.show();
    }


//private method to handle Kisumu
private void showSelectionDialogKisumu() {
    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
    builder.setTitle("Select Location");

    // Options
    String[] options = {"Kisumu East", "Nyando"};
    builder.setSingleChoiceItems(options, -1, (dialog, which) -> {
        String selectedOption = options[which];

        // Open WorkActivity with selected location


        dialog.dismiss(); // Close dialog after selection
        showCategoryDialog(selectedOption);
    });

    // Show dialog
    AlertDialog dialog = builder.create();
    dialog.show();
}

//private method to handle Turkana
private void showSelectionDialogTurukana() {
    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
    builder.setTitle("Select Location");

    // Options
    String[] options = {"Turkana South", "Turkana Central"};
    builder.setSingleChoiceItems(options, -1, (dialog, which) -> {
        String selectedOption = options[which];

        // Open WorkActivity with selected location
        dialog.dismiss(); // Close dialog after selection
        showCategoryDialog(selectedOption);
    });

    // Show dialog
    AlertDialog dialog = builder.create();
    dialog.show();
}




    // Method to handle mapview and display weather layer
    private void addWeatherLayer() {
        OnlineTileSourceBase weatherTileSource = new OnlineTileSourceBase(
                "WeatherOverlay",
                0, 18, 256, ".png",
                new String[]{"https://tile.openweathermap.org/map/temp_new/"}) {
            @Override
            public String getTileURLString(long pMapTileIndex) {
                return getBaseUrl() + MapTileIndex.getZoom(pMapTileIndex) + "/"
                        + MapTileIndex.getX(pMapTileIndex) + "/"
                        + MapTileIndex.getY(pMapTileIndex) + ".png?appid=b5586b728090ed0fdb173b8f98d9f585";
            }
        };

        MapTileProviderBasic tileProvider = new MapTileProviderBasic(requireContext());
        tileProvider.setTileSource(weatherTileSource);

        TilesOverlay tilesOverlay = new TilesOverlay(tileProvider, requireContext());
        tilesOverlay.setLoadingBackgroundColor(android.graphics.Color.TRANSPARENT);

        mapView.getOverlays().add(tilesOverlay);

        // Add markers for all the points (coordinates)
        addMarkersToMap();

        mapView.invalidate(); // Refresh map
    }

    private void addMarkersToMap() {
        // Clear existing overlays
        mapView.getOverlays().clear();

        // Marker data
        GeoPoint[] points = {
                new GeoPoint(0.644702, 35.5180662),  // Poultry management
                new GeoPoint(0.6093856, 35.5198752),  // Poultry managemenT
                new GeoPoint(0.5931558, 35.7824782),  // Poultry management
                new GeoPoint(0.5933497, 35.5568657),  // Poultry management/ Mango value chain
                new GeoPoint(0.5927761, 35.5569621),  // Poultry management/ Mango value chain
                new GeoPoint(0.578466, 35.6496978),   // Living Lab for Mango value chain/ poultry
                new GeoPoint(0.3087793, 35.586815),    // Bee Keeping
                new GeoPoint(2.41064464017798, 35.65065804638287),
                new GeoPoint( 2.963539941900906, 35.43214666733075),
                new GeoPoint(2.38094247085346, 35.627804)
        };

        String[] titles = {
                "Setek Kiptorok Women Group",
                "Orapset Lead Farmers G",
                "Set up Empowerment SHG",
                "Anyiny Tai Songeto WG",
                "Chepanyiny Songeto WG",
                "4K Club – Songeto comprehensive school",
                "Koimugul SHG",
                "Nehema Women Group",
                "Tiya Irrigation Scheme",
                "Achukule Farm"
        };

        int[] colors = {
                Color.YELLOW,
                Color.YELLOW,
                Color.YELLOW,
                Color.YELLOW,
                Color.RED,
                Color.RED,
                Color.parseColor("#800080"),
                android.R.color.black,
                android.R.color.holo_blue_bright,
                Color.YELLOW,
        };

        // Create markers
        for (int i = 0; i < points.length; i++) {
            Marker marker = new Marker(mapView);
            marker.setPosition(points[i]);
            marker.setTitle(titles[i]);

            // Set colored icon
            Drawable markerIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_marker);
            if (markerIcon != null) {
                markerIcon.setColorFilter(colors[i], PorterDuff.Mode.SRC_IN);
                marker.setIcon(markerIcon);
            }

            marker.setOnMarkerClickListener((clickedMarker, mapView) -> {
                new AlertDialog.Builder(requireContext())
                        .setTitle(clickedMarker.getTitle())
                        .setMessage("Coordinates: \n" +
                                String.format("Lat: %.6f", clickedMarker.getPosition().getLatitude()) + "\n" +
                                String.format("Lon: %.6f", clickedMarker.getPosition().getLongitude()))
                        .setPositiveButton("Navigate", (dialog, which) -> {
                            double lat = clickedMarker.getPosition().getLatitude();
                            double lon = clickedMarker.getPosition().getLongitude();
                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lon + "&mode=d");
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                                startActivity(mapIntent);
                            } else {
                                Toast.makeText(requireContext(),
                                        "Install Google Maps for navigation",
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNeutralButton("Zoom Here", (dialog, which) -> {
                            IMapController mapController = mapView.getController();
                            mapController.animateTo(clickedMarker.getPosition());
                            mapController.setZoom(10.0);
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                return true;
            });

            mapView.getOverlays().add(marker);
        }

        // Zoom to show all markers with padding
      //BoundingBox boundingBox = BoundingBox.fromGeoPoints(Arrays.asList(points));
     //mapView.zoomToBoundingBox(boundingBox, false, 50); // 100 pixels padding

        mapView.invalidate();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(requireContext(),
                        "Location permission required for navigation", Toast.LENGTH_SHORT).show();
            }
        }
    }





    // Overriding onResume to handle map resuming
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        mapView.invalidate(); // Refresh the map view

        // Restart animation if needed
        if (title_CurrentLocationWeather != null) {
            TextAnimationUtil.stopTextAnimation(title_CurrentLocationWeather);
        }
    }

    // Overriding onPause to handle map pausing
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        // Restart animation when fragment resumes
        if (title_CurrentLocationWeather!= null && title_CurrentLocationWeather.getText().toString().isEmpty()) {
            TextAnimationUtil.animateTextWithTypingEffect(
                    title_CurrentLocationWeather,
                    "Your Current Location's Weather",
                    150L
            );
        }
    }

    // Method to get current location and set map center to the user's location
    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if (location != null) {
                double lat = location.getLatitude();
                double lon = location.getLongitude();
                Log.d("LOCATION", "Latitude: " + lat + ", Longitude: " + lon);

                GeoPoint userLocation = new GeoPoint(lat, lon);
                mapController.setCenter(userLocation);
                mapController.setZoom(6);
            } else {
                Log.e("LOCATION", "Failed to get location.");
                Toast.makeText(requireContext(), "Unable to fetch location. Ensure GPS is ON!", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(e -> {
            Log.e("LOCATION", "Error fetching location: " + e.getMessage());
        });
    }

    // Method to update map location based on user location
    private void updateMapLocation(Location location) {
        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        mapController.setCenter(geoPoint);
        mapController.setZoom(6.0); // Adjust zoom level
    }







private void setCurrentDate() {
    SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM dd", Locale.getDefault());
    String currentDate = sdf.format(new Date());
    tvDate.setText("Today,\n " + currentDate);
}

@SuppressLint("MissingPermission")
private void requestUserLocation() {
    if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        return;
    }

    fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
        @Override
        public void onSuccess(Location location) {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                fetchWeatherData(latitude, longitude);
            } else {
                Toast.makeText(requireContext(), "Location not found", Toast.LENGTH_SHORT).show();
            }
        }
    });
}

private void fetchWeatherData(double latitude, double longitude) {
    String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&units=metric&appid=" + API_KEY;

    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder().url(url).build();

    client.newCall(request).enqueue(new Callback() {

        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            e.printStackTrace();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            if (response.isSuccessful() && getActivity() != null) {
                String responseData = response.body().string();
                getActivity().runOnUiThread(() -> updateUI(responseData));
            } else if (getActivity() != null) {
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getActivity(), "Error: " + response.code() + " - " + response.message(), Toast.LENGTH_LONG).show()
                );
            }
        }
    });
}

private void updateUI(String jsonData) {
    try {
        JSONObject jsonObject = new JSONObject(jsonData);
        String cityName = jsonObject.getString("name");
        JSONObject main = jsonObject.getJSONObject("main");
        JSONObject wind = jsonObject.getJSONObject("wind");
        JSONObject weather = jsonObject.getJSONArray("weather").getJSONObject(0);

        String temperature = main.getString("temp");
        String humidity = main.getString("humidity") + "%";
        String pressure = main.getString("pressure") + " hPa";
        String windSpeed = wind.getString("speed") + " km/h";
        String weatherCondition = weather.getString("description");
        String icon = weather.getString("icon");

        // Update UI Elements
        tvLocation.setText(cityName);
        tvTemperature.setText(temperature );
        tvHumidity.setText(humidity);
        tvWind.setText(windSpeed);
        tvPressure.setText(pressure);
        tvWeatherCondition.setText(weatherCondition);

        // Load Weather Icon
        String iconUrl = "https://openweathermap.org/img/w/" + icon + ".png";
        Picasso.get().load(iconUrl).into(ivWeatherIcon);

    } catch (JSONException e) {
        e.printStackTrace();
    }
}

}






