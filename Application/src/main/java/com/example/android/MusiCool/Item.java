package com.example.android.MusiCool;

public class Item {

    // Labels DB
    public  static final String DATABASE_TABLE = "db_table";

    // Labels Table Columns names
    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_DATA = "data";

    //mode constants
    public static final int MODE_NONE = 0;
    public static final int MODE_PLAY = 1;
    public static final int MODE_RECODE = 2;
    public static final int MODE_FREE = 3;
    public static final int MODE_LEARN = 4;

    public static int _id;
    public String name;
    public String data;
    public static String new_song_name;
    public static int current_position = 0;
    public static int current_mode = MODE_NONE;
}
