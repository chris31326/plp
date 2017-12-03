package com.example.xin.fileprotector.util;

import java.io.File;

public enum FileType {
    Photo(new String[]{".jpg", ".jpeg", ".png", ".gif"}),
    Audio(new String[]{".mp3", ".wav", ".aac", ".m4a", ".wma", ".ogg"}),
    Video(new String[]{".avi", ".mp4", ".mkv", ".webp"}),
    Document(new String[]{".doc", ".docx", ".xls", ".xlsx", ".pdf", ".txt"}),
    Others(new String[]{});

    final public String[] fileExtensions;

    FileType(final String[] fileExtensions) {
        this.fileExtensions = fileExtensions;
    }

    public static FileType fromFile(final File file) {
        for (final FileType type : FileType.values()) {
            for (final String ext : type.fileExtensions) {
                if (file.getName().endsWith(ext)) {
                    return type;
                }
            }
        }
        return Others;
    }
}
