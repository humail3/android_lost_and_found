package com.gopi.lostandfound.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "LostFoundDB";
    private static final int DATABASE_VERSION = 1;

    // Table name
    private static final String TABLE_NAME = "items";

    // Column names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_LOCATION = "location";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the table
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_TYPE + " TEXT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_PHONE + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_DATE + " TEXT,"
                + COLUMN_LOCATION + " TEXT"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Upgrade the database (if needed)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

// Add a new item to the database
    public void addItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE, item.getType());
        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_PHONE, item.getPhone());
        values.put(COLUMN_DESCRIPTION, item.getDescription());
        values.put(COLUMN_DATE, item.getDate());
        values.put(COLUMN_LOCATION, item.getLocation());

        long newRowId = db.insert(TABLE_NAME, null, values);
        if (newRowId != -1) {
            // Insert successful
            item.setId(newRowId); // Set the ID of the item
        } else {
            // Insert failed
            // Handle the failure, such as logging an error message or throwing an exception
        }
        db.close();
    }




    // Retrieve all items from the database
    // Retrieve all items from the database
    // Retrieve all items from the database
    public List<Item> getAllItems() {
        List<Item> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            int typeIndex = cursor.getColumnIndex(COLUMN_TYPE);
            int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
            int phoneIndex = cursor.getColumnIndex(COLUMN_PHONE);
            int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);
            int dateIndex = cursor.getColumnIndex(COLUMN_DATE);
            int locationIndex = cursor.getColumnIndex(COLUMN_LOCATION);

            do {
                int id = cursor.getInt(idIndex);
                String type = cursor.getString(typeIndex);
                String name = cursor.getString(nameIndex);
                String phone = cursor.getString(phoneIndex);
                String description = cursor.getString(descriptionIndex);
                String date = cursor.getString(dateIndex);
                String location = cursor.getString(locationIndex);

                // Create Item object with retrieved data
                Item item = new Item(type, name, phone, description, date, location);
                item.setId(id);

                itemList.add(item);
            } while (cursor.moveToNext());
        }

        // Close cursor and database
        cursor.close();
        db.close();

        return itemList;
    }

    // Delete an item from the database
    public void deleteItem(long itemId) {
        Log.d("DBHelper","deleteItem: "+itemId);
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(itemId)});
        db.close();
    }



}
