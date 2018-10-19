package com.example.hasanzian.inventoryapp.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hasanzian.inventoryapp.R;
import com.example.hasanzian.inventoryapp.helper.InventoryDbHelper;
import com.example.hasanzian.inventoryapp.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.hasanzian.inventoryapp.utils.Utils.isEmpty;

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

    int p, q, s, rs, ph;

    InventoryDbHelper mHelper;

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

        productName.setOnFocusChangeListener(this);
        price.setOnFocusChangeListener(this);
        quantity.setOnFocusChangeListener(this);
        supplier.setOnFocusChangeListener(this);
        phone.setOnFocusChangeListener(this);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (p + s + q + rs + ph == 4 & !isEmpty(phone)) {
                    Uri ID = Utils.insertProducts(getApplicationContext(), productName.getText().toString().trim(), price.getText().toString().trim(), quantity.getText().toString().trim(), supplier.getText().toString().trim(), phone.getText().toString().trim());
                    if (ID != null) {
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


    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        switch (view.getId()) {
            case R.id.et_product_name:
                if (!hasFocus) {

                    if (isEmpty(productName)) {
                        productName.setError(getString(R.string.product_name_errror));
                        p = 0;
                    } else {
                        productName.setError(getString(R.string.good_error), Utils.greenCheck(this));
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
                        price.setError(getString(R.string.good_error), Utils.greenCheck(this));
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
                        quantity.setError(getString(R.string.good_error), Utils.greenCheck(this));
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
                        supplier.setError(getString(R.string.good_error), Utils.greenCheck(this));
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
                        phone.setError(getString(R.string.good_error), Utils.greenCheck(this));
                        ph = 1;
                    }

                }
                break;


        }

    }
}
