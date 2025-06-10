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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.TilesOverlay;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;


public class
NearbyUsersFragment extends Fragment {

    private static final String API_KEY = "b5586b728090ed0fdb173b8f98d9f585";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private FusedLocationProviderClient fusedLocationClient;
    private MapView mapView;

    private IMapController mapController;





    public NearbyUsersFragment() {
        // Required empty public constructor
    }

    @SuppressLint("CutPasteId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_nearby_users, container, false);




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


        // Add OpenWeatherMap tile layer
        addWeatherLayer();



        // Initialize Location Services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        requestUserLocation();



        return view;

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
                new GeoPoint(2.96360422882828, 35.4321252075548),
                new GeoPoint(2.41056520164573, 35.6506532711642),
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
                "Tiya Irrigation Scheme",
                "Nehema Women Group",
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

                } else {
                    Toast.makeText(requireContext(), "Location not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }





}






