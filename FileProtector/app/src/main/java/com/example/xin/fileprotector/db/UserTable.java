package com.example.xin.fileprotector.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class UserTable {

    private final SQLiteOpenHelper helper;
    private static final String TABLE_USER = "user";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USER_NAME = "user_name";
    private static final String COLUMN_USER_EMAIL = "user_email";
    private static final String COLUMN_USER_PASSWORD = "user_password";

    private static final String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                        + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + COLUMN_USER_NAME + " TEXT,"
                        + COLUMN_USER_EMAIL + " TEXT UNIQUE,"
                        + COLUMN_USER_PASSWORD + " TEXT" + ")";

    private static final String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;

    public UserTable(final SQLiteOpenHelper helper) {
        this.helper = helper;
    }

    public void onCreate(final SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
    }

    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        db.execSQL(DROP_USER_TABLE);
        onCreate(db);
    }

    public void addUser(final User user) {
        final SQLiteDatabase db = helper.getWritableDatabase();

        final ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());

        // Inserting Row
        db.insert(TABLE_USER, null, values);
        db.close();
    }

    public boolean checkUserAlreadyExists(final String email) {
        final String selection = COLUMN_USER_EMAIL + " = ?";
        final String[] selectionArgs = {email};

        return hasUsersThatMatch(selection, selectionArgs);
    }

    public boolean checkPassword(final String password) {
        final String selection = COLUMN_USER_PASSWORD + " = ?";
        final String[] selectionArgs = { password };

        return hasUsersThatMatch(selection, selectionArgs);
    }

    public boolean hasUsersAlready() {
        return hasUsersThatMatch(null, null);
    }

    private boolean hasUsersThatMatch(final String selection, final String[] selectionArgs) {
        final String[] columns = {COLUMN_USER_ID};
        final SQLiteDatabase db = helper.getReadableDatabase();

        final Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,               //group the rows
                null,                //filter by row groups
                null);              //The sort order

        final int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        return cursorCount > 0;
    }

    public String getRegisteredUserEmail() {
        final String[] columns = {COLUMN_USER_EMAIL};
        final SQLiteDatabase db = helper.getReadableDatabase();

        final Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                null,               //columns for the WHERE clause
                null,           //The values for the WHERE clause
                null,               //group the rows
                null,                //filter by row groups
                null);              //The sort order

        final String email;
        if (cursor.moveToFirst()) {
            email = cursor.getString(0);
        } else {
            email = null;
        }

        cursor.close();
        db.close();

        return email;
    }
}
