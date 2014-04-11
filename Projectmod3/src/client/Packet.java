package client;

import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * Created by Martijn on 11-4-14.
 */
public class Packet {

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
}
