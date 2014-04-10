package client;

import Utils.messageUtils;
import com.sun.deploy.util.ArrayUtil;

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
        byte[] temp;

        byte[] cipherText = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            if (data.length < 101){
                cipherText = cipher.doFinal(data);
            }
            else {
                temp = new byte[100];
                cipherText = new byte[0];
                for (int i = 0; i < data.length; i = i + 100){
                    if ((data.length - i) < 100 ){
                        System.arraycopy(data, i, temp, 0, (data.length - i));
                    }
                    else {
                        System.arraycopy(data, i, temp, 0, 100);
                    }

                    if (cipherText.length == 0){
                        cipherText = cipher.doFinal(temp);
                    }
                    else {
                        cipherText = messageUtils.concatenate(cipherText, cipher.doFinal(temp));
                    }
                }
            }

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
        String hallo = "hallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallo";
        KeyPair keys = generateKey();
        String result = "";
        result = new String(Encryption.encrypt(hallo.getBytes(), keys.getPublic()));
        System.out.println(result);
        /*
        result = new String(Encryption.decrypt(result.getBytes(), keys.getPrivate()));
        System.out.println(result);
        */
    }

}
