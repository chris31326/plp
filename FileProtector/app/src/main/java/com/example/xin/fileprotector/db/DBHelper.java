package com.example.xin.fileprotector.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "DBHelper.db";
    public UserTable userTable;
    public FileTable fileTable;

    public DBHelper(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        userTable = new UserTable(this);
        fileTable = new FileTable(this);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        userTable.onCreate(db);
        fileTable.onCreate(db);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        userTable.onUpgrade(db, oldVersion, newVersion);
        fileTable.onUpgrade(db, oldVersion, newVersion);
    }
}
