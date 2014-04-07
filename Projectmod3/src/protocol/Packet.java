package protocol;

import org.apache.commons.lang3.ArrayUtils;
import java.net.DatagramPacket;

public class Packet {

    public static byte[] getByteArray(byte[] sourcePort, byte[] destinationPort, byte[] checksum, byte[] sequencenumber, byte[] hopCount, byte[] data){

        byte[] result;
        result = ArrayUtils.addAll(sourcePort, destinationPort);
        result = ArrayUtils.addAll(result, checksum);
        result = ArrayUtils.addAll(result, sequencenumber);
        result = ArrayUtils.addAll(result, hopCount);
        result = ArrayUtils.addAll(result, data);

        return result;
    }

    public static DatagramPacket makePacket(byte[] sourcePort, byte[] destinationPort, byte[] checksum, byte[] sequencenumber, byte[] hopCount, byte[] data){
        byte[] byteArray = getByteArray(sourcePort, destinationPort, checksum, sequencenumber, hopCount, data);
        int lengte = byteArray.length;
        DatagramPacket packet = new DatagramPacket(byteArray, lengte);
        return packet;
    }
}
