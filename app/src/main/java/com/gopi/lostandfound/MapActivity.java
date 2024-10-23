package com.gopi.lostandfound;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(latLng -> {
            // Add a marker at the selected location
            mMap.clear(); // Clear previous markers
            mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));

            // Move the camera to the selected location
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15)); // Zoom level: 15

            // Pass selected location back to calling activity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("selected_location", latLng);
            setResult(RESULT_OK, resultIntent);
        });
    }
}
