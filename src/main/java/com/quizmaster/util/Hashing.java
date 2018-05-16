package com.quizmaster.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Hashing {
    public String performHash(String password,String salt){
        String hashedPassword = null;
        try {
            String saltedPassword = password+salt;
            System.out.println("salted Password: "+saltedPassword); //for checking
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(saltedPassword.getBytes());

            byte[] byteData = messageDigest.digest();

            //Convert  the byte to hex format
            StringBuffer buffer = new StringBuffer();
            for(int i=0;i<byteData.length;i++){
                buffer.append(Integer.toString((byteData[i] & 0xff) + 0x100,16).substring(1));
            }
            hashedPassword=buffer.toString();
            System.out.println("hashed password in HEX 64 bit: "+hashedPassword);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashedPassword;
    }

    public String generateRandomSalt(){
        SecureRandom random = new SecureRandom();
        String salt = null;
        byte[] byteData = new byte[32];
        random.nextBytes(byteData);
        StringBuffer buffer = new StringBuffer();
        for(int i=0;i<byteData.length;i++){
            buffer.append(Integer.toString((byteData[i] & 0xff) + 0x100,16).substring(1));
        }
        salt=buffer.toString();
        return salt;
    }
}
