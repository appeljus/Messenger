package client;

import java.net.DatagramPacket;
import java.util.HashMap;

/**
 * Created by Martijn on 11-4-14.
 */
public class PacketLog {

    private HashMap<Integer, DatagramPacket> logSend;
    private HashMap<Integer, DatagramPacket> logReceived;

    public PacketLog(){
        logSend = new HashMap<Integer, DatagramPacket>();
        logReceived = new HashMap<Integer, DatagramPacket>();

    }

    public DatagramPacket getPacketSend(int sqc) {
        return logSend.get(sqc);
    }

    public DatagramPacket getPacketReceived(int sqc) {
        return logReceived.get(sqc);
    }

    public void addReceivedPacket(DatagramPacket packet){
        logReceived.put((int)packet.getData()[0], packet);
    }

    public void addSendPacket(DatagramPacket packet){
        logSend.put((int)packet.getData()[0], packet);
    }

    public boolean containsReceivedSeq(int sqc){
        return logReceived.containsKey(sqc);
    }

    public boolean containsSendSeq(int sqc){
        return logSend.containsKey(sqc);
    }

}
