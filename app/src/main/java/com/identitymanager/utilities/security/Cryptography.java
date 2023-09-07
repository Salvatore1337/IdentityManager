package com.identitymanager.utilities.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Cryptography {


    /**
     * This method is useful to hashing registration passwords on sign up and store them into the db
     * In this way the password are more safe into the db
     * We also use this method hash the entered password at login and compare the two hashed password to allow the user to login
     *
     * @param stringToHash is the string that will be hashed
     * @return the hashed string
     */
    public static String hashString (String stringToHash) {

        StringBuilder hashedString = new StringBuilder();; //this string will be the result of the hashing

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256"); //getting the hashing algorithm
            byte[]hashInBytes = messageDigest.digest(stringToHash.getBytes(StandardCharsets.UTF_8));

            //bytes to hex
            for (byte b : hashInBytes) {
                hashedString.append(String.format("%02x", b));
            }

        } catch (NoSuchAlgorithmException e) {
            hashedString = new StringBuilder();
            hashedString.append("");
        }

        return hashedString.toString();
    }
}
