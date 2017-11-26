package com.example.xin.fileprotector;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.apache.commons.codec.binary.Hex;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.NoSuchPaddingException;

public class MainActivity extends AppCompatActivity {

    private Encryptor encryptor;
    private KeyStore keyStore = null;
    private static final String ALIAS = "hellohello";//TODO:For test only
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FileInfo fileInfo = new FileInfo();

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                Uri uri = data.getData();
                ContentResolver cr = getContentResolver();
                java.io.File sourceFile = new java.io.File(uri.getPath());
                java.io.File destFile = new java.io.File(getFilesDir(), sourceFile.getName() + "cipher");

                try {
                    boolean success = encryptor.encryptFile(cr.openInputStream(uri), destFile, ALIAS);
                    if (success)
                        Toast.makeText(this, "FILE ENCRYPTED", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(this, "FAIL: " +
                                sourceFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "FAIL: FILE NOT FOUND" +
                            sourceFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                }
                catch (NoSuchPaddingException | NoSuchAlgorithmException | NoSuchProviderException
                        | InvalidAlgorithmParameterException | InvalidKeyException
                        | KeyStoreException | UnrecoverableEntryException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "FAIL: EXCEPTION WHEN ENCRYPTING" +
                            sourceFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                }

                fileInfo.setEncryptedFileName(destFile.getAbsolutePath());
                fileInfo.setOriginalPath(sourceFile.getAbsolutePath());
                fileInfo.setType(FileInfo.FileType.Photo);
                fileInfo.setKey(new String(Hex.encodeHex(encryptor.getIv())));

                dbHelper.fileTable.addFile(fileInfo);
            }
        }
    }

    public void onClickViewAllFiles(final View view) {
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.setType("*/*");//FILE TYPE, */*stands for any type
        startActivityForResult(intent,1);
    }

    public void onClickFolderPhoto(final View view) {
        final Intent intent = new Intent(this, FileListActivity.class);
        intent.putExtra("FileType", FileInfo.FileType.Photo.toString());
        startActivity(intent);
    }
}
