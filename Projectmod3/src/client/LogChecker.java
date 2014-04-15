package client;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class LogChecker extends Thread {
	private Client client;
	private PacketLog log;

	public LogChecker(Client client, PacketLog log) {
		this.client = client;
		this.log = log;
	}

	public void run() {
		int lastPrintedPacket = 0;
		while (true) {
			for (int dn = 1; dn < 5; dn++) {
				int lowSeq = log.getLowestSeq(dn);
				int highSeq = log.getHighestSeq(dn);
				if (lowSeq != 40000 && highSeq != -1 && dn != client.getDeviceNr()) {
					List<Integer> holes = new ArrayList<Integer>();
					for (int i = lowSeq; i < highSeq; i++) {
						if (!log.containsReceiveSeq(dn,i)) {
							holes.add(i);
						} else if (holes.size() == 0) {
							DatagramPacket packet = log.getPacket(dn, i);
							byte[] message = PacketUtils.getMessage(packet);
							int sequence = PacketUtils.getSequenceNr(packet);
							int hop = PacketUtils.getHopCount(packet);
							InetAddress sourceAddress = PacketUtils.getSourceAddress(packet);
							InetAddress destinationAddress = PacketUtils.getDistinationAddress(packet);
                            int length = PacketUtils.getLength(packet);
							client.processPacket(message, sequence, hop, sourceAddress, destinationAddress, length);
							lastPrintedPacket = i;
							log.removePacket(dn,i);
						}
					}
					if (holes.size() != 0) {
						for (int i = 0; i < holes.size(); i++) {
							System.out.println("NACK " + holes.get(i));
							byte[] msg = ("[NACK] " + holes.get(i) + " DUMMY_WORD").getBytes();
							System.out.println("NAKC LENGHRTSASA: " + msg.length);
							DatagramPacket p = new DatagramPacket(msg,
									msg.length, client.getMyAddress(),
									client.getPort());
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
}
