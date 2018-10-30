package com.example.hasanzian.inventoryapp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hasanzian.inventoryapp.R;
import com.example.hasanzian.inventoryapp.utils.Utils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_IMAGE_LOCATION;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_PRICE;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_QUANTITY;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry._ID;

public class DetailsViewActivity extends AppCompatActivity implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Identifier for the inventory data loader
     */
    private static final int EXISTING_INVENTORY_LOADER = 1;
    @BindView(R.id.tv_product_name_current)
    TextView mProductName;
    @BindView(R.id.tv_product_price_current)
    TextView mPrice;
    @BindView(R.id.tv_product_quantity_current)
    TextView mQuantity;
    @BindView(R.id.tv_product_supplier_current)
    TextView mSupplier;
    @BindView(R.id.tv_product_supplier_number_current)
    TextView mPhone;
    @BindView(R.id.fab_editor)
    FloatingActionButton mEditor;
    @BindView(R.id.fab_call)
    FloatingActionButton mCall;
    @BindView(R.id.toolbarImage)
    ImageView image;
    private Uri mCurrentInventoryUri;
    String imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_view);
        ButterKnife.bind(this);
        setTitle(R.string.details_activity_title);
        Intent intent = getIntent();
        mCurrentInventoryUri = intent.getData();
        getSupportLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);

        mEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editorIntent = new Intent(getApplicationContext(), EditorActivity.class);
                editorIntent.setData(mCurrentInventoryUri);
                startActivity(editorIntent);
            }
        });

        mCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPermissionGranted()) {
                    call_action();
                }
            }
        });

    }

    @NonNull
    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {_ID, COLUMN_PRODUCT_NAME, COLUMN_PRICE, COLUMN_QUANTITY, COLUMN_SUPPLIER_NAME, COLUMN_SUPPLIER_PHONE_NUMBER, COLUMN_IMAGE_LOCATION};
        return new CursorLoader(this, mCurrentInventoryUri, projection, null, null, null);

    }

    @Override
    public void onLoadFinished(@NonNull android.support.v4.content.Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // finding column index
            int nameColumnIndex = cursor.getColumnIndex(COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(COLUMN_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(COLUMN_SUPPLIER_NAME);
            int phoneColumnIndex = cursor.getColumnIndex(COLUMN_SUPPLIER_PHONE_NUMBER);
            int imageColumnIndex = cursor.getColumnIndex(COLUMN_IMAGE_LOCATION);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            String supplierName = cursor.getString(supplierColumnIndex);
            String phone = cursor.getString(phoneColumnIndex);
            imageUri = cursor.getString(imageColumnIndex);
            Toast.makeText(this, "" + imageUri, Toast.LENGTH_SHORT).show();

            // Update the views on the screen with the values from the database
            mProductName.setText(name);
            mPrice.setText(String.valueOf(price));
            mQuantity.setText(String.valueOf(quantity));
            mSupplier.setText(supplierName);
            mPhone.setText(phone);
            if (imageUri != null) {
                File imageFile = Utils.getFileFromLocation(imageUri);
                if (imageFile != null && imageFile.exists()) {
                    Glide.with(this).load(imageFile).into(image);
                }

            }

        }
    }

    @Override
    public void onLoaderReset(@NonNull android.support.v4.content.Loader<Cursor> loader) {
        mProductName.setText("");
        mPrice.setText(String.valueOf(""));
        mQuantity.setText(String.valueOf("0"));
        mPhone.setText("");
    }

    private void call_action() {

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + mPhone.getText().toString().trim()));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), R.string.toast_msg, Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(callIntent);
    }

    public boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), R.string.toast_msg_granted, Toast.LENGTH_SHORT).show();
                    call_action();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.toast_msg_deny, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
