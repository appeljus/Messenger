package protocol;

import org.apache.commons.lang3.ArrayUtils;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class Packet {

    public static byte[] getByteArray(byte[] sourcePort, byte[] checksum, byte[] sequencenumber, byte[] hopCount, byte[] data){

        byte[] result;
        result = ArrayUtils.addAll(sourcePort, checksum);
        result = ArrayUtils.addAll(result, sequencenumber);
        result = ArrayUtils.addAll(result, hopCount);
        result = ArrayUtils.addAll(result, data);

        return result;
    }

    public static DatagramPacket makePacket(InetAddress adres, int port, byte[] sourcePort, byte[] checksum, byte[] sequencenumber, byte[] hopCount, byte[] data){
        byte[] byteArray = getByteArray(sourcePort, checksum, sequencenumber, hopCount, data);
        int lengte = byteArray.length;
        DatagramPacket packet = new DatagramPacket(byteArray, lengte, adres, port);
        return packet;
    }
}
