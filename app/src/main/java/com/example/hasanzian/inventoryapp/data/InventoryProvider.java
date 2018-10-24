package com.example.hasanzian.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.hasanzian.inventoryapp.helper.InventoryDbHelper;

import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_PRICE;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_QUANTITY;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.CONTENT_ITEM_TYPE;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.CONTENT_LIST_TYPE;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.TABLE_NAME;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry._ID;

/**
 * {@link ContentProvider} for Inventory app.
 */

public class InventoryProvider extends ContentProvider {
    // database helper object
    private InventoryDbHelper mDbHelper;

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();
    public static final int INVENTORY = 100;
    public static final int PATH_INVENTORY_ID = 101;
    public static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY, INVENTORY);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY + "/#", PATH_INVENTORY_ID);
    }

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }


    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor = null;


        int match = sUriMatcher.match(uri);

        switch (match) {
            case INVENTORY: {
                cursor = database.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
            }
            break;
            case PATH_INVENTORY_ID: {
                selection = _ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);


            }
            break;
            default:
                throw new IllegalArgumentException("Unknown URI Exception");
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return CONTENT_LIST_TYPE;
            case PATH_INVENTORY_ID:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }


    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return insertInventory(uri, contentValues);

            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertInventory(Uri uri, ContentValues values) {
        // Get Writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        /// Insert the new pet with the given values
        long id = database.insert(TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        //notify listeners that data changes
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Track the number of rows that were deleted
        int rowsDeleted;
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(TABLE_NAME, selection, selectionArgs);
                break;
            case PATH_INVENTORY_ID:
                // Delete a single row given by the ID in the URI
                selection = _ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;

    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowUpdated;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                rowUpdated = updateInventory(uri, contentValues, selection, selectionArgs);
                break;
            case PATH_INVENTORY_ID:
                // For the PATH_INVENTORY_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = _ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowUpdated = updateInventory(uri, contentValues, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowUpdated;
    }

    private int updateInventory(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //Product name
        if (values.containsKey(COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }

        //Product Price
        if (values.containsKey(COLUMN_PRICE)) {
            String price = values.getAsString(COLUMN_PRICE);
            if (price == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }

        //Product Price
        if (values.containsKey(COLUMN_QUANTITY)) {
            String quantity = values.getAsString(COLUMN_QUANTITY);
            if (quantity == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }
        //Product Price
        if (values.containsKey(COLUMN_SUPPLIER_NAME)) {
            String supplier = values.getAsString(COLUMN_SUPPLIER_NAME);
            if (supplier == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }
        //Product Price
        if (values.containsKey(COLUMN_SUPPLIER_PHONE_NUMBER)) {
            String number = values.getAsString(COLUMN_SUPPLIER_PHONE_NUMBER);
            if (number == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }
        // Otherwise, get Writable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // Returns the number of database rows affected by the update statement
        return database.update(TABLE_NAME, values, selection, selectionArgs);
    }
}
