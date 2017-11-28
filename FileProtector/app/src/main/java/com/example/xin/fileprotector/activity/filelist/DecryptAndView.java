package com.example.xin.fileprotector.activity.filelist;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.example.xin.fileprotector.db.FileInfo;

public class DecryptAndView implements ItemClickListener {
    private final Context context;

    public DecryptAndView(final Context context) {
        this.context = context;
    }

    @Override
    public void onItemClick(final View view, final FileInfo fileInfo) {
        Toast.makeText(context, fileInfo.getEncryptedFileName(), Toast.LENGTH_SHORT).show();


    }
}
