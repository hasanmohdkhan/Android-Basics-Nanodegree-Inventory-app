package com.example.hasanzian.inventoryapp.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hasanzian.inventoryapp.R;
import com.example.hasanzian.inventoryapp.helper.InventoryDbHelper;
import com.example.hasanzian.inventoryapp.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditorActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    @BindView(R.id.et_product_name)
    EditText productName;
    @BindView(R.id.et_product_price)
    EditText price;
    @BindView(R.id.et_product_quantity)
    EditText quantity;
    @BindView(R.id.et_supplier_name)
    EditText supplier;
    @BindView(R.id.et_supplier_phone)
    EditText phone;
    @BindView(R.id.save_fab)
    FloatingActionButton save;


    String productStr, priceStr, quantityStr, supplierStr, phoneStr;
    int p, q, s, rs, ph;

    InventoryDbHelper mHelper;

    public static void hideSoftKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    private void clearEditText() {
        productName.setText("");
        price.setText("");
        quantity.setText("");
        supplier.setText("");
        phone.setText("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        ButterKnife.bind(this);
        mHelper = new InventoryDbHelper(this);

        productStr = productName.getText().toString().trim();
        priceStr = price.getText().toString().trim();
        quantityStr = quantity.getText().toString().trim();
        supplierStr = supplier.getText().toString().trim();
        phoneStr = phone.getText().toString().trim();

        productName.setOnFocusChangeListener(this);
        price.setOnFocusChangeListener(this);
        quantity.setOnFocusChangeListener(this);
        supplier.setOnFocusChangeListener(this);
        phone.setOnFocusChangeListener(this);


        Log.d("Text", "Et: " + productName + " " + priceStr + " " + quantityStr + " " + supplierStr + " " + phoneStr);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (p + s + q + rs + ph == 4 & !isEmpty(phone)) {
                    long ID = Utils.insertProducts(getApplicationContext(), mHelper, price.getText().toString().trim(), productName.getText().toString().trim(), quantity.getText().toString().trim(), supplier.getText().toString().trim(), phone.getText().toString().trim());
                    if (ID == -1) {
                        Toast.makeText(getApplicationContext(), "Error in data insertion", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "data insertion: " + ID, Toast.LENGTH_SHORT).show();
                        clearEditText();
                        finish();
                    }

                    Log.d("Text", "Et: " + productName.getText().toString());

                } else {
                    Toast.makeText(getApplicationContext(), "Check Entries " + isEmpty(phone), Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() <= 0;
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        switch (view.getId()) {
            case R.id.et_product_name:
                if (!hasFocus) {

                    if (isEmpty(productName)) {
                        productName.setError(getString(R.string.product_name_errror));
                        p = 0;
                    } else {
                        Drawable myIcon = getResources().getDrawable(R.drawable.check_circle);
                        myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
                        productName.setError(getString(R.string.good_error), myIcon);
                        p = 1;

                    }

                }
                break;

            case R.id.et_product_price:

                if (!hasFocus) {
                    if (isEmpty(price)) {
                        price.setError(getString(R.string.price_error));
                        rs = 0;
                    } else {
                        Drawable myIcon = getResources().getDrawable(R.drawable.check_circle);
                        myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
                        price.setError(getString(R.string.good_error), myIcon);
                        rs = 1;

                    }

                }
                break;

            case R.id.et_product_quantity:

                if (!hasFocus) {
                    if (isEmpty(quantity)) {
                        quantity.setError(getString(R.string.quantity_error));
                        q = 0;
                    } else {
                        Drawable myIcon = getResources().getDrawable(R.drawable.check_circle);
                        myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
                        quantity.setError(getString(R.string.good_error), myIcon);
                        q = 1;
                    }

                }
                break;

            case R.id.et_supplier_name:
                if (!hasFocus) {
                    if (isEmpty(supplier)) {
                        supplier.setError(getString(R.string.supplier_name_error));
                        s = 0;
                    } else {
                        Drawable myIcon = getResources().getDrawable(R.drawable.check_circle);
                        myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
                        supplier.setError(getString(R.string.good_error), myIcon);
                        s = 1;
                    }

                }
                break;

            case R.id.et_supplier_phone:
                if (!hasFocus) {

                    if (isEmpty(phone)) {
                        phone.setError(getString(R.string.error_phone_number));
                        ph = 0;
                    } else {
                        Drawable myIcon = getResources().getDrawable(R.drawable.check_circle);
                        myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
                        phone.setError(getString(R.string.good_error), myIcon);
                        ph = 1;
                    }

                }
                break;


        }

    }
}
