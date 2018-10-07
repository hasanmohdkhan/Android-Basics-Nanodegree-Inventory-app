package com.example.hasanzian.inventoryapp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.hasanzian.inventoryapp.data.InventoryContract;
import com.example.hasanzian.inventoryapp.helper.InventoryDbHelper;

/**
 * Utils contain helper methods
 */
public class Utils {

    public static long insertProducts(Context mContext, InventoryDbHelper mHelper, String price, String product, String quantity, String supplier, String phone) {

        SQLiteDatabase db = mHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME, product);
        values.put(InventoryContract.InventoryEntry.COLUMN_PRICE, Integer.parseInt(price));
        values.put(InventoryContract.InventoryEntry.COLUMN_QUANTITY, Integer.parseInt(quantity));
        values.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME, supplier);
        values.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER, Integer.parseInt(phone));

        long newRowID = db.insert(InventoryContract.InventoryEntry.TABLE_NAME, null, values);
        if (newRowID == -1) {
            Toast.makeText(mContext, "Error in data insertion", Toast.LENGTH_SHORT).show();
            return newRowID;
        } else {
            Toast.makeText(mContext, "data insertion: " + newRowID, Toast.LENGTH_SHORT).show();
            return newRowID;
        }
    }
}
