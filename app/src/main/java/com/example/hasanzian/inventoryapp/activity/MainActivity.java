package com.example.hasanzian.inventoryapp.activity;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hasanzian.inventoryapp.R;
import com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry;
import com.example.hasanzian.inventoryapp.helper.InventoryDbHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

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
                insertProducts();
                displayDatabaseInfo();
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
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME , "Mi A1");
        values.put(InventoryEntry.COLUMN_PRICE,15999);
        values.put(InventoryEntry.COLUMN_QUANTITY,100);
        values.put(InventoryEntry.COLUMN_SUPPLIER_NAME,"Xiaomi Ltd.");
        values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER,180020020);

        long newRowID = db.insert(InventoryEntry.TABLE_NAME , null,values);
        if(newRowID == -1){
            Toast.makeText(this,"Error in data insertion",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this,"data insertion: " +newRowID,Toast.LENGTH_SHORT).show();
        }

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void displayDatabaseInfo() {

        SQLiteDatabase db = mHelper.getReadableDatabase();


        try (Cursor cursor = db.rawQuery("SELECT * FROM " + InventoryEntry.TABLE_NAME, null)) {
            db_count.setText("Number of rows in pets database table: " + cursor.getCount());
        }


    }
}
