package com.example.aulasqlitemobile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BancoHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "HabitsDB";
    private static final int DB_VERSION = 1;

    public BancoHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE habits (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, description TEXT, done TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS habits");
        onCreate(db);
    }

    public void insert(String title, String desc, String done) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("description", desc);
        values.put("done", done);
        db.insert("habits", null, values);
    }

    public void update(int id, String title, String desc, String done) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("description", desc);
        values.put("done", done);
        db.update("habits", values, "id = ?", new String[]{String.valueOf(id)});
    }

    public void delete(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("habits", "id = ?", new String[]{String.valueOf(id)});
    }

    public Cursor getAll() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM habits", null);
    }

    public Cursor getById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM habits WHERE id = ?", new String[]{String.valueOf(id)});
    }
}