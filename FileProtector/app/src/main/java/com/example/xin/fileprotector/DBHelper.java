package com.example.xin.fileprotector;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "DBHelper.db";

    public final UserTable userTable;
    public final FileTable fileTable;

    /**
     * Constructor
     *
     * @param context
     */
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        userTable = new UserTable(this);
        fileTable = new FileTable(this);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        userTable.onCreate(db);
        fileTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        userTable.onUpgrade(db, oldVersion, newVersion);
        fileTable.onUpgrade(db, oldVersion, newVersion);
    }
}
