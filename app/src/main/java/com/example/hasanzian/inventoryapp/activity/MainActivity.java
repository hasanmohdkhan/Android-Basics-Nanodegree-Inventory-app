package com.example.hasanzian.inventoryapp.activity;

import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.hasanzian.inventoryapp.R;
import com.example.hasanzian.inventoryapp.adapter.InventoryCursorAdapter;
import com.example.hasanzian.inventoryapp.helper.InventoryDbHelper;
import com.example.hasanzian.inventoryapp.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_IMAGE_LOCATION;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_PRICE;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_QUANTITY;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.CONTENT_URI;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry._ID;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.floatingActionButton)
    FloatingActionButton fab;
    public static final int INVENTORY_LOADER = 1;
    @BindView(R.id.empty_view)
    View empty;
    InventoryDbHelper mHelper;
    @BindView(R.id.list)
    ListView inventoryListView;
    InventoryCursorAdapter mInventoryAdapter;
    Bitmap bm;

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
        // Setup an Adapter to create a list item for each row of pet data in the Cursor.
        mInventoryAdapter = new InventoryCursorAdapter(this, null);

        // Attach the mInventoryAdapter to the ListView.
        inventoryListView.setAdapter(mInventoryAdapter);
        inventoryListView.setEmptyView(empty);

        inventoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DetailsViewActivity.class);
                // here we getting current item id i.e
                // content://com.example.android.pets/pets/3
                Uri currentProductUri = ContentUris.withAppendedId(CONTENT_URI, id);
                intent.setData(currentProductUri);
                startActivity(intent);
            }
        });
        getSupportLoaderManager().initLoader(INVENTORY_LOADER, null, this);
    }

    //Helper method to insert dummy data in Products table for debugging purpose
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void insertProducts() {
        // Insert a new row for "Mi A1" into the provider using the ContentResolver.
        // Use the {@link InventoryEntry#CONTENT_URI} to indicate that we want to insert
        // into the Inventory database table.
        // Receive the new content URI that will allow us to access "Mi A1"'s data in the future.

        String fileNameDemo = Utils.createDemoPic(this);
        Utils.insertProducts(getApplicationContext(), "Mi A1", "15999", "100", "Xiaomi Ltd.", "8604646437", Uri.parse(fileNameDemo), true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.dummy:
                insertProducts();
                return true;

            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:

                Cursor cursor = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    cursor = getContentResolver().query(CONTENT_URI, null, null, null);
                    assert cursor != null;
                    if (cursor.getCount() == 0) {
                        Toast.makeText(getApplicationContext(), "Nonting to delete", Toast.LENGTH_SHORT).show();
                    } else {
                        showDeleteConfirmationDialog();
                    }
                }


                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        //  Cursor cursor;
        String[] projection = {_ID, COLUMN_PRODUCT_NAME, COLUMN_PRICE, COLUMN_QUANTITY, COLUMN_SUPPLIER_NAME, COLUMN_SUPPLIER_PHONE_NUMBER, COLUMN_IMAGE_LOCATION};
        // cursor = getContentResolver().query(CONTENT_URI, projection, null, null, null);
        return new CursorLoader(this, CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.
        mInventoryAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mInventoryAdapter.swapCursor(null);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mInventoryAdapter.swapCursor(Utils.readItem(this));
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_dialog_msg);
        builder.setPositiveButton(R.string.conform, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so ic_delete the item.
                // Delete all entries
                int del = getContentResolver().delete(CONTENT_URI, null, null);
                Toast.makeText(getApplicationContext(), "DELETED :" + del, Toast.LENGTH_SHORT).show();
                Utils.deleteAllOldFiles(getApplicationContext());

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the item.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}
