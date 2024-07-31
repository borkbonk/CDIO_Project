//package com.sp.cdio_project2.Database;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//import com.sp.cdio_project2.ui.dashboard.Item;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class DatabaseHelper extends SQLiteOpenHelper {
//
//    private static final String DATABASE_NAME = "inventory.db";
//    private static final int DATABASE_VERSION = 1;
//    private static final String TABLE_ITEMS = "items";
//    private static final String COLUMN_ID = "id";
//    private static final String COLUMN_TITLE = "title";
//    private static final String COLUMN_DESCRIPTION = "description";
//    private static final String COLUMN_QUANTITY = "quantity";
//
//    public DatabaseHelper(Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_ITEMS + "("
//                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + COLUMN_TITLE + " TEXT,"
//                + COLUMN_DESCRIPTION + " TEXT,"
//                + COLUMN_QUANTITY + " INTEGER" + ")";
//        db.execSQL(CREATE_ITEMS_TABLE);
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
//        onCreate(db);
//    }
//
//    public void addItem(Item item) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_TITLE, item.getTitle());
//        values.put(COLUMN_DESCRIPTION, item.getDescription());
//        values.put(COLUMN_QUANTITY, item.getQuantity());
//
//        db.insert(TABLE_ITEMS, null, values);
//        db.close();
//    }
//
//    public Item getItem(int id) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.query(TABLE_ITEMS, new String[]{COLUMN_ID, COLUMN_TITLE, COLUMN_DESCRIPTION, COLUMN_QUANTITY},
//                COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
//        if (cursor != null)
//            cursor.moveToFirst();
//
//        Item item = new Item(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3));
//        cursor.close();
//        return item;
//    }
//
//    public List<Item> getAllItems() {
//        List<Item> itemList = new ArrayList<>();
//        String selectQuery = "SELECT * FROM " + TABLE_ITEMS;
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//
//        if (cursor.moveToFirst()) {
//            do {
//                Item item = new Item(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3));
//                itemList.add(item);
//            } while (cursor.moveToNext());
//        }
//
//        cursor.close();
//        return itemList;
//    }
//
//    public void updateItem(Item item) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_TITLE, item.getTitle());
//        values.put(COLUMN_DESCRIPTION, item.getDescription());
//        values.put(COLUMN_QUANTITY, item.getQuantity());
//
//        db.update(TABLE_ITEMS, values, COLUMN_ID + "=?", new String[]{String.valueOf(item.getId())});
//        db.close();
//    }
//}