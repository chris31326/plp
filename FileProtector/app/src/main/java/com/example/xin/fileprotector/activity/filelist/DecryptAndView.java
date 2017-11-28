package com.example.xin.fileprotector.activity.filelist;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.example.xin.fileprotector.crypto.CryptoFactory;
import com.example.xin.fileprotector.db.FileInfo;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.File;

public class DecryptAndView implements ItemClickListener {
    private final Context context;

    public DecryptAndView(final Context context) {
        this.context = context;
    }

    @Override
    public void onItemClick(final View view, final FileInfo fileInfo) {
        final File encryptedFile = new File(fileInfo.getEncryptedFileName());
        //final File outFile = new File(encryptedFile.getParentFile(), new File(fileInfo.getOriginalPath()).getName());
        final File outFile = new File(Environment.getExternalStorageDirectory(), new File(fileInfo.getOriginalPath()).getName());
        final byte[] iv;
        try {
            iv = Hex.decodeHex(fileInfo.getKey().toCharArray());
        } catch (DecoderException e) {
            throw new IllegalStateException(e);
        }

        try {
            CryptoFactory.decryptor.decryptFile(encryptedFile, outFile, CryptoFactory.ALIAS, iv);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        final String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(getExt(outFile.getName()));

        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(outFile), mimeType);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No app for this type of file found.", Toast.LENGTH_LONG).show();
        }
    }

    private String getExt(final String name) {
        final int dot = name.lastIndexOf('.');
        if (dot == -1) {
            return "";
        }
        return name.substring(dot+1);
    }
}