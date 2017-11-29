package com.example.xin.fileprotector.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.aditya.filebrowser.Constants;
import com.aditya.filebrowser.FileChooser;
import com.example.xin.fileprotector.R;
import com.example.xin.fileprotector.activity.filelist.FileListActivity;
import com.example.xin.fileprotector.crypto.CryptoFactory;
import com.example.xin.fileprotector.crypto.Encryptor;
import com.example.xin.fileprotector.db.DBHelper;
import com.example.xin.fileprotector.db.FileInfo;
import com.example.xin.fileprotector.util.FileType;
import com.example.xin.fileprotector.util.Util;

import org.apache.commons.codec.binary.Hex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.util.ArrayList;

import javax.crypto.NoSuchPaddingException;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;

public class MainActivity extends AppCompatActivity {
    private static final int ADD_FILES_REQUEST_CODE = 7;
    private Encryptor encryptor;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        encryptor = CryptoFactory.encryptor;
        dbHelper = DBHelper.getInstance(this);

        findViewById(R.id.add_photos).bringToFront();
        findViewById(R.id.add_files).bringToFront();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        final ArrayList<String> files = new ArrayList<>();
        switch (requestCode) {
        case FilePickerConst.REQUEST_CODE_PHOTO:
            if (resultCode == RESULT_OK && data != null) {
                files.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
            }
            break;
        case ADD_FILES_REQUEST_CODE:
            if (resultCode == RESULT_OK && data != null) {
                final ArrayList<Uri> selectedFiles = data.getParcelableArrayListExtra(Constants.SELECTED_ITEMS);
                for (final Uri fileUri : selectedFiles) {
                    files.add(fileUri.getPath());
                }
            }
            break;
        }

        encryptFiles(files);
    }

    private void encryptFiles(final ArrayList<String> files) {
        for (final String fileName : files) {
            final File file = new File(fileName);
            encryptFile(file);
        }
    }

    private void encryptFile(final File sourceFile) {
        final InputStream is;
        try {
            is = new FileInputStream(sourceFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "FAIL: FILE NOT FOUND: " +
                    sourceFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            return;
        }

        final String destFileName = sourceFile.getName() + "." + System.currentTimeMillis() + ".plp";
        final File destFile = new File(getFilesDir(), destFileName);

        try {
            final boolean success = encryptor.encryptFile(is, destFile, CryptoFactory.ALIAS);
            if (success) {
                Toast.makeText(this, "FILE(S) ENCRYPTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "FAIL: " +
                        sourceFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            }
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | NoSuchProviderException
                | InvalidAlgorithmParameterException | InvalidKeyException
                | KeyStoreException | UnrecoverableEntryException e) {
            e.printStackTrace();
            Toast.makeText(this, "FAIL: EXCEPTION WHEN ENCRYPTING" +
                    sourceFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        }

        final FileInfo fileInfo = new FileInfo();
        fileInfo.setEncryptedFileName(destFile.getAbsolutePath());
        fileInfo.setOriginalPath(sourceFile.getAbsolutePath());
        fileInfo.setType(FileType.fromFile(sourceFile));
        fileInfo.setKey(new String(Hex.encodeHex(encryptor.getIv())));
        fileInfo.setEncryptionStatus(true);

        dbHelper.fileTable.addOrUpdateFile(fileInfo);

        sourceFile.delete();

        Util.rescanFile(this, sourceFile);
    }

    public void onClickAddPhotos(final View view) {
        if (!checkAndGetPermissions()) {
            return;
        }

        FilePickerBuilder.getInstance()
                .enableImagePicker(true)
                .enableVideoPicker(true)
                .setSelectedFiles(new ArrayList<>())
                .pickPhoto(this);
    }

    public void onClickAddFiles(final View view) {
        if (!checkAndGetPermissions()) {
            return;
        }

        final Intent intent = new Intent(this, FileChooser.class);
        intent.putExtra(Constants.SELECTION_MODE, Constants.SELECTION_MODES.MULTIPLE_SELECTION.ordinal());
        startActivityForResult(intent, ADD_FILES_REQUEST_CODE);
    }

    private boolean checkAndGetPermissions() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "INSUFFICIENT PERMISSIONS", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    public void onClickFolder(final View view) {
        final Intent intent = new Intent(this, FileListActivity.class);
        intent.putExtra("FileType", ((Button)view).getText().toString());
        startActivity(intent);
    }
}
