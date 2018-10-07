package com.example.hasanzian.inventoryapp.activity;

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

public class EditorActivity extends AppCompatActivity {

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

    InventoryDbHelper mHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        ButterKnife.bind(this);
        mHelper = new InventoryDbHelper(this);

        productStr = productName.getText().toString();
        priceStr = price.getText().toString();
        quantityStr = quantity.getText().toString();
        supplierStr = supplier.getText().toString();
        phoneStr = phone.getText().toString();

        Log.d("Text", "Et: " + productName + " " + priceStr + " " + quantityStr + " " + supplierStr + " " + phoneStr);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                long ID = Utils.insertProducts(getApplicationContext(), mHelper, price.getText().toString(), productName.getText().toString(), quantity.getText().toString(), supplier.getText().toString(), phone.getText().toString());
                if (ID == -1) {
                    Toast.makeText(getApplicationContext(), "Error in data insertion", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "data insertion: " + ID, Toast.LENGTH_SHORT).show();
                    clearEditText();
                    finish();
                }

                Log.d("Text", "Et: " + productName.getText().toString());


            }
        });


    }

    private void clearEditText() {
        productName.setText("");
        price.setText("");
        quantity.setText("");
        supplier.setText("");
        phone.setText("");
    }
}
