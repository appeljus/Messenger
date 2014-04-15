package client;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

/**
 * Created by Martijn on 11-4-14.
 */
public class PacketUtils {

    public static byte[] getData(byte [] message, int sequenceNumber, int hopCount, InetAddress source, InetAddress destination){
        byte[] data;
        byte[] header;

        byte seq = (byte) sequenceNumber;
        byte hop = (byte) hopCount;
        byte[] src = source.getAddress();
        byte[] dst = destination.getAddress();

        header = new byte[12];
        header[0] = seq;
        header[1] = hop;
        header[2] = src[0];
        header[3] = src[1];
        header[4] = src[2];
        header[5] = src[3];
        header[6] = dst[0];
        header[7] = dst[1];
        header[8] = dst[2];
        header[9] = dst[3];
        short length = (short)message.length;
        ByteBuffer buff = ByteBuffer.allocate(2);
        buff.putShort(length);
        byte[] lengte = buff.array();
        header[10] = lengte[0];
        header[11] = lengte[1];

        data = new byte[header.length + message.length];
        System.arraycopy(header, 0, data, 0, header.length);
        System.arraycopy(message, 0, data, header.length, message.length);
        return data;
    }

    public static int getSequenceNr(DatagramPacket packet){
        return ((int)packet.getData()[0]) & 0xFF;
    }

    public static int getHopCount(DatagramPacket packet){
        return ((int)packet.getData()[1]) & 0xFF;
    }

    public static InetAddress getSourceAddress(DatagramPacket packet){
        byte[] address = {packet.getData()[2], packet.getData()[3],packet.getData()[4], packet.getData()[5] };
        InetAddress result = null;
        try {
            result = InetAddress.getByAddress(address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static InetAddress getDistinationAddress(DatagramPacket packet){
        byte[] address = {packet.getData()[6], packet.getData()[7],packet.getData()[8], packet.getData()[9] };
        InetAddress result = null;
        try {
            result = InetAddress.getByAddress(address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static byte[] getMessage(DatagramPacket packet){
        byte[] data = packet.getData();
        byte[] result = new byte[data.length-12];
        System.arraycopy(data, 12, result, 0, data.length-12);
        return result;
    }

    public static int getLength(DatagramPacket packet){
        byte[] lengte = new byte[2];
        lengte[0] = packet.getData()[10];
        lengte[1] = packet.getData()[11];
        ByteBuffer buff = ByteBuffer.wrap(lengte);
        int nummer = (int)buff.getShort();
        return nummer;
    }

}
