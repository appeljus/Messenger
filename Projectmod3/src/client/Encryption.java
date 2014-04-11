package client;


import javax.crypto.*;

import Utils.messageUtils;

import java.security.*;

/**
 * Created by Martijn on 9-4-14.
 */
public class Encryption {

    public static byte[] getEncryption(byte[] data){
        StringBuilder strng = new StringBuilder();
        for (byte d : data){
            strng.append((char)((int)d + 4));
        }
        return strng.toString().getBytes();
    }

    public static byte[] getDecryption(byte[] data){
        StringBuilder strng = new StringBuilder();
        for (byte d : data){
            strng.append((char)((int)d - 4));
        }
        return strng.toString().getBytes();
    }

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
                        System.out.println(cipherText.length);
                    }
                    else {
                        cipherText = messageUtils.concatenate(cipherText, cipher.doFinal(temp));
                        System.out.println(cipherText.length);
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
        byte[] temp;
        byte[] decryptedText = null;
        try{
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, key);
            if (data.length < 128){
                decryptedText = cipher.doFinal(data);
            }
            else {
                temp = new byte[127];
                decryptedText = new byte[0];
                for (int i = 0; i < data.length; i = i + 127){
                    if ((data.length - i) < 127 ){
                        System.arraycopy(data, i, temp, 0, (data.length - i));
                    }
                    else {
                        System.arraycopy(data, i, temp, 0, 127);
                    }

                    if (decryptedText.length == 0){
                        decryptedText = cipher.doFinal(temp);
                    }
                    else {
                        decryptedText = messageUtils.concatenate(decryptedText, cipher.doFinal(temp));
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
        return decryptedText;
    }



    public static void main(String[] agrs){
        String hallo = "hallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallo";


        KeyPair keys = generateKey();
        String result = "";
        result = new String(getEncryption(hallo.getBytes()));

        System.out.println(result);
        result = new String(getDecryption(result.getBytes()));
        System.out.println(result);
    }

}
