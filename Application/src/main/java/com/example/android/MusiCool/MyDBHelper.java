package com.example.android.MusiCool;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHelper extends SQLiteOpenHelper {

    private static MyDBHelper instance = null;
    public static MyDBHelper getInstance(Context ctx){
        if (instance == null){
            instance = new MyDBHelper(ctx, "db_test.db", null, 1);
        }
        return instance;
    }

    private MyDBHelper(Context context, String name,
                      SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE  TABLE " + Item.DATABASE_TABLE+
                " (_id INTEGER PRIMARY KEY  NOT NULL , " +
                "name VARCHAR , " +
                "data VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion, int newVersion) {
    }

}
