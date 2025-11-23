package com.zybooks.inventory;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class InventoryActivity extends AppCompatActivity {

    private LinearLayout inventoryLayout;
    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        db = new Database(this);
        inventoryLayout = findViewById(R.id.inventoryLayout);

        loadInventoryItems();
//ITEM BUTTON
        Button addItemButton = findViewById(R.id.addItemButton);
        addItemButton.setOnClickListener(v ->
                startActivity(new Intent(this, AddItemActivity.class))
        );
//NOTIFICATION BUTTON
        Button notificationsButton = findViewById(R.id.notificationsButton);
        notificationsButton.setOnClickListener(v ->
                startActivity(new Intent(this, SmsActivity.class))
        );
    }

    private void loadInventoryItems() {
        if (inventoryLayout.getChildCount() > 0) {
            inventoryLayout.removeAllViews();
        }

        Cursor cursor = db.getAllInventoryItems();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String sku = cursor.getString(1);
            int quantity = cursor.getInt(2);

            // ROW CONTAINER
            LinearLayout itemRow = new LinearLayout(this);
            itemRow.setOrientation(LinearLayout.HORIZONTAL);
            itemRow.setPadding(8, 8, 8, 8);
            itemRow.setBackgroundColor(0xFFEEEEEE);
            itemRow.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            rowParams.setMargins(0, 0, 0, 16);
            itemRow.setLayoutParams(rowParams);

            // TEXT LABEL
            TextView itemName = new TextView(this);
            itemName.setText(sku + ": " + quantity);
            itemName.setTextSize(24);
            itemName.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1  // weight to push buttons to right
            ));
            itemRow.addView(itemName);

            // PLUS
            Button plusButton = new Button(this);
            plusButton.setText("+");
            plusButton.setTextSize(18);
            plusButton.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            plusButton.setTypeface(null, android.graphics.Typeface.BOLD);
            plusButton.setBackgroundTintList(getColorStateList(R.color.green));
            LinearLayout.LayoutParams plusParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            plusParams.setMargins(8, 0, 8, 0);
            plusButton.setLayoutParams(plusParams);
            plusButton.setOnClickListener(v -> {
                db.updateQuantity(id, quantity + 1);
                loadInventoryItems(); // refresh
            });
            itemRow.addView(plusButton);

            // MINUS
            Button minusButton = new Button(this);
            minusButton.setText("-");
            minusButton.setTextSize(18);
            minusButton.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            minusButton.setTypeface(null, android.graphics.Typeface.BOLD);
            minusButton.setBackgroundTintList(getColorStateList(R.color.red));
            LinearLayout.LayoutParams minusParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            minusParams.setMargins(8, 0, 8, 0);
            minusButton.setLayoutParams(minusParams);
            minusButton.setOnClickListener(v -> {
                int newQty = quantity - 1;
                db.updateQuantity(id, newQty);
                if (newQty == 0) sendLowStockAlert(sku);
                loadInventoryItems(); // refresh
            });
            itemRow.addView(minusButton);

            // DELETE
            ImageButton deleteButton = new ImageButton(this);
            deleteButton.setImageResource(R.drawable.ic_trash);
            deleteButton.setBackgroundTintList(getColorStateList(R.color.delete_red));
            LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(
                    80, 80
            );
            deleteParams.setMargins(8, 0, 0, 0);
            deleteButton.setLayoutParams(deleteParams);
            deleteButton.setOnClickListener(v -> {
                db.deleteItem(id);
                loadInventoryItems(); // refresh
            });
            itemRow.addView(deleteButton);

            inventoryLayout.addView(itemRow);
        }

        cursor.close();
    }

    // SMS
    private void sendLowStockAlert(String sku) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            String message = "Inventory item '" + sku + "' is out of stock";
            String phoneNumber = "2222222222";
            SmsManager.getDefault().sendTextMessage(phoneNumber, null, message, null, null);
        }
    }
}