package com.example.xin.fileprotector;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class FileTable {
    private final SQLiteOpenHelper helper;
    // file table name
    private static final String FILE_TABLE_NAME = "Files";
    // file Table Columns names
    private static final String COLUMN_FILE_ID = "file_id";
    private static final String COLUMN_EN_FILE_NAME = "encrypted_file_name";
    private static final String COLUMN_ORIGINAL_PATH = "original_path";
    private static final String COLUMN_FILE_TYPE = "file_type";
    private static final String COLUMN_KEY = "key";
    //true = 1, false = 0
    private static final String COLUMN_IS_ENCRYPTED = "is_encrypted";

    // create table sql query
    private String CREATE_FILE_TABLE = "CREATE TABLE " + FILE_TABLE_NAME + "("
            + COLUMN_FILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_EN_FILE_NAME + " TEXT,"
            + COLUMN_ORIGINAL_PATH + " TEXT,"
            + COLUMN_FILE_TYPE + " TEXT,"
            + COLUMN_KEY + "TEXT,"
            + COLUMN_IS_ENCRYPTED + "INTEGER" + ")";

    // drop table sql query
    private String DROP_file_TABLE = "DROP TABLE IF EXISTS " + FILE_TABLE_NAME;

    public FileTable(SQLiteOpenHelper helper) {
        this.helper = helper;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FILE_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop file Table if exist
        db.execSQL(DROP_file_TABLE);

        // Create tables again
        onCreate(db);
    }

    public void addFile(File file) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_EN_FILE_NAME, file.getEncryptedFileName());
        values.put(COLUMN_ORIGINAL_PATH, file.getOriginalPath());
        values.put(COLUMN_FILE_TYPE, file.getType());
        values.put(COLUMN_KEY, file.getKey());
        values.put(COLUMN_IS_ENCRYPTED, (file.isEncrypted() ? 1 : 0));

        // Inserting Row
        db.insert(FILE_TABLE_NAME, null, values);
        db.close();
    }

    public List<File> getAllFile() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_FILE_ID,
                COLUMN_ORIGINAL_PATH,
                COLUMN_EN_FILE_NAME,
                COLUMN_FILE_TYPE,
                COLUMN_KEY,
                COLUMN_IS_ENCRYPTED
        };
        // sorting orders
        //String sortOrder = COLUMN_EN_FILE_NAME + " ASC";
        List<File> fileList = new ArrayList<>();

        SQLiteDatabase db = helper.getReadableDatabase();

        // query the file table
        Cursor cursor = db.query(FILE_TABLE_NAME, //Table to query
                columns,                          //columns to return
                null,                    //columns for the WHERE clause
                null,                 //The values for the WHERE clause
                COLUMN_FILE_TYPE,                 //group the rows
                null,                      //filter by row groups
                null);                    //The sort order


        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                File file = new File();
                file.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_FILE_ID))));
                file.setEncryptedFileName(cursor.getString(cursor.getColumnIndex(COLUMN_EN_FILE_NAME)));
                file.setOriginalPath(cursor.getString(cursor.getColumnIndex(COLUMN_ORIGINAL_PATH)));
                file.setType(File.FileType.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_FILE_TYPE))));
                file.setKey(cursor.getString(cursor.getColumnIndex(COLUMN_KEY)));
                file.setEncryptionStatus(1 == cursor.getColumnIndex(COLUMN_IS_ENCRYPTED));
                // Adding file record to list
                fileList.add(file);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return file list
        return fileList;
    }

    public void updateFile(File file) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_EN_FILE_NAME, file.getEncryptedFileName());
        values.put(COLUMN_ORIGINAL_PATH, file.getOriginalPath());
        values.put(COLUMN_FILE_TYPE, file.getType());
        values.put(COLUMN_KEY, file.getKey());
        values.put(COLUMN_IS_ENCRYPTED, (file.isEncrypted() ? 1 : 0));

        // updating row
        db.update(FILE_TABLE_NAME, values, COLUMN_FILE_ID + " = ?", new String[]{ String.valueOf(file.getId()) });
        db.close();
    }

    public void deletefile(File file) {
        SQLiteDatabase db = helper.getWritableDatabase();
        // delete file record by id
        db.delete(FILE_TABLE_NAME, COLUMN_FILE_ID + " = ?", new String[]{ String.valueOf(file.getId()) });
        db.close();
    }

    public List<File> getFileByType(File.FileType type) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_FILE_ID,
                COLUMN_ORIGINAL_PATH,
                COLUMN_EN_FILE_NAME,
                COLUMN_FILE_TYPE,
                COLUMN_KEY,
                COLUMN_IS_ENCRYPTED
        };

        String selection = COLUMN_FILE_TYPE + " = ?";
        // selection arguments
        String[] selectionArgs = { type.toString() };
        List<File> fileList = new ArrayList<>();

        SQLiteDatabase db = helper.getReadableDatabase();

        // query the file table
        Cursor cursor = db.query(FILE_TABLE_NAME, //Table to query
                columns,                          //columns to return
                selection,                        //columns for the WHERE clause
                selectionArgs,                    //The values for the WHERE clause
                null,                     //group the rows
                null,                      //filter by row groups
                null);                    //The sort order

        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                File file = new File();
                file.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_FILE_ID))));
                file.setEncryptedFileName(cursor.getString(cursor.getColumnIndex(COLUMN_EN_FILE_NAME)));
                file.setOriginalPath(cursor.getString(cursor.getColumnIndex(COLUMN_ORIGINAL_PATH)));
                file.setType(File.FileType.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_FILE_TYPE))));
                file.setKey(cursor.getString(cursor.getColumnIndex(COLUMN_KEY)));
                file.setEncryptionStatus(1 == cursor.getColumnIndex(COLUMN_IS_ENCRYPTED));
                // Adding file record to list
                fileList.add(file);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return file list
        return fileList;
    }

    public List<File> getDecryptedFiles() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_FILE_ID,
                COLUMN_ORIGINAL_PATH,
                COLUMN_EN_FILE_NAME,
                COLUMN_FILE_TYPE,
                COLUMN_KEY,
                COLUMN_IS_ENCRYPTED
        };
        String selection = COLUMN_IS_ENCRYPTED + " = ?";
        // selection arguments
        String[] selectionArgs = { "0" };
        List<File> fileList = new ArrayList<>();

        SQLiteDatabase db = helper.getReadableDatabase();

        // query the file table
        Cursor cursor = db.query(FILE_TABLE_NAME, //Table to query
                columns,                          //columns to return
                selection,                        //columns for the WHERE clause
                selectionArgs,                    //The values for the WHERE clause
                null,                     //group the rows
                null,                      //filter by row groups
                null);                    //The sort order

        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                File file = new File();
                file.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_FILE_ID))));
                file.setEncryptedFileName(cursor.getString(cursor.getColumnIndex(COLUMN_EN_FILE_NAME)));
                file.setOriginalPath(cursor.getString(cursor.getColumnIndex(COLUMN_ORIGINAL_PATH)));
                file.setType(File.FileType.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_FILE_TYPE))));
                file.setKey(cursor.getString(cursor.getColumnIndex(COLUMN_KEY)));
                file.setEncryptionStatus(1 == cursor.getColumnIndex(COLUMN_IS_ENCRYPTED));
                // Adding file record to list
                fileList.add(file);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return file list
        return fileList;
    }
}
