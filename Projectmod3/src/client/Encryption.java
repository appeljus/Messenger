package client;


import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Martijn
 */
public class Encryption {

        private Cipher cipher;
        private SecretKeySpec key;
        private IvParameterSpec ivspec;

        public Encryption(){
            try {
                this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            } catch (   NoSuchAlgorithmException |
                        NoSuchPaddingException e)
            {
                e.printStackTrace();
            }

            byte[] iv = new byte[0];
            try {
                iv = MessageDigest.getInstance("MD5").digest("SecurityIV".getBytes());
            }
            catch (NoSuchAlgorithmException e)
            {
                e.printStackTrace();
            }

            this.ivspec = new IvParameterSpec(iv);

        }

        public void setPassword(String pw){
            try {
                byte[] digest = MessageDigest.getInstance("MD5").digest(pw.getBytes());

                this.key = new SecretKeySpec(digest, "AES");

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        public byte[] encryptData(byte[] msg){
            try {

                try {
                    this.cipher.init(Cipher.ENCRYPT_MODE, this.key, this.ivspec);
                } catch (InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                }

                byte[] encryptedData = this.cipher.doFinal(msg);
                return encryptedData;
            } catch (   InvalidKeyException |
                        IllegalBlockSizeException |
                        BadPaddingException e) {
                e.printStackTrace();
            }

            return null;
        }

        public byte[] decryptData(byte[] encryptedData){
            try {

                try {
                    this.cipher.init(Cipher.DECRYPT_MODE, this.key, this.ivspec);
                } catch (InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                }
                byte[] decryptedData = this.cipher.doFinal(encryptedData);
                return decryptedData;
            } catch (   InvalidKeyException |
                        BadPaddingException |
                        IllegalBlockSizeException e)
            {
                e.printStackTrace();
            }
            return null;
        }

    public static void main(String[] args){
        byte[] crypt = "hallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallohallo".getBytes();
        Encryption kevin = new Encryption();
        Encryption kvein2 = new Encryption();
        kvein2.setPassword("string");
        kevin.setPassword("string");
        String result = "";
        crypt = kevin.encryptData(crypt);
        result = new String(crypt);
        System.out.println(result);
        crypt = kvein2.decryptData(crypt);
        result = new String(crypt);
        System.out.println(result);
    }
}
