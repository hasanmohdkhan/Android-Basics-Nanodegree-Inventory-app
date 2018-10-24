package com.example.hasanzian.inventoryapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.hasanzian.inventoryapp.R;

import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_PRICE;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_QUANTITY;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME;
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER;

/**
 * {@link InventoryCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of Inventory data as its data source. This adapter knows
 * how to create list items for each row of Inventory data in the {@link Cursor}.
 */
public class InventoryCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link InventoryCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //view initialization
        TextView name = view.findViewById(R.id.tv_product_name);
        TextView price = view.findViewById(R.id.tv_product_price);
        TextView quantity = view.findViewById(R.id.tv_product_quantity);
        TextView supplier = view.findViewById(R.id.tv_product_supplier);
        TextView number = view.findViewById(R.id.tv_product_supplier_number);

        /// Find the columns of pet attributes that we're interested in

        int nameIndex = cursor.getColumnIndex(COLUMN_PRODUCT_NAME);
        int priceIndex = cursor.getColumnIndex(COLUMN_PRICE);
        int quantityIndex = cursor.getColumnIndex(COLUMN_QUANTITY);
        int supplierIndex = cursor.getColumnIndex(COLUMN_SUPPLIER_NAME);
        int supplierNumberIndex = cursor.getColumnIndex(COLUMN_SUPPLIER_PHONE_NUMBER);

        // Read the Inventory attributes from the Cursor for the current Inventory item in list
        String currentProductName = cursor.getString(nameIndex);
        int currentPrice = cursor.getInt(priceIndex);
        int currentQuantity = cursor.getInt(quantityIndex);
        String currentSupplier = cursor.getString(supplierIndex);
        String currentPhone = cursor.getString(supplierNumberIndex);

        // Update the TextViews with the attributes for the current Inventory item in list
        name.setText(currentProductName);
        price.setText(String.valueOf(currentPrice));
        quantity.setText(String.valueOf(currentQuantity));
        supplier.setText(currentSupplier);
        number.setText(currentPhone);

    }
}
