package com.workday.test;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

/**
 * Created by raovinay on 27-07-2017.
 */
public class EncryptUtils {

    public static final String ENCRYPTION_PASSWORD = "MyTestPassword";

    public static void main(String[] args) {
        if(args.length!=1){
            System.out.println("Please provide exactly 1 argument as the input string to be encrypted.");
        }
        StandardPBEStringEncryptor textEncryptor = new StandardPBEStringEncryptor();
        textEncryptor.setPassword(ENCRYPTION_PASSWORD);
        System.out.println(textEncryptor.encrypt(args[0]));
    }
    public static String decrypt(String encryptedString){
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(ENCRYPTION_PASSWORD);
        return encryptor.decrypt(encryptedString);
    }
}
