package com.example.xin.fileprotector.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hashing {

    public static String getHexString(final String key) {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(key.getBytes());
            byte[] byteData = md.digest();

            final StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < byteData.length; ++i) {
                final String hex = Integer.toHexString(0xff & byteData[i]);
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
