package com.example.xin.fileprotector.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.xin.fileprotector.util.FileType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileTable {

    private final SQLiteOpenHelper helper;

    private static final String FILE_TABLE_NAME = "files";

    private static final String COLUMN_FILE_ID = "file_id";
    private static final String COLUMN_EN_FILE_NAME = "encrypted_file_name";
    private static final String COLUMN_ORIGINAL_PATH = "original_path";
    private static final String COLUMN_FILE_TYPE = "file_type";
    private static final String COLUMN_KEY = "key";
    /** true = 1, false = 0 */
    private static final String COLUMN_IS_ENCRYPTED = "is_encrypted";

    private static final String CREATE_FILE_TABLE_QUERY = "CREATE TABLE " + FILE_TABLE_NAME + "("
                        + COLUMN_FILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + COLUMN_EN_FILE_NAME + " TEXT,"
                        + COLUMN_ORIGINAL_PATH + " TEXT UNIQUE,"
                        + COLUMN_FILE_TYPE + " TEXT,"
                        + COLUMN_KEY + " TEXT,"
                        + COLUMN_IS_ENCRYPTED + " INTEGER" + ")";

    private static final String DROP_FILE_TABLE_QUERY = "DROP TABLE IF EXISTS " + FILE_TABLE_NAME;

    public FileTable(SQLiteOpenHelper helper) {
        this.helper = helper;
    }

    public void onCreate(final SQLiteDatabase db) {
        db.execSQL(CREATE_FILE_TABLE_QUERY);
    }

    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        db.execSQL(DROP_FILE_TABLE_QUERY);
        onCreate(db);
    }

    public void addOrUpdateFile(final FileInfo fileInfo) {
        final SQLiteDatabase db = helper.getWritableDatabase();

        db.beginTransaction();

        final Cursor cc = db.query(FILE_TABLE_NAME, new String[]{ COLUMN_EN_FILE_NAME },
                COLUMN_ORIGINAL_PATH + " = ?", new String[]{ fileInfo.getOriginalPath() },
                null, null, null);

        final ContentValues values = new ContentValues();
        values.put(COLUMN_EN_FILE_NAME, fileInfo.getEncryptedFileName());
        values.put(COLUMN_FILE_TYPE, fileInfo.getType());
        values.put(COLUMN_KEY, fileInfo.getKey());
        values.put(COLUMN_IS_ENCRYPTED, (fileInfo.isEncrypted() ? 1 : 0));

        final String oldEncFileToRemove;

        if (cc.getCount() == 0) {
            oldEncFileToRemove = null;
            values.put(COLUMN_ORIGINAL_PATH, fileInfo.getOriginalPath());
            db.insert(FILE_TABLE_NAME, null, values);
        } else {
            cc.moveToFirst();
            oldEncFileToRemove = cc.getString(0);
            db.update(FILE_TABLE_NAME, values,
                    COLUMN_ORIGINAL_PATH + " = ?", new String[]{ String.valueOf(fileInfo.getOriginalPath()) });
        }

        cc.close();

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

        if (oldEncFileToRemove != null) {
            new File(oldEncFileToRemove).delete();
        }
    }

    public List<FileInfo> getFileByType(final FileType type) {
        final String selection =
                COLUMN_FILE_TYPE + " = ? AND " +
                COLUMN_IS_ENCRYPTED + " = 1";
        final String[] selectionArgs = {type.toString()};

        return getFiles(selection, selectionArgs);
    }

    public List<FileInfo> getDecryptedFiles() {
        final String selection = COLUMN_IS_ENCRYPTED + " = ?";
        final String[] selectionArgs = {"0"};

        return getFiles(selection, selectionArgs);
    }

    private List<FileInfo> getFiles(final String selection, final String[] selectionArgs) {
        final String[] columns = {
                COLUMN_FILE_ID,
                COLUMN_ORIGINAL_PATH,
                COLUMN_EN_FILE_NAME,
                COLUMN_FILE_TYPE,
                COLUMN_KEY,
                COLUMN_IS_ENCRYPTED
        };

        final SQLiteDatabase db = helper.getReadableDatabase();

        final List<FileInfo> fileInfoList = new ArrayList<>();

        final Cursor cursor = db.query(FILE_TABLE_NAME, //Table to query
                columns,                          //columns to return
                selection,                        //columns for the WHERE clause
                selectionArgs,                    //The values for the WHERE clause
                null,                     //group the rows
                null,                      //filter by row groups
                null);                    //The sort order

        if (cursor.moveToFirst()) {
            do {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_FILE_ID))));
                fileInfo.setEncryptedFileName(cursor.getString(cursor.getColumnIndex(COLUMN_EN_FILE_NAME)));
                fileInfo.setOriginalPath(cursor.getString(cursor.getColumnIndex(COLUMN_ORIGINAL_PATH)));
                fileInfo.setType(FileType.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_FILE_TYPE))));
                fileInfo.setKey(cursor.getString(cursor.getColumnIndex(COLUMN_KEY)));
                fileInfo.setEncryptionStatus(1 == cursor.getColumnIndex(COLUMN_IS_ENCRYPTED));
                fileInfoList.add(fileInfo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return fileInfoList;
    }

    public boolean tableEmpty() {
        final SQLiteDatabase db = helper.getReadableDatabase();
        final String count = "SELECT count(*) FROM " + FILE_TABLE_NAME;

        final Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        final int icount = mcursor.getInt(0);

        return icount <= 0;
    }

    public void setFileDecrypted(final int id) {
        final SQLiteDatabase db = helper.getWritableDatabase();

        final ContentValues values = new ContentValues();
        values.put(COLUMN_IS_ENCRYPTED, 0);

        db.update(FILE_TABLE_NAME, values,
                COLUMN_FILE_ID + " = ?", new String[]{ String.valueOf(id) });
        db.close();
    }
}
