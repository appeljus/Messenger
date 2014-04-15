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
	private HashMap<Integer, List<Integer>> sequenceReceived;
	private static final int sizeLog = 128;

	public PacketLog() {
		logSend = new HashMap<Integer, DatagramPacket>();
		sequenceReceived = new HashMap<Integer, List<Integer>>();
		logReceived = new HashMap<Integer, HashMap<Integer, DatagramPacket>>();
	}

	public boolean hasDevice(int deviceNr) {
		return sequenceReceived.containsKey(deviceNr);
	}

	public DatagramPacket getPacket(int deviceNr, int seqNr) {
		if (logReceived.containsKey(deviceNr)) {
			return logReceived.get(deviceNr).get(seqNr);
		} else
			return null;
	}

	public int getLatestSeq(int deviceNr) {
		return sequenceReceived.get(deviceNr).get(
				sequenceReceived.get(deviceNr).size() - 1);
	}

	public void addSequenceNr(int devicenr, int sequencenr) {
		if (sequenceReceived.containsKey(devicenr))
			sequenceReceived.get(devicenr).add(sequencenr);
		else {
			List<Integer> list = new ArrayList<Integer>();
			list.add(sequencenr);
			sequenceReceived.put(devicenr, list);
		}
	}

	public void addReceivePacket(int deviceNr, int seqNr, DatagramPacket packet) {
		if (logReceived.containsKey(deviceNr)) { 
			logReceived.get(deviceNr).put(seqNr, packet);
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

	public boolean containsReceiveSeq(int sqc) {
		return sequenceReceived.containsKey(sqc);
	}

	public int getSizeLog() {
		return sizeLog;
	}

	public int getLowestSeq(int deviceNr) {
		int lowestSeq = 0;
		if (sequenceReceived.containsKey(deviceNr)) {
			for (int i = 0; i < sequenceReceived.get(deviceNr).size(); i++) {
				if (sequenceReceived.get(deviceNr).get(i) < lowestSeq) {
					lowestSeq = sequenceReceived.get(deviceNr).get(i);
				}
			}
		}
		return lowestSeq;
	}

	public int getHighestSeq(int deviceNr) {
		int highestSeq = 0;
		if (sequenceReceived.containsKey(deviceNr)) {
			for (int i = 0; i < sequenceReceived.get(deviceNr).size(); i++) {
				if (sequenceReceived.get(deviceNr).get(i) > highestSeq) {
					highestSeq = sequenceReceived.get(deviceNr).get(i);
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
