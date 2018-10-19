package com.example.hasanzian.inventoryapp.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hasanzian.inventoryapp.R;
import com.example.hasanzian.inventoryapp.helper.InventoryDbHelper;
import com.example.hasanzian.inventoryapp.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_PRICE;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_QUANTITY;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.CONTENT_URI;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry._ID;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.db_numbers)
    TextView db_count;
    @BindView(R.id.floatingActionButton)
    FloatingActionButton fab;
    InventoryDbHelper mHelper;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), EditorActivity.class));
            }
        });
        mHelper = new InventoryDbHelper(this);
        displayDatabaseInfo();

    }

    /*
     *  Helper method to insert dummy data in Products table for debugging purpose
     * */
    private void insertProducts() {
        // Insert a new row for "Mi A1" into the provider using the ContentResolver.
        // Use the {@link InventoryEntry#CONTENT_URI} to indicate that we want to insert
        // into the Inventory database table.
        // Receive the new content URI that will allow us to access "Mi A1"'s data in the future.
        Uri newUri = Utils.insertProducts(getApplicationContext(), "Mi A1", "15999", "100", "Xiaomi Ltd.", "8604646437");

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void displayDatabaseInfo() {

        Cursor cursor = null;

        try {
            String[] projection = {_ID, COLUMN_PRODUCT_NAME, COLUMN_PRICE, COLUMN_QUANTITY, COLUMN_SUPPLIER_NAME, COLUMN_SUPPLIER_PHONE_NUMBER};
            cursor = getContentResolver().query(CONTENT_URI, projection, null, null, null);

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(_ID);
            int productNameColumnIndex = cursor.getColumnIndex(COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(COLUMN_SUPPLIER_PHONE_NUMBER);

            db_count.setText(getString(R.string.number_of_row) + cursor.getCount() + "\n\n");
            db_count.append(_ID + "-" + COLUMN_PRODUCT_NAME + " - " + COLUMN_PRICE + " - " + COLUMN_QUANTITY + " - " + COLUMN_SUPPLIER_NAME + " - " + COLUMN_SUPPLIER_PHONE_NUMBER + "\n");

            while (cursor.moveToNext()) {

                int currentID = cursor.getInt(idColumnIndex);
                String currentProductName = cursor.getString(productNameColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupplierName = cursor.getString(supplierNameColumnIndex);
                String currentPhone = cursor.getString(supplierPhoneColumnIndex);
                //add current information to text view
                db_count.append("\n" + currentID + " - " + currentProductName + " - " + currentPrice + " - " + currentQuantity + " - " + currentSupplierName + " - " + currentPhone);
            }
        } finally {
            assert cursor != null;
            cursor.close();
        }


    }

    /**
     * refresh the text view after inserting data in db
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            displayDatabaseInfo();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.dummy:
                insertProducts();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    displayDatabaseInfo();
                }
                return true;

            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                int del = getContentResolver().delete(CONTENT_URI, null, null);
                Toast.makeText(this, "DELETED :" + del, Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    displayDatabaseInfo();
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
