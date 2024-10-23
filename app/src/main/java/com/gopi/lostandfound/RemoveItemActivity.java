package com.gopi.lostandfound;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.gopi.lostandfound.services.DBHelper;
import com.gopi.lostandfound.services.Item;

public class RemoveItemActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private Item currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_remove_item);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DBHelper(this);

        // Retrieve item details from the intent
        Intent intent = getIntent();
        if (intent != null) {
            long itemId = intent.getLongExtra("id", -1); // Retrieve item ID
            String type = intent.getStringExtra("type");
            String name = intent.getStringExtra("name");
            String phone = intent.getStringExtra("phone");
            String description = intent.getStringExtra("description");
            String date = intent.getStringExtra("date");
            String location = intent.getStringExtra("location");

            // Set the item details in the TextViews
            currentItem = new Item(type, name, phone, description, date, location);
            currentItem.setId(itemId); // Set the ID of the current item

            // Bind the TextViews with the item details
            TextView itemTypeTextView = findViewById(R.id.TVItemType_remove);
            itemTypeTextView.setText(type + " ");

            TextView itemNameTextView = findViewById(R.id.TVItemName_remove);
            itemNameTextView.setText(name);

            TextView phoneTextView = findViewById(R.id.TVPhone_remove);
            phoneTextView.setText(phone);

            TextView dateTextView = findViewById(R.id.TVDate_remove);
            dateTextView.setText(date);

            TextView locationTextView = findViewById(R.id.TVLoaction_remove);
            locationTextView.setText(location);

            TextView descriptionTextView = findViewById(R.id.TVDescription_remove);
            descriptionTextView.setText(description);
        }

        // Setup Remove button click listener
        Button removeButton = findViewById(R.id.BTNRemove);
        removeButton.setOnClickListener(v -> removeItem());
    }

    // Method to remove item from the database
    private void removeItem() {
        if (currentItem != null) {
            long itemId = currentItem.getId(); // Get the ID of the current item
            dbHelper.deleteItem(itemId); // Call method to delete item from database
            Toast.makeText(this, "Item removed successfully", Toast.LENGTH_SHORT).show();

            // Send back the deleted item ID to the ShowItemsActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("deletedItemId", itemId);
            setResult(RESULT_OK, resultIntent);

            finish(); // Close this activity after removing the item
        } else {
            Log.e("RemoveItemActivity", "Current item is null"); // Add error logging
        }
    }
}
