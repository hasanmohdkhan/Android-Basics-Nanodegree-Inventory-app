package com.example.hasanzian.inventoryapp.activity;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry;
import com.example.hasanzian.inventoryapp.helper.InventoryDbHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_PRICE;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_QUANTITY;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.TABLE_NAME;
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
                //  Intent editorIntent = new Intent(getApplicationContext(),EditorActivity.class);
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
        SQLiteDatabase db = mHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, "Mi A1");
        values.put(InventoryEntry.COLUMN_PRICE, 15999);
        values.put(InventoryEntry.COLUMN_QUANTITY, 100);
        values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, "Xiaomi Ltd.");
        values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER, 180020020);

        long newRowID = db.insert(InventoryEntry.TABLE_NAME, null, values);
        if (newRowID == -1) {
            Toast.makeText(this, R.string.error_in_data_insertion_str, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getApplicationContext().getString(R.string.data_insertion_sucessful_str) + newRowID, Toast.LENGTH_SHORT).show();

        }

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void displayDatabaseInfo() {

        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            String[] projection = {_ID, COLUMN_PRODUCT_NAME, COLUMN_PRICE, COLUMN_QUANTITY, COLUMN_SUPPLIER_NAME, COLUMN_SUPPLIER_PHONE_NUMBER};

            cursor = db.query(TABLE_NAME, projection, null, null, null, null, null);
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
                int currentPhone = cursor.getInt(supplierPhoneColumnIndex);
                //add current information to text view
                db_count.append("\n" + currentID + " - " + currentProductName + " - " + currentPrice + " - " + currentQuantity + " - " + currentSupplierName + " - " + currentPhone);
            }


        } finally {
            assert cursor != null;
            cursor.close();
        }


    }

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
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
