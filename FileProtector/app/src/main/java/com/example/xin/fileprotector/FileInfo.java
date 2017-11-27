package com.example.xin.fileprotector;

public class FileInfo {
    private int id;
    private String encryptedFileName;
    private String originalPath;
    private boolean isEncrypted;
    private String key;
    private FileType type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEncryptedFileName() {
        return encryptedFileName;
    }

    public void setEncryptedFileName(String encryptedFileName) {
        this.encryptedFileName = encryptedFileName;
    }

    public String getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type.toString();
    }

    public void setType(FileType type) {
        this.type = type;
    }


    public boolean isEncrypted() {
        return isEncrypted;
    }

    public void setEncryptionStatus(boolean isEncrypted) {
        this.isEncrypted = isEncrypted;
    }
}
