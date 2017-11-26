package com.example.xin.fileprotector;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Base64;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class Encryptor {
    private static final String ALGORITHM = "AES/CBC/PKCS7Padding";
    private static final int BUFFER_SIZE = 1024;
    //private FileInputStream fis = null;
    private byte iv[];
    private FileOutputStream fos = null;
    private KeyStore keyStore = null;

    Encryptor(final KeyStore keyStore) {
        this.keyStore = keyStore;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    boolean encryptFile(final InputStream fis, final File outputFile, final String alias)
            throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException,
            InvalidAlgorithmParameterException, InvalidKeyException, KeyStoreException,
            UnrecoverableEntryException {

        final Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(alias));
        boolean success = true;
        CipherInputStream cis = null;
        iv = cipher.getIV();

        try {
            if (!outputFile.getParentFile().exists()) {
                outputFile.getParentFile().mkdirs();
            }
            outputFile.createNewFile();
            fos = new FileOutputStream(outputFile);
            cis = new CipherInputStream(fis, cipher);
            byte[] buffer = new byte[BUFFER_SIZE];

            for (;;) {
                final int n = cis.read(buffer);
                if (n == -1) {
                    break;
                }
                fos.write(buffer, 0, n);
            }
            fos.flush();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            success = false;
        }
        catch (IOException e) {
            e.printStackTrace();
            success = false;
        }
        finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    cis.close();
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return success;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    String encryptText(String textToEncrypt, final String alias) throws NoSuchPaddingException,
            NoSuchAlgorithmException , NoSuchProviderException, InvalidAlgorithmParameterException,
            KeyStoreException, InvalidKeyException, UnrecoverableEntryException,
            UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {

        final Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(alias));
        iv = cipher.getIV();

        byte[] result = cipher.doFinal(textToEncrypt.getBytes("UTF-8"));
        return Base64.encodeToString(result, Base64.DEFAULT);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @NonNull
    private SecretKey getSecretKey(final String alias) throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidAlgorithmParameterException, KeyStoreException,
            UnrecoverableEntryException {

        SecretKey key = null;

        if (!keyStore.containsAlias(alias)) {
            final KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,
                    "AndroidKeyStore");

            keyGenerator.init(
                    new KeyGenParameterSpec.Builder(
                            alias,
                            KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                            .build());
            key = keyGenerator.generateKey();
        }
        else {
            return ((KeyStore.SecretKeyEntry) keyStore.getEntry(alias, null)).getSecretKey();
        }

        return key;
    }

    public byte[] getIv() {
        return iv;
    }
}
