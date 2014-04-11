package client;

import java.net.DatagramPacket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Martijn on 11-4-14.
 */
public class PacketLog {

    private HashMap<Integer, DatagramPacket> logSend;
    private HashMap<Integer, DatagramPacket> logReceived;
    private static final int sizeLog = 128;

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
        if (logReceived.size() < sizeLog){
            logReceived.put((int)packet.getData()[0], packet);
        }
        else {
            logReceived.remove(lowestSqc(logReceived));
            logReceived.put((int)packet.getData()[0], packet);
        }
    }

    public void addSendPacket(DatagramPacket packet){
        if (logSend.size() < sizeLog){
            logSend.put((int)packet.getData()[0], packet);
        }
        else {
            logSend.remove(lowestSqc(logSend));
            logSend.put((int)packet.getData()[0], packet);
        }
    }

    public boolean containsReceivedSeq(int sqc){
        return logReceived.containsKey(sqc);
    }

    public boolean containsSendSeq(int sqc){
        return logSend.containsKey(sqc);
    }

    public int getSizeLog(){
        return sizeLog;
    }

    private Integer lowestSqc(HashMap<Integer,DatagramPacket> log) {
        Set keySet = log.keySet();
        return (Integer)Collections.min(keySet);
    }

}
