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
    private HashMap<Integer, DatagramPacket[]> logReceived;
    private HashMap<Integer, Integer> sequenceReceived;
    private static final int sizeLog = 128;

    public PacketLog(){
        logSend = new HashMap<Integer, DatagramPacket>();
        sequenceReceived = new HashMap<Integer, Integer>();
        logReceived = new HashMap<Integer, DatagramPacket[]>();
    }

    public boolean hasDevice(int deviceNr) {
    	return sequenceReceived.containsKey(deviceNr);
    }
    
    public DatagramPacket getPacket(int deviceNr, int seqNr) {
    	if(logReceived.containsKey(deviceNr)) {
    		return logReceived.get(deviceNr)[seqNr];
    	}
    	else return null;
    }
    
    public int getLatestSeq(int deviceNr){
        return sequenceReceived.get(deviceNr);
    }

    public void addSequenceNr(int devicenr, int sequencenr){
        sequenceReceived.put(devicenr, sequencenr);
    }
    
    public void addReceivePacket(int deviceNr, int seqNr, DatagramPacket packet) {
    	if(logReceived.containsKey(deviceNr)) {
    		logReceived.get(deviceNr)[seqNr] = packet;
    	}
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
    
    public boolean containsReceiveSeq(int sqc) {
    	return sequenceReceived.containsKey(sqc);
    }

    public int getSizeLog(){
        return sizeLog;
    }
    
    public int getLowestSeq() {
    	Set<Integer> keySet = sequenceReceived.keySet();
    	if(keySet.size() != 0)
    		return (Integer)Collections.min(keySet);
    	else
    		return 0;
    }
    
    public int getHighestSeq() {
    	Set<Integer> keySet = sequenceReceived.keySet();
    	if(keySet.size() != 0)
    		return (Integer)Collections.max(keySet);
    	else
    		return 0;
    }

    private Integer lowestSqc(HashMap<Integer,DatagramPacket> log) {
        Set<Integer> keySet = log.keySet();
        return (Integer)Collections.min(keySet);
    }

}
