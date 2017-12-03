package com.example.xin.fileprotector.activity.filelist;

import android.view.View;

import com.example.xin.fileprotector.db.FileInfo;

public interface ItemClickListener {
    void onItemClick(View view, final FileInfo fileInfo);
}
