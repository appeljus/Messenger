package client;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class LogChecker extends Thread {
	private Client client;
	private PacketLog log;
	private int[] cannotPrint = {40000,40000,40000,40000};

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
					List<Integer> holes = new ArrayList<Integer>();
					for (int i = lowSeq; i < highSeq; i++) {
						if (!log.containsReceiveSeq(dn,i)) {
							holes.add(i);
							if(i < cannotPrint[dn]) cannotPrint[dn] = i;
						} else if (i < cannotPrint[dn]) {
							DatagramPacket packet = log.getPacket(dn, i);
							byte[] message = PacketUtils.getMessage(packet);
							int sequence = PacketUtils.getSequenceNr(packet);
							int hop = PacketUtils.getHopCount(packet);
							InetAddress sourceAddress = PacketUtils.getSourceAddress(packet);
							InetAddress destinationAddress = PacketUtils.getDistinationAddress(packet);
                            int length = PacketUtils.getLength(packet);
							client.processPacket(message, sequence, hop, sourceAddress, destinationAddress, length);
							log.removePacket(dn,i);
						}
						else if(i == cannotPrint[dn]) {
							DatagramPacket packet = log.getPacket(dn, i);
							byte[] message = PacketUtils.getMessage(packet);
							int sequence = PacketUtils.getSequenceNr(packet);
							int hop = PacketUtils.getHopCount(packet);
							InetAddress sourceAddress = PacketUtils.getSourceAddress(packet);
							InetAddress destinationAddress = PacketUtils.getDistinationAddress(packet);
							int length = PacketUtils.getLength(packet);
							client.processPacket(message, sequence, hop, sourceAddress, destinationAddress, length);
							log.removePacket(dn,i);
							cannotPrint[dn] = 40000;
						}
					}
					if (holes.size() != 0) {
						for (int i = 0; i < holes.size(); i++) {
							System.out.println("NACK " + holes.get(i));
							byte[] msg = ("[NACK] " + holes.get(i) + " DUMMY_WORD").getBytes();
							DatagramPacket p = null;
							try {
								PacketUtils.getData(client.getEncryption().encryptData(msg), 0, client.getHopCount(), client.getMyAddress(), InetAddress.getByName("192.168.5." + dn));
								p = new DatagramPacket(client.getEncryption().encryptData(msg), msg.length, InetAddress.getByName("192.168.5." + dn), client.getPort());
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
	
	public void removeDevice(int deviceNr) {
		cannotPrint[deviceNr] = 40000;
	}
}
