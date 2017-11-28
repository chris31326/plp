package com.example.xin.fileprotector.crypto;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class CryptoFactory {
    private final static KeyStore keyStore;

    public final static Encryptor encryptor;
    public final static Decryptor decryptor;

    static {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
        } catch (KeyStoreException | NoSuchAlgorithmException | IOException | CertificateException e) {
            throw new IllegalStateException(e);
        }

        encryptor = new Encryptor(keyStore);
        decryptor = new Decryptor(keyStore);
    }
}
