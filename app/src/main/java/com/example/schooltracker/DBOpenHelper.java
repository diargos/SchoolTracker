package com.example.schooltracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "schooltracker.db";
    public static final int DATABASE_VERSION = 4;

    public static final String TABLE = "tracker";
    public static final String[] COLUMNS = {"_id", "type", "name", "parent", "data1", "data2", "data3", "data4"};

    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE  + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "type TEXT, " +
                "parent INTEGER, " +
                "name TEXT, " +
                "data1 TEXT, " +
                "data2 TEXT, " +
                "data3 TEXT, " +
                "data4 TEXT " +
                ")";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE + ";");
        onCreate(db);
    }
}
