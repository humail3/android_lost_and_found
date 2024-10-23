package com.gopi.lostandfound;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gopi.lostandfound.services.DBHelper;
import com.gopi.lostandfound.services.Item;
import com.gopi.lostandfound.services.ItemAdapter;
import com.gopi.lostandfound.services.ItemClickListener;

import java.util.ArrayList;

public class ShowItemsActivity extends AppCompatActivity implements ItemClickListener {

    private RecyclerView recyclerView;
    private DBHelper dbHelper;
    private ItemAdapter itemAdapter;
    private ArrayList<Item> itemList;
    private static final int REQUEST_CODE_REMOVE_ITEM = 1001; // Choose any integer value

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_items);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.RVItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize DBHelper
        dbHelper = new DBHelper(this);

        // Retrieve items from the database
        Log.d("ShowItemsActivity", "Retrieving items from the database...");
        itemList = new ArrayList<>();
        try {
            itemList.addAll(dbHelper.getAllItems());
            Log.d("ShowItemsActivity", "Number of items retrieved: " + itemList.size());
        } catch (Exception e) {
            // Handle database errors gracefully
            e.printStackTrace();
            // Show an error message to the user or log the error for debugging
        }

        // Initialize ItemAdapter
        itemAdapter = new ItemAdapter(this, itemList, this);
        recyclerView.setAdapter(itemAdapter);
    }

    @Override
    public void onItemClick(int position) {
        // Handle item click, e.g., navigate to details screen
        Item item = itemAdapter.getItem(position);
        Intent intent = new Intent(this, RemoveItemActivity.class);
        // Pass item details to the details screen
        intent.putExtra("id", item.getId()); // Pass the item ID
        intent.putExtra("type", item.getType());
        intent.putExtra("name", item.getName());
        intent.putExtra("phone", item.getPhone());
        intent.putExtra("description", item.getDescription());
        intent.putExtra("date", item.getDate());
        intent.putExtra("location", item.getLocation());

        startActivityForResult(intent, REQUEST_CODE_REMOVE_ITEM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_REMOVE_ITEM && resultCode == RESULT_OK && data != null) {
            long deletedItemId = data.getLongExtra("deletedItemId", -1);
            if (deletedItemId != -1) {
                // Remove the item from the dataset
                for (int i = 0; i < itemList.size(); i++) {
                    if (itemList.get(i).getId() == deletedItemId) {
                        itemList.remove(i);
                        break;
                    }
                }
                // Notify the adapter that the dataset has changed
                itemAdapter.notifyDataSetChanged();
            }
        }
    }
}
