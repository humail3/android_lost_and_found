package com.gopi.lostandfound;

import static androidx.core.location.LocationManagerCompat.getCurrentLocation;

import android.Manifest;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.gopi.lostandfound.services.DBHelper;
import com.gopi.lostandfound.services.Item;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.model.LatLng;

public class CreateAdvertActivity extends AppCompatActivity {

    private RadioGroup radioGroupPostType;
    private EditText editTextName, editTextPhone, editTextDescription, editTextDate, editTextLocation;
    private Button btnSave, getCurrentLocationButton;
    private DBHelper dbHelper;
    int year, month, day;
    String selectedDateSTR;
    private static final int REQUEST_CODE_MAP = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100; // Define request code
    private FusedLocationProviderClient fusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_advert);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        radioGroupPostType = findViewById(R.id.RGPostType);
        editTextName = findViewById(R.id.ETName);
        editTextPhone = findViewById(R.id.ETPhone);
        editTextDescription = findViewById(R.id.ETDescription);
        editTextDate = findViewById(R.id.ETDate);
        editTextLocation = findViewById(R.id.ETLocation);
        btnSave = findViewById(R.id.BTNSave);
        getCurrentLocationButton = findViewById(R.id.BTNGetCurrentLocation);

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = sdf.format(new Date());
        editTextDate.setText(currentDate);
        editTextDate.setOnClickListener(v -> showDatePickerDialog());

        dbHelper = new DBHelper(this);

        btnSave.setOnClickListener(v -> saveItem());

        editTextLocation.setOnClickListener(v -> {
            // Start MapActivity to select location
            Intent intent = new Intent(CreateAdvertActivity.this, MapActivity.class);
            startActivityForResult(intent, REQUEST_CODE_MAP);
        });

        getCurrentLocationButton.setOnClickListener(v -> getCurrentLocation());
    }

//    private void getCurrentLocation() {
//        // Check location permission
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // Request location permission if not granted
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
//            return;
//        }
//
//        // Get the last known location
//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if (locationManager != null) {
//            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            if (location != null) {
//                // Convert location to address
//                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//                try {
//                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//                    if (addresses != null && addresses.size() > 0) {
//                        Address address = addresses.get(0);
//                        String locationName = address.getAddressLine(0); // Get the first line of the address
//                        // Update the location EditText with the current location name
//                        editTextLocation.setText(locationName);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
private void getCurrentLocation() {
    // Check location permission
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        // Request location permission if not granted
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        return;
    }

    // Request location updates
    LocationRequest locationRequest = LocationRequest.create();
    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    locationRequest.setInterval(10000); // Update interval in milliseconds
    locationRequest.setFastestInterval(5000); // Fastest update interval in milliseconds

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
                // Update the location EditText with the current location name
                updateLocation(location);
            }
        }
    };

    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
}

    private void updateLocation(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                String locationName = address.getAddressLine(0); // Get the first line of the address
                // Update the location EditText with the current location name
                editTextLocation.setText(locationName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // If location permission is granted, get the current location
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MAP && resultCode == RESULT_OK) {
            // Get the selected location from the intent data
            if (data != null && data.hasExtra("selected_location")) {
                LatLng selectedLocation = data.getParcelableExtra("selected_location");
                if (selectedLocation != null) {
                    // Convert LatLng to address
                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(selectedLocation.latitude, selectedLocation.longitude, 1);
                        if (addresses != null && addresses.size() > 0) {
                            Address address = addresses.get(0);
                            String locationName = address.getAddressLine(0); // Get the first line of the address
                            // Update the location EditText with the selected location name
                            editTextLocation.setText(locationName);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void saveItem() {
        String type = radioGroupPostType.getCheckedRadioButtonId() == R.id.RBLost ? "Lost" : "Found";
        String name = editTextName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String date = editTextDate.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || description.isEmpty() || date.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtain the current location
        String currentLocation = editTextLocation.getText().toString().trim();

        // Check if the current location is available
        if (currentLocation.isEmpty()) {
            // If the current location is not available, show a message
            Toast.makeText(this, "Current location not available. Please select a location.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Store the item with the current location
        Item item = new Item(type, name, phone, description, date, currentLocation);
        dbHelper.addItem(item);

        editTextName.setText("");
        editTextPhone.setText("");
        editTextDescription.setText("");
        editTextDate.setText("");
        editTextLocation.setText("");

        Toast.makeText(this, "Item saved successfully", Toast.LENGTH_SHORT).show();
    }

//    private void saveItem() {
//        String type = radioGroupPostType.getCheckedRadioButtonId() == R.id.RBLost ? "Lost" : "Found";
//        String name = editTextName.getText().toString().trim();
//        String phone = editTextPhone.getText().toString().trim();
//        String description = editTextDescription.getText().toString().trim();
//        String date = editTextDate.getText().toString().trim();
//        String location = editTextLocation.getText().toString().trim();
//
//        if (name.isEmpty() || phone.isEmpty() || description.isEmpty() || date.isEmpty() || location.isEmpty()) {
//            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        Item item = new Item(type, name, phone, description, date, location);
//        dbHelper.addItem(item);
//
//        editTextName.setText("");
//        editTextPhone.setText("");
//        editTextDescription.setText("");
//        editTextDate.setText("");
//        editTextLocation.setText("");
//
//        Toast.makeText(this, "Item saved successfully", Toast.LENGTH_SHORT).show();
//    }

    public void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;
            selectedDateSTR = day + "-" + (month + 1) + "-" + year;
            editTextDate.setText(selectedDateSTR);
        }, year, month, day);
        datePickerDialog.show();
    }
}