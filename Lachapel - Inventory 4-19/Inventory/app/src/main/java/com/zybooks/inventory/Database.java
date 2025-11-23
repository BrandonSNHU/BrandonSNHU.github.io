package com.zybooks.inventory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    private static final String TABLE_INVENTORY = "inventory";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_SKU = "sku";
    private static final String COLUMN_QUANTITY = "quantity";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // CREATE USERS TABLE
        String createUsers = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USERNAME + " TEXT PRIMARY KEY, " +
                COLUMN_PASSWORD + " TEXT)";
        db.execSQL(createUsers);

        // CREATE INVENTORY TABLE
        String createInventory = "CREATE TABLE " + TABLE_INVENTORY + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SKU + " TEXT UNIQUE, " +
                COLUMN_QUANTITY + " INTEGER)";
        db.execSQL(createInventory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTORY);
        onCreate(db);
    }

    // REGISTER NEW USER
    public boolean registerUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    // LOGIN CREDENTIALS CHECK
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USERNAME},
                COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{username, password},
                null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // GET ALL INV
    public Cursor getAllInventoryItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_INVENTORY, null);
    }

    // ADD ITEM
    public void addItem(String sku, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_INVENTORY, new String[]{COLUMN_ID, COLUMN_QUANTITY},
                COLUMN_SKU + " = ?", new String[]{sku}, null, null, null);

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            int existingQty = cursor.getInt(1);
            updateQuantity(id, existingQty + quantity);
        } else {
            ContentValues values = new ContentValues();
            values.put(COLUMN_SKU, sku);
            values.put(COLUMN_QUANTITY, quantity);
            db.insert(TABLE_INVENTORY, null, values);
        }
        cursor.close();
    }

    // UPDATE QTY
    public void updateQuantity(int id, int newQty) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_QUANTITY, newQty);
        db.update(TABLE_INVENTORY, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // DELETE ITEM
    public void deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_INVENTORY, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }
}