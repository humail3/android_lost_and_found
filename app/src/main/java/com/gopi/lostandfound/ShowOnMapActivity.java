package com.gopi.lostandfound;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gopi.lostandfound.services.Item;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShowOnMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<Item> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_on_map);

        // Get the items from the intent
        Intent intent = getIntent();
        itemList = (ArrayList<Item>) intent.getSerializableExtra("itemList");

        // Initialize the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add markers for each item
        if (itemList != null) {
            for (Item item : itemList) {
                LatLng location = getLocationFromAddress(item.getLocation());
                if (location != null) {
                    mMap.addMarker(new MarkerOptions().position(location).title(item.getName()).snippet(item.getDescription()));
                }
            }
            // Move camera to the first item location or to a default location
            if (!itemList.isEmpty()) {
                LatLng firstLocation = getLocationFromAddress(itemList.get(0).getLocation());
                if (firstLocation != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 10));
                }
            }
        }
    }

    private LatLng getLocationFromAddress(String strAddress) {
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null || address.isEmpty()) {
                return null;
            }
            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return p1;
    }
}
