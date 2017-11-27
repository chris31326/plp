package com.example.xin.fileprotector;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import org.apache.commons.codec.binary.Hex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import javax.crypto.NoSuchPaddingException;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;

public class MainActivity extends AppCompatActivity {

    private Encryptor encryptor;
    private KeyStore keyStore = null;
    private static final String ALIAS = "hellohello";//TODO:For test only
    private DBHelper dbHelper;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
        } catch (KeyStoreException | NoSuchAlgorithmException | IOException | CertificateException e) {
            e.printStackTrace();
        }
        encryptor = new Encryptor(keyStore);
        dbHelper = new DBHelper(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.bringToFront();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        final ArrayList<String> files = new ArrayList<>();
        switch (requestCode) {
        case FilePickerConst.REQUEST_CODE_PHOTO:
            if (resultCode == Activity.RESULT_OK && data != null) {
                files.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
            }
            break;
        case FilePickerConst.REQUEST_CODE_DOC:
            if (resultCode == Activity.RESULT_OK && data != null) {
                files.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
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

        final File destFile = new File(getFilesDir(), sourceFile.getName() + ".plp");

        try {
            final boolean success = encryptor.encryptFile(is, destFile, ALIAS);
            if (success) {
                Toast.makeText(this, "FILE ENCRYPTED", Toast.LENGTH_SHORT).show();
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

        dbHelper.fileTable.addFile(fileInfo);
    }

    public void onClickViewAllFiles(final View view) {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "INSUFFICIENT PERMISSIONS", Toast.LENGTH_SHORT).show();
        } else {
            FilePickerBuilder.getInstance()
                    .enableImagePicker(true)
                    .enableVideoPicker(true)
                    .setSelectedFiles(new ArrayList<>())
                    .pickPhoto(this);
        }
    }

    public void onClickFolderPhoto(final View view) {
        final Intent intent = new Intent(this, FileListActivity.class);
        intent.putExtra("FileType", FileType.Photo.toString());
        startActivity(intent);
    }
}
