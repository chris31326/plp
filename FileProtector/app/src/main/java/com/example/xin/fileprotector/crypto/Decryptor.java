package com.example.xin.fileprotector.crypto;

import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class Decryptor {
    private static final String ALGORITHM = "AES/CBC/PKCS7Padding";
    private static final int BUFFER_SIZE = 1024;
    private final KeyStore keyStore;

    public Decryptor(final KeyStore keyStore)  {
        this.keyStore = keyStore;
    }

    public boolean decryptFile(File inputFile, File outputFile, final String alias, final byte[] iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException,
            InvalidAlgorithmParameterException, InvalidKeyException, UnrecoverableEntryException
            , KeyStoreException {

        FileInputStream fis = null;
        FileOutputStream fos = null;

        final Cipher cipher = Cipher.getInstance(ALGORITHM);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(alias), ivSpec);
        boolean success = true;

        try {
            if (inputFile.exists() && inputFile.isFile()) {
                if (!outputFile.getParentFile().exists()) {
                    outputFile.getParentFile().mkdirs();
                }
                outputFile.createNewFile();
                fis = new FileInputStream(inputFile);
                fos = new FileOutputStream(outputFile);
                CipherInputStream cis = new CipherInputStream(fis, cipher);
                byte[] buffer = new byte[BUFFER_SIZE];
                int n = 0;
                while ((n = cis.read(buffer)) != -1) {
                    fos.write(buffer, 0, n);
                    fos.flush();
                }
            } else
                success = false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            success = false;
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    public String decryptText(final String textToDecrypt, final String alias, final byte[] iv)  throws
            NoSuchAlgorithmException, NoSuchPaddingException, UnrecoverableEntryException,
            KeyStoreException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException,
            InvalidAlgorithmParameterException {

        final Cipher cipher = Cipher.getInstance(ALGORITHM);
        final IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(alias), ivSpec);

        final byte[] encryptedData = Base64.decode(textToDecrypt, Base64.DEFAULT);
        return new String(cipher.doFinal(encryptedData));
    }

    private SecretKey getSecretKey(final String alias) throws NoSuchAlgorithmException,
            UnrecoverableEntryException, KeyStoreException {
        return ((KeyStore.SecretKeyEntry) keyStore.getEntry(alias, null)).getSecretKey();
    }
}
