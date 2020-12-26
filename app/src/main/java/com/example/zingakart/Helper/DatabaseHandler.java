package com.example.zingakart.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String TAG = DatabaseHandler.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "zingakart";

    // table name
    private static final String TABLE_USER = "user";

    //Columns names for user Table
    private static final String KEY_ID = "id";
    private static final String KEY_UID = "uid";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NAME = "name";
    private static final String KEY_MOBILE = "mobile";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_CITY = "city";
    private static final String KEY_STATE = "state";
    private static final String KEY_COUNTRY = "country";
    private static final String KEY_LANDMARK = "landmark";
    private static final String KEY_ZIPCODE = "zipcode";
    private static final String KEY_IFSC = "ifsc";
    private static final String KEY_BENEFICIARYNAME = "beneficiary";
    private static final String KEY_BANKNAME = "bank";
    private static final String KEY_ACCOUNTNUMBER = "account";

    private static final String KEY_URL = "url";

    String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_UID + " TEXT,"
            + KEY_NAME + " TEXT,"
            + KEY_EMAIL + " TEXT,"
            + KEY_URL + " TEXT,"
            + KEY_MOBILE + " TEXT,"
            + KEY_ADDRESS + " TEXT,"
            + KEY_CITY + " TEXT,"
            + KEY_STATE + " TEXT,"
            + KEY_COUNTRY + " TEXT,"
            + KEY_LANDMARK + " TEXT,"
            + KEY_ZIPCODE + " TEXT,"
            + KEY_IFSC + " TEXT,"
            + KEY_BENEFICIARYNAME + " TEXT,"
            + KEY_BANKNAME + " TEXT,"
            + KEY_ACCOUNTNUMBER + " TEXT" + ")";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LOGIN_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    public void addUser(String id, String name, String email, String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_UID, id);
        values.put(KEY_NAME, name);
        values.put(KEY_EMAIL, email);
        values.put(KEY_URL, url);
//        values.put(KEY_MOBILE, mobile);
//        values.put(KEY_ADDRESS, address);
//        values.put(KEY_CITY, city);
//        values.put(KEY_STATE, state);
//        values.put(KEY_COUNTRY, country);
//        values.put(KEY_LANDMARK, landmark);
//        values.put(KEY_ZIPCODE, zipcode);
//        values.put(KEY_IFSC, ifsc);
//        values.put(KEY_BENEFICIARYNAME, beneficiary);
//        values.put(KEY_BANKNAME, bank);
//        values.put(KEY_ACCOUNTNUMBER, account);
        db.insert(TABLE_USER, null, values);
        db.close();
    }

    public void updateUser(String uid, String name, String url, String mobile, String address, String city, String state,
                           String country, String landmark, String zipcode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_URL, url);
        values.put(KEY_MOBILE, mobile);
        values.put(KEY_ADDRESS, address);
        values.put(KEY_CITY, city);
        values.put(KEY_STATE, state);
        values.put(KEY_COUNTRY, country);
        values.put(KEY_LANDMARK, landmark);
        values.put(KEY_ZIPCODE, zipcode);
        db.update(TABLE_USER, values, KEY_UID + "=" + uid, null);
        db.close();
    }

    public void updateBankDetails(String uid, String ifsc, String beneficiary, String bank, String account) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_IFSC, ifsc);
        values.put(KEY_BENEFICIARYNAME, beneficiary);
        values.put(KEY_BANKNAME, bank);
        values.put(KEY_ACCOUNTNUMBER, account);
        db.update(TABLE_USER, values, KEY_UID + "=" + uid, null);
        db.close();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("id", cursor.getString(0));
            user.put("uid", cursor.getString(1));
            user.put("name", cursor.getString(2));
            user.put("email", cursor.getString(3));
            user.put("url", cursor.getString(4));
            user.put("mobile", cursor.getString(5));
            user.put("address", cursor.getString(6));
            user.put("city", cursor.getString(7));
            user.put("state", cursor.getString(8));
            user.put("country", cursor.getString(9));
            user.put("landmark", cursor.getString(10));
            user.put("zipcode", cursor.getString(11));
            user.put("ifsc", cursor.getString(12));
            user.put("beneficiary", cursor.getString(13));
            user.put("bank", cursor.getString(14));
            user.put("account", cursor.getString(15));
        }
        cursor.close();
        db.close();
        return user;
    }

    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER, null, null);
        db.close();
    }
}
