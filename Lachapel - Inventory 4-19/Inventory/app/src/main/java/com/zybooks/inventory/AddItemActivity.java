package com.zybooks.inventory;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class AddItemActivity extends AppCompatActivity {

    private EditText skuField, quantityField;
    private Button saveItemButton, cancelButton;
    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additem);

        db = new Database(this);
        skuField = findViewById(R.id.editItemName);
        quantityField = findViewById(R.id.editItemQuantity);
        saveItemButton = findViewById(R.id.saveItemButton);
        cancelButton = findViewById(R.id.cancelButton);

        // SAVE ITEM
        saveItemButton.setOnClickListener(v -> {
            String sku = skuField.getText().toString();
            int quantity = Integer.parseInt(quantityField.getText().toString());

            db.addItem(sku, quantity);

            Intent intent = new Intent(this, InventoryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        // CANCEL
        cancelButton.setOnClickListener(v -> {
            finish();
        });
    }
}