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
    private HashMap<Integer, Integer> sequenceReceived;
    private static final int sizeLog = 128;

    public PacketLog(){
        logSend = new HashMap<Integer, DatagramPacket>();
        sequenceReceived = new HashMap<Integer, Integer>();
    }

    public int getLatestSeq(int deviceNr, int seq){
    	if(!sequenceReceived.containsKey(deviceNr)){
    		addSequenceNr(deviceNr,seq);
    	}
        return sequenceReceived.get(deviceNr);
    }

    public void addSequenceNr(int devicenr, int sequencenr){
        sequenceReceived.put(devicenr, sequencenr);
    }

    public DatagramPacket getPacketSend(int sqc) {
        return logSend.get(sqc);
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
