package com.lanchatapp.lanchatapp.Server;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


// Algorithm used to hash users password
public class HashingAlgo {

    public static String hashSHA256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
