package com.gopi.lostandfound;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.gopi.lostandfound.authenticationactivities.LoginActivity;
import com.gopi.lostandfound.services.DBHelper;
import com.gopi.lostandfound.services.Item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button CreateAdvertBTN, ShowItemsBTN, ShowOnMapBTN,logoutBTN;
    private DBHelper dbHelper;
    private List<Item> itemList; // Change the type to List<Item>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DBHelper(this);

        CreateAdvertBTN = findViewById(R.id.BTNCreateAdvert);
        ShowItemsBTN = findViewById(R.id.BTNShowItems);
        ShowOnMapBTN = findViewById(R.id.BTNShowOnMap_main);
        logoutBTN = findViewById(R.id.BTNlogout);

        // Set click listeners for buttons
        CreateAdvertBTN.setOnClickListener(view -> {
            // Navigate to CreateAdvertActivity when CreateAdvertBTN is clicked
            Intent intent = new Intent(MainActivity.this, CreateAdvertActivity.class);
            startActivity(intent);
        });

        ShowItemsBTN.setOnClickListener(view -> {
            // Retrieve the list of items from the database
            itemList = dbHelper.getAllItems();

            // Navigate to ShowItemsActivity when ShowItemsBTN is clicked
            Intent intent = new Intent(MainActivity.this, ShowItemsActivity.class);
            startActivity(intent);
        });

        ShowOnMapBTN.setOnClickListener(view -> {
            // Pass the list of items to ShowOnMapActivity when ShowOnMapBTN is clicked
            Intent intent = new Intent(MainActivity.this, ShowOnMapActivity.class);
            intent.putExtra("itemList", (Serializable)itemList);
            startActivity(intent);
        });

        logoutBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle logout action
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish(); // Close MainActivity after logout
            }
        });

    }
}
