package client;


import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Martijn on 9-4-14.
 */
public class Encryption2 {

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

    public static Key generateKey(){
        Key key = null;
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            key = keyGen.generateKey();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return key;
    }

    public static byte[] encrypt(byte[] data, Key key){
        byte[] encryptedData = null;

        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE,key);
            encryptedData = cipher.doFinal(data);

        }
        catch (     NoSuchPaddingException |
                    InvalidKeyException |
                    IllegalBlockSizeException |
                    BadPaddingException |
                    NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }


        return encryptedData;

    }

    public static byte[] decrypt(byte[] data,Key key){
        byte[] decryptedData = null;

        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            decryptedData = cipher.doFinal(data);

        }
        catch (     NoSuchPaddingException |
                InvalidKeyException |
                IllegalBlockSizeException |
                BadPaddingException |
                NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }

        return decryptedData;
    }



    public static void main(String[] agrs){
        Key key = Encryption2.generateKey();
        byte[] message = "stringstringstringstringstringstringstringstringstringstringstringstringstringstringstringstringstringstringstringstringstringstringstringstringstringstringstringstringstringstringstringstringstringstringstringstringstringstringstringstring".getBytes();
        String result = "";
        result = new String(Encryption2.encrypt(message, key));
        System.out.println(result + "\n");
        result = new String(Encryption2.decrypt(result.getBytes(), key));
        System.out.println(result);
    }

}
