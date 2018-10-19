package com.example.hasanzian.inventoryapp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hasanzian.inventoryapp.R;
import com.example.hasanzian.inventoryapp.data.InventoryContract;

import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.CONTENT_URI;

/**
 * Utils contains helper methods
 */
public class Utils {

    /**
     * Helper method for Products Table
     *
     * @param mContext get the calling context
     * @param product  product name as string
     * @param price    price as string
     * @param quantity quantity as string
     * @param supplier supplier name
     * @param phone    phone of supplier
     * @return Uri of newly inserted item
     */
    public static Uri insertProducts(Context mContext, String product, String price, String quantity, String supplier, String phone) {
        //get param as values and set to corresponding column
        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME, product);
        values.put(InventoryContract.InventoryEntry.COLUMN_PRICE, Integer.parseInt(price));
        values.put(InventoryContract.InventoryEntry.COLUMN_QUANTITY, Integer.parseInt(quantity));
        values.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME, supplier);
        values.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER, phone);
        //insert new value via content resolver
        Uri newRowID = mContext.getContentResolver().insert(CONTENT_URI, values);
        // Error
        if (newRowID == null) {
            Toast.makeText(mContext, R.string.error_in_data_insertion_str, Toast.LENGTH_SHORT).show();
            return null;
        } else {
            // when insert is successful
            Toast.makeText(mContext, mContext.getString(R.string.data_insertion_sucessful_str), Toast.LENGTH_SHORT).show();
            return newRowID;
        }
    }


    /**
     * method to check edit text box is empty or not
     *
     * @param etText takes edit text
     * @return true if edit text is left empty else return false
     */
    public static boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() <= 0;
    }

    /**
     * greenCheck take current context
     * and returns a drawable
     *
     * @param mContext of current activity
     * @return a green drawable  with check
     */
    public static Drawable greenCheck(Context mContext) {
        Drawable myIcon = mContext.getResources().getDrawable(R.drawable.check_circle);
        myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
        return myIcon;
    }
}
