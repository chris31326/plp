package com.example.xin.fileprotector;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.NoSuchPaddingException;

public class TestActivity extends AppCompatActivity {
    private Button btn;
    private Encryptor encryptor;
    private Decryptor decryptor;
    private static final String ALIAS = "hellohello";//TODO:For test only
    private KeyStore keyStore = null;

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
        decryptor = new Decryptor(keyStore);

//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("*/*");//FILE TYPE, */*stands for any type
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                startActivityForResult(intent,1);
//            }
//        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                Uri uri = data.getData();
                ContentResolver cr = getContentResolver();
                java.io.File sourceFile = new java.io.File(uri.getPath());
                java.io.File destFile = new java.io.File(getFilesDir(), sourceFile.getName() + "cipher");
                //TODO: encrypt filename
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
            }
        }
    }

    public void onClickButton2(View view) {
//        Intent intent = new Intent("android.intent.action.VIEW");
//        intent.addCategory("android.intent.category.DEFAULT");
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setDataAndType(Uri.fromFile(file), "image/*");
//        startActivity(intent);
    }

    public void onClickButtonGetFilesList(View view) {
        String[] fList = fileList();
        StringBuilder sb = new StringBuilder();
        for (int i = 0, len = fList.length; i < len; i++) {
            sb.append(fList[i]);
            sb.append("\n");
            System.out.println(fList[i]);
        }
        Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show();
    }

    public void deleteKey(String alias) throws KeyStoreException {
        keyStore.deleteEntry(alias);
    }

    public boolean delete(String fName) {
        return deleteFile(fName);
    }

    public boolean delete(java.io.File file) {
        return deleteFile(file.getName());
    }

    public void onClickButton(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//FILE TYPE, */*stands for any type
        startActivityForResult(intent,1);
    }

}
