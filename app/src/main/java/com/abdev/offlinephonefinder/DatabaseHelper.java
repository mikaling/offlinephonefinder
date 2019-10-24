package com.abdev.offlinephonefinder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    //Logcat tag
    private static final String LOG = "DatabaseHelper";

    //Database version
    private static final int DATABASE_VERSION = 1;

    //Database name
    public static final String DATABASE_NAME = "Finder.db";

    //User table  name
    private static final String TABLE_USER = "user";

    //User columns
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_PASSWORD = "password";

    //Codes table name
    public static final String TABLE_CODES = "codes";

    //Codes columns
    public static final String COLUMN_CODES_ID = "code_id";
    public static final String COLUMN_CODES_CODE = "code";
    public static final String COLUMN_CODES_FEATURE = "feature";
    public static final String COLUMN_CODES_USER_ID = "user_id";


    //Create user table statement
    private static final String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER + "(" + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_USER_EMAIL
            + " TEXT, " + COLUMN_USER_PASSWORD + " TEXT)";

    //Create codes table statement
    private static final String CREATE_TABLE_CODES = "CREATE TABLE " + TABLE_CODES + "(" + COLUMN_CODES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_CODES_CODE
            + " TEXT, " +  COLUMN_CODES_FEATURE + " TEXT, " + COLUMN_CODES_USER_ID + " INT, FOREIGN KEY(" + COLUMN_CODES_USER_ID + ") REFERENCES "
            + TABLE_USER + "(" + COLUMN_USER_ID + "))";

    private static final SQLiteDatabase.CursorFactory factory = null;


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Executing create statements
        sqLiteDatabase.execSQL(CREATE_TABLE_USER);
        sqLiteDatabase.execSQL(CREATE_TABLE_CODES);
/*
        sqLiteDatabase.execSQL("CREATE TABLE user(user_id INT PRIMARY KEY AUTOINCREMENT, email TEXT, password TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE codes(code_id INT PRIMARY KEY AUTOINCREMENT, feature TEXT, code TEXT, user_id INT, FOREIGN KEY(user_id) REFERENCES user(user_id))");
*/

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //Drop new tables on upgrade
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CODES);

        //Create new tables
        onCreate(sqLiteDatabase);
    }

//    public long createUser(User user){
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_USER_EMAIL, user.getEmail());
//        values.put(COLUMN_USER_PASSWORD, user.getPassword());
//
//        //Insert row
//        long user_id = db.insert(TABLE_USER, null, values);
//        return user_id;
//    }
//
//    public long createCode(Code code){
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_CODES_CODE, code.getCode());
//        values.put(COLUMN_CODES_FEATURE, code.getFeature());
//        values.put(COLUMN_CODES_USER_ID, code.getUserID());
//        long code_id = db.insert(TABLE_CODES, null, values);
//        return code_id;
//    }

    public boolean createUser(String email, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_EMAIL, email);
        values.put(COLUMN_USER_PASSWORD, password);
        db.beginTransaction();
        long ins = db.insert(TABLE_USER, null, values);

        db.execSQL("INSERT INTO " + TABLE_CODES + "(" + COLUMN_CODES_FEATURE + ") VALUES('Location Retrieval')");
        db.execSQL("INSERT INTO " + TABLE_CODES + "(" + COLUMN_CODES_FEATURE + ") VALUES('Contact Retrieval')");
        db.execSQL("INSERT INTO " + TABLE_CODES + "(" + COLUMN_CODES_FEATURE + ") VALUES('Enable Ringer')");

        db.setTransactionSuccessful();
        db.endTransaction();

        if(ins == -1)
            return false;
        else
            return true;
    }




    //Checking if email exists
    public Boolean checkEmail(String email){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM user WHERE email = ?", new String[]{email});
        return cursor.getCount() <= 0;

    }
}
