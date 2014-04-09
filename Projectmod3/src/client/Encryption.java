package client;


import org.apache.commons.codec.*;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Random;

/**
 * Created by Martijn on 9-4-14.
 */
public class Encryption {

    public static byte[] encrypt(byte[] data, byte[] key){

        return null;

    }

    public static byte[] decrypt(byte[] data,byte[] key){
        return null;
    }



    public static void main(String[] agrs){
        String hallo = "hallo";
        byte[] key = {(byte)1, (byte)2, (byte)3, (byte)4, (byte)5, (byte)6, (byte)7, (byte)8};
        System.out.println(Encryption.encrypt(hallo.getBytes(), key));
    }

}
