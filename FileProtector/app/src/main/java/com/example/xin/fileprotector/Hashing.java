package com.example.xin.fileprotector;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hashing {

    public String getHexString(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(key.getBytes());
            byte[] byteData = md.digest();

            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < byteData.length; ++i) {
                String hex = Integer.toHexString(0xff & byteData[i]);
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
