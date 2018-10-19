package com.example.hasanzian.inventoryapp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hasanzian.inventoryapp.R;
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
            Toast.makeText(mContext, R.string.error_in_data_insertion_str, Toast.LENGTH_SHORT).show();
            return newRowID;
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.data_insertion_sucessful_str) + newRowID, Toast.LENGTH_SHORT).show();
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
