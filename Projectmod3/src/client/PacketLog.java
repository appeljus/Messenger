package client;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by Martijn on 11-4-14.
 */
public class PacketLog {

	private HashMap<Integer, DatagramPacket> logSend;
	private HashMap<Integer, HashMap<Integer, DatagramPacket>> logReceived;
	private static final int sizeLog = 128;
	private int[] lastSeq = {-1, -1, -1, -1, -1};

	public PacketLog() {
		logSend = new HashMap<Integer, DatagramPacket>();
		logReceived = new HashMap<Integer, HashMap<Integer, DatagramPacket>>();
	}

	public boolean hasDevice(int deviceNr) {
		return logReceived.containsKey(deviceNr);
	}
	
	public void removePacket(int deviceNr, int seqNr) {
		if(logReceived.containsKey(deviceNr) && logReceived.get(deviceNr).containsKey(seqNr)) {
			logReceived.get(deviceNr).remove(seqNr);
		}
	}

	public DatagramPacket getPacket(int deviceNr, int seqNr) {
		if (logReceived.containsKey(deviceNr)) {
			return logReceived.get(deviceNr).get(seqNr);
		} else
			return null;
	}

	public void addReceivePacket(int deviceNr, int seqNr, DatagramPacket packet) {
		if (logReceived.containsKey(deviceNr)) { 
			if(logReceived.get(deviceNr).size() < sizeLog) {
				if(seqNr == 0 && lastSeq[deviceNr] == 255) {
					logReceived.get(deviceNr).clear();
				}
				logReceived.get(deviceNr).put(seqNr, packet);
				lastSeq[deviceNr] = seqNr;
			}
			else {
				logReceived.get(deviceNr).remove(getLowestSeq(deviceNr));
				if(seqNr == 0 && lastSeq[deviceNr] == 255) {
					logReceived.get(deviceNr).clear();
				}
				logReceived.get(deviceNr).put(seqNr, packet);
				lastSeq[deviceNr] = seqNr;
			}
		} else {
			HashMap<Integer, DatagramPacket> map = new HashMap<Integer, DatagramPacket>();
			map.put(seqNr, packet);
			if(seqNr == 0 && lastSeq[deviceNr] == 255) {
				logReceived.get(deviceNr).clear();
			}
			lastSeq[deviceNr] = seqNr;
			logReceived.put(deviceNr, map);
		}
	}
	
	public void removeDevice(int deviceNr) {
		if(logReceived.containsKey(deviceNr)) {
			logReceived.remove(deviceNr);
		}
	}

	public DatagramPacket getPacketSend(int sqc) {
		return logSend.get(sqc);
	}

	public void addSendPacket(DatagramPacket packet) {
		if (logSend.size() < sizeLog) {
			logSend.put((int) packet.getData()[0], packet);
		} else {
			logSend.remove(lowestSqc(logSend));
			logSend.put((int) packet.getData()[0], packet);
		}
	}

	public boolean containsSendSeq(int sqc) {
		return logSend.containsKey(sqc);
	}

	public boolean containsReceiveSeq(int deviceNr, int sqc) {
		return logReceived.get(deviceNr).containsKey(sqc);
	}

	public int getSizeLog() {
		return sizeLog;
	}

	public int getLowestSeq(int deviceNr) {
		if (logReceived.containsKey(deviceNr)) {
			Set<Integer> keySet = logReceived.get(deviceNr).keySet();
			return (int) Collections.min(keySet);
		}
		return -1;
	}

	public int getHighestSeq(int deviceNr) {
		if (logReceived.containsKey(deviceNr)) {
			Set<Integer> keySet = logReceived.get(deviceNr).keySet();
			return (int) Collections.max(keySet);
		}
		return -1;
	}

	private Integer lowestSqc(HashMap<Integer, DatagramPacket> log) {
		Set<Integer> keySet = log.keySet();
		return (Integer) Collections.min(keySet);
	}

}
