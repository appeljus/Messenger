package client;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

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

        header = new byte[message.length + src.length + dst.length + 2];
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

        data = new byte[header.length + message.length];
        System.arraycopy(header, 0, data, 0, header.length);
        System.arraycopy(message, 0, data, header.length, message.length);
        return data;
    }

    public static int getSequenceNr(DatagramPacket packet){
        return (int)packet.getData()[0];
    }

    public static int getHopCount(DatagramPacket packet){
        return (int)packet.getData()[1];
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
        byte[] result = new byte[data.length - 10];
        System.arraycopy(data, 10, result, 0, data.length-10);
        return result;
    }
}
