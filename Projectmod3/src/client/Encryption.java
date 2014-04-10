package client;

import javax.crypto.*;
import java.security.*;

/**
 * Created by Martijn on 9-4-14.
 */
public class Encryption {

    public static KeyPair generateKey(){
        KeyPairGenerator keyGen = null;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keyGen.initialize(1024);
        KeyPair key = keyGen.generateKeyPair();

        return key;
    }

    public static byte[] encrypt(byte[] data, Key key){

        byte[] cipherText = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipherText = cipher.doFinal(data);
        }
        catch (     NoSuchPaddingException |
                    InvalidKeyException |
                    NoSuchAlgorithmException |
                    IllegalBlockSizeException |
                    BadPaddingException e)
        {
            e.printStackTrace();
        }
        return cipherText;

    }

    public static byte[] decrypt(byte[] data,Key key){
        byte[] decryptedText = null;
        try{
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, key);
            decryptedText = cipher.doFinal(data);
        }
        catch (     NoSuchPaddingException |
                    InvalidKeyException |
                    NoSuchAlgorithmException |
                    IllegalBlockSizeException |
                    BadPaddingException e)
        {
            e.printStackTrace();
        }
        return decryptedText;
    }



    public static void main(String[] agrs){
        String hallo = "hallo";
        KeyPair keys = generateKey();
        System.out.println(Encryption.encrypt(hallo.getBytes(), keys.getPublic()));
        String result = new String(Encryption.decrypt(encrypt(hallo.getBytes(), keys.getPublic()), keys.getPrivate()));
        System.out.println(result);

    }

}