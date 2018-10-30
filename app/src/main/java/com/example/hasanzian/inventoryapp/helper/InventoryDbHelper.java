package com.example.hasanzian.inventoryapp.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_IMAGE_LOCATION;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_PRICE;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_QUANTITY;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.TABLE_NAME;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry._ID;

public class InventoryDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Inventory.db";

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_INVENTORY = "CREATE TABLE " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " + COLUMN_PRICE + " INTEGER NOT NULL, " + COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, " + COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, " + COLUMN_SUPPLIER_PHONE_NUMBER + " TEXT ," + COLUMN_IMAGE_LOCATION + " TEXT );";

        Log.d("TAG", CREATE_TABLE_INVENTORY);

        db.execSQL(CREATE_TABLE_INVENTORY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
