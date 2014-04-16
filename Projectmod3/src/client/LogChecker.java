package client;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class LogChecker extends Thread {
	private Client client;
	private PacketLog log;
	private int[] cannotPrint = {40000,40000,40000,40000,40000};
	private int[] lastPrinted = {-1,-1,-1,-1,-1};

	public LogChecker(Client client, PacketLog log) {
		this.client = client;
		this.log = log;
	}

	public void run() {
		while (true) {
			for (int dn = 1; dn < 5; dn++) {
				int lowSeq = log.getLowestSeq(dn);
				int highSeq = log.getHighestSeq(dn);
				if (lowSeq != 40000 && highSeq != -1 && dn != client.getDeviceNr()) {
					if(highSeq == 0 && lastPrinted[dn] == 254) {
						lastPrinted[dn] = -1;
						cannotPrint[dn] = 40000;
					}
					List<Integer> holes = new ArrayList<Integer>();
					for (int i = Math.max(lowSeq,lastPrinted[dn]+1); i < highSeq; i++) {
						if (!log.containsReceiveSeq(dn,i)) {
							holes.add(i);
							if(i < cannotPrint[dn]) cannotPrint[dn] = i;
						} else if (i < cannotPrint[dn]) {
							printPacket(dn, i);
						}
						else if(i == cannotPrint[dn]) {
							printPacket(dn, i);
							cannotPrint[dn] = 40000;
						}
					}
					if (holes.size() != 0) {
						for (int i = 0; i < holes.size(); i++) {
							byte[] msg = ("[NACK] " + holes.get(i) + " DUMMY_WORD").getBytes();
							DatagramPacket p = null;
							try {
								byte[] data = PacketUtils.getData(client.getEncryption().encrypt(msg), 0, client.getHopCount(), client.getMyAddress(), InetAddress.getByName("192.168.5." + dn));
								p = new DatagramPacket(data, data.length, InetAddress.getByName("192.168.5." + dn), client.getPort());
							} catch (UnknownHostException e) { }
							client.resendPacket(p);
						}
					}
				}
			}
			try {
				sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void printPacket(int dn, int i) {
		DatagramPacket packet = log.getPacket(dn, i);
		byte[] message = PacketUtils.getMessage(packet);
		int sequence = PacketUtils.getSequenceNr(packet);
		int hop = PacketUtils.getHopCount(packet);
		InetAddress sourceAddress = PacketUtils.getSourceAddress(packet);
		InetAddress destinationAddress = PacketUtils.getDistinationAddress(packet);
        int length = PacketUtils.getLength(packet);
		client.processPacket(message, sequence, hop, sourceAddress, destinationAddress, length);
		lastPrinted[dn] = i;
		//log.removePacket(dn,i);
	}
	
	public void removeDevice(int deviceNr) {
		cannotPrint[deviceNr] = 40000;
	}
	
	public void seqGone(int deviceNr, int seqNr) {
		if(cannotPrint[deviceNr] == seqNr) {
			cannotPrint[deviceNr] = 40000;
			lastPrinted[deviceNr] = seqNr;
		}
	}
}
