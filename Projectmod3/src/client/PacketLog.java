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
				logReceived.get(deviceNr).put(seqNr, packet);
			}
			else {
				logReceived.get(deviceNr).remove(getLowestSeq(deviceNr));
				logReceived.get(deviceNr).put(seqNr, packet);
			}
		} else {
			HashMap<Integer, DatagramPacket> map = new HashMap<Integer, DatagramPacket>();
			map.put(seqNr, packet);
			logReceived.put(deviceNr, map);
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
		int lowestSeq = 40000;
		if (logReceived.containsKey(deviceNr)) {
			for (Integer i : logReceived.get(deviceNr).keySet()) {
				if (i < lowestSeq) {
					lowestSeq = i;
				}
			}
		}
		return lowestSeq;
	}

	public int getHighestSeq(int deviceNr) {
		int highestSeq = -1;
		if (logReceived.containsKey(deviceNr)) {
			for (Integer i : logReceived.get(deviceNr).keySet()) {
				if (i > highestSeq) {
					highestSeq = i;
				}
			}
		}
		return highestSeq;
	}

	private Integer lowestSqc(HashMap<Integer, DatagramPacket> log) {
		Set<Integer> keySet = log.keySet();
		return (Integer) Collections.min(keySet);
	}

}
