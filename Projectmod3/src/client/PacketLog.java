package client;

import java.net.DatagramPacket;
import java.util.HashMap;

/**
 * Created by Martijn on 11-4-14.
 */
public class PacketLog {

    private HashMap<Integer, DatagramPacket> log;

    public PacketLog(){
        log = new HashMap<Integer, DatagramPacket>();

    }

    public void addPacket(DatagramPacket packet){
        int sequenceNr = (int)packet.getData()[];
        log.put(sequenceNr, packet);
    }

    public DatagramPacket getPacket(int sequencenr){
        return log.get(sequencenr);
    }

    public boolean containsSeq(int sequencenr){
        return log.containsKey(sequencenr);
    }
}
