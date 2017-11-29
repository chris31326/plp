package com.example.xin.fileprotector.activity.filelist;

import android.content.Context;
import android.view.View;

import com.example.xin.fileprotector.db.DBHelper;
import com.example.xin.fileprotector.db.FileInfo;

public class ReEncryptOnClick implements ItemClickListener {
    private final Context context;
    private final DBHelper dbHelper;

    public ReEncryptOnClick(final Context context, final DBHelper dbHelper) {
        this.context = context;
        this.dbHelper = dbHelper;
    }

    @Override
    public void onItemClick(final View view, final FileInfo fileInfo) {
    }
}
