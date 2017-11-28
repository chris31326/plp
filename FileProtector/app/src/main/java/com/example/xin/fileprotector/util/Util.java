package com.example.xin.fileprotector.util;

import android.content.Context;
import android.media.MediaScannerConnection;

import java.io.File;

public class Util {
    public static void rescanFile(final Context context, final File file) {
        MediaScannerConnection.scanFile(context, new String[]{ file.getAbsolutePath() }, null, null);
    }

    public static String getFileExt(final String name) {
        final int dot = name.lastIndexOf('.');
        if (dot == -1) {
            return "";
        }
        return name.substring(dot+1);
    }
}
