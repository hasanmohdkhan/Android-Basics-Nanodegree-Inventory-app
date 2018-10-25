package com.example.hasanzian.inventoryapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
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
import static com.example.hasanzian.inventoryapp.data.InventoryContract.InventoryEntry._ID;
import static com.example.hasanzian.inventoryapp.utils.Utils.isEmpty;

public class EditorActivity extends AppCompatActivity implements View.OnFocusChangeListener, android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the inventory data loader
     */
    private static final int EXISTING_INVENTORY_LOADER = 1;
    @BindView(R.id.et_product_name)
    EditText mProductName;
    @BindView(R.id.et_product_price)
    EditText mPrice;
    @BindView(R.id.et_product_quantity)
    EditText mQuantity;
    @BindView(R.id.et_supplier_name)
    EditText mSupplier;
    @BindView(R.id.et_supplier_phone)
    EditText mPhone;

    /**
     * Boolean flag that keeps track of whether the item has been edited (true) or not (false)
     */
    private boolean mItemFieldHasChanged = false;
    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mItemFieldHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemFieldHasChanged = true;
            return false;
        }
    };
    /**
     * Content URI for the existing Inventory item (null if it's a new item)
     */
    private Uri mCurrentInventoryUri;
    @BindView(R.id.save_fab)
    FloatingActionButton save;
    @BindView(R.id.fab_plus)
    FloatingActionButton plus;
    @BindView(R.id.fab_minus)
    FloatingActionButton minus;

    int p, q, s, rs, ph, quantity;

    InventoryDbHelper mHelper;

    private void clearEditText() {
        mProductName.setText("");
        mPrice.setText("");
        mQuantity.setText("");
        mSupplier.setText("");
        mPhone.setText("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        ButterKnife.bind(this);
        mHelper = new InventoryDbHelper(this);

        mProductName.setOnFocusChangeListener(this);
        mPrice.setOnFocusChangeListener(this);
        mQuantity.setOnFocusChangeListener(this);
        mSupplier.setOnFocusChangeListener(this);
        mPhone.setOnFocusChangeListener(this);

        mProductName.setOnTouchListener(mTouchListener);
        mPrice.setOnTouchListener(mTouchListener);
        mQuantity.setOnTouchListener(mTouchListener);
        mSupplier.setOnTouchListener(mTouchListener);
        mPhone.setOnTouchListener(mTouchListener);


        Intent intent = getIntent();
        mCurrentInventoryUri = intent.getData();

        if (mCurrentInventoryUri == null) {
            setTitle(R.string.add_item_string);
            invalidateOptionsMenu();
            mQuantity.setText("0");
        } else {
            getSupportLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
            setTitle(R.string.edit_existing_item);

        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isEmpty(mProductName) & !isEmpty(mPrice) & !isEmpty(mQuantity) & !isEmpty(mSupplier) & !isEmpty(mPhone)) {

                    if (mCurrentInventoryUri == null) {
                        Uri ID = Utils.insertProducts(getApplicationContext(), mProductName.getText().toString().trim(), mPrice.getText().toString().trim(), mQuantity.getText().toString().trim(), mSupplier.getText().toString().trim(), mPhone.getText().toString().trim());
                        if (ID != null) {
                            clearEditText();
                            finish();
                        }
                    } else {
                        int rowsAffected = Utils.updateProducts(mCurrentInventoryUri, getApplicationContext(), mProductName.getText().toString().trim(), mPrice.getText().toString().trim(), mQuantity.getText().toString().trim(), mSupplier.getText().toString().trim(), mPhone.getText().toString().trim());
                        if (rowsAffected != 0) {
                            clearEditText();
                            finish();
                        }
                    }
                    Log.d("Text", "Et: " + mProductName.getText().toString());

                } else {
                    Toast.makeText(getApplicationContext(), "Check Entries " + isEmpty(mPhone), Toast.LENGTH_SHORT).show();
                }


            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity = Integer.parseInt(mQuantity.getText().toString().trim());
                quantity += 1;
                mQuantity.setText(String.valueOf(quantity));
            }
        });
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity = Integer.parseInt(mQuantity.getText().toString().trim());
                if (quantity > 0) {
                    quantity -= 1;
                    mQuantity.setText(String.valueOf(quantity));
                }
            }
        });

    }


    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        switch (view.getId()) {
            case R.id.et_product_name:
                if (!hasFocus) {

                    if (isEmpty(mProductName)) {
                        mProductName.setError(getString(R.string.product_name_errror));
                        p = 0;
                    } else {
                        mProductName.setError(getString(R.string.good_error), Utils.greenCheck(this));
                        p = 1;

                    }

                }
                break;

            case R.id.et_product_price:

                if (!hasFocus) {
                    if (isEmpty(mPrice)) {
                        mPrice.setError(getString(R.string.price_error));
                        rs = 0;
                    } else {
                        mPrice.setError(getString(R.string.good_error), Utils.greenCheck(this));
                        rs = 1;
                    }
                }
                break;

            case R.id.et_product_quantity:

                if (!hasFocus) {
                    if (isEmpty(mQuantity)) {
                        mQuantity.setError(getString(R.string.quantity_error));
                        q = 0;
                    } else {
                        mQuantity.setError(getString(R.string.good_error), Utils.greenCheck(this));
                        q = 1;
                    }

                }
                break;

            case R.id.et_supplier_name:
                if (!hasFocus) {
                    if (isEmpty(mSupplier)) {
                        mSupplier.setError(getString(R.string.supplier_name_error));
                        s = 0;
                    } else {
                        mSupplier.setError(getString(R.string.good_error), Utils.greenCheck(this));
                        s = 1;
                    }

                }
                break;

            case R.id.et_supplier_phone:
                if (!hasFocus) {

                    if (isEmpty(mPhone)) {
                        mPhone.setError(getString(R.string.error_phone_number));
                        ph = 0;
                    } else {
                        mPhone.setError(getString(R.string.good_error), Utils.greenCheck(this));
                        ph = 1;
                    }

                }
                break;


        }

    }


    @NonNull
    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {_ID, COLUMN_PRODUCT_NAME, COLUMN_PRICE, COLUMN_QUANTITY, COLUMN_SUPPLIER_NAME, COLUMN_SUPPLIER_PHONE_NUMBER};
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

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            String supplierName = cursor.getString(supplierColumnIndex);
            String phone = cursor.getString(phoneColumnIndex);

            // Update the views on the screen with the values from the database
            mProductName.setText(name);
            mPrice.setText(String.valueOf(price));
            mQuantity.setText(String.valueOf(quantity));
            mSupplier.setText(supplierName);
            mPhone.setText(phone);

        }
    }

    @Override
    public void onLoaderReset(@NonNull android.support.v4.content.Loader<Cursor> loader) {
        clearEditText();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                // If the item hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mItemFieldHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, navigate to parent activity.
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the item hasn't changed, continue with handling back button press
        if (!mItemFieldHasChanged) {
            super.onBackPressed();
            return;
        }
        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked "Discard" button, close the current activity.
                finish();
            }
        };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }


    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new item, hide the "Delete" menu item.
        if (mCurrentInventoryUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }


    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so ic_delete the item.
                deletePet();

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

    /**
     * Perform the deletion of the item in the database.
     */
    private void deletePet() {
        if (mCurrentInventoryUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentInventoryUri, null, null);

            // Show a toast message depending on whether or not the ic_delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the ic_delete.
                Toast.makeText(this, getString(R.string.editor_delete_pet_failed), Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the ic_delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_pet_successful), Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(this, MainActivity.class));
            }

        }
    }


}

