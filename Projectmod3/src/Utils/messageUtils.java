package Utils;

/**
 * Created by Martijn on 10-4-14.
 */
public class messageUtils {

    public static byte[] concatenate(byte[] array1, byte[] array2){
        int len1 = array1.length;
        int len2 = array2.length;
        byte[] result = new byte[len1 + len2];
        System.arraycopy(array1, 0, result, 0, len1);
        System.arraycopy(array2, 0, result, len1, len2);
        return result;
    }
}
