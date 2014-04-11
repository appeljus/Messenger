package client;


import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.*;

/**
 * Created by Martijn on 9-4-14.
 */
public class Encryption {

    private static final String string = "stringstringstringstringstringstringstringstringstringst";
    private static final Key key;
    private static final DESKeySpec spec;


    public static final void getKey(){
        spec = new DESKeySpec(string.getBytes());
        key = spec;
    }

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





        return null;
    }

    public static byte[] encrypt(byte[] data, Key key){
        byte[] encryptedData = null;
        SecureRandom rnd = new SecureRandom();
        IvParameterSpec iv = new IvParameterSpec(rnd.generateSeed(16));

        try {
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            encryptedData = cipher.doFinal(data);

        }
        catch (     NoSuchPaddingException |
                    InvalidKeyException |
                    InvalidAlgorithmParameterException |
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
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
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
        String result = "";
        String hallo = "hallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallo";
        result = new String(Encryption.encrypt(hallo.getBytes(), key));
        System.out.println(new String(result));
        result = new String(Encryption.decrypt(result.getBytes(), key));
        System.out.println(result);

    }

}
