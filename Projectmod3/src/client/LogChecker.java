package client;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class LogChecker extends Thread {
	private Client client;
	private PacketLog log;
	private int lastPrintedPacket;
	
	public LogChecker(Client client, PacketLog log){
		this.client = client;
		this.log = log;
	}

	public void run(){
		while(true){
			int lowSeq = log.getLowestSeq();
			int highSeq = log.getHighestSeq();
			if(lowSeq != 0) System.out.println("I GOT A PACKET! " + lowSeq);
			List<Integer> holes = new ArrayList<Integer>();
			for(int i=Math.max(lastPrintedPacket, lowSeq); i<highSeq; i++) {
				if(!log.containsReceiveSeq(i)) {
					holes.add(i);
				}
				else if(holes.size() == 0){
					DatagramPacket packet = log.getPacket(client.getDeviceNr(), i);
					byte[] message = PacketUtils.getMessage(packet);
					int sequence = PacketUtils.getSequenceNr(packet);
					int hop = PacketUtils.getHopCount(packet);
					InetAddress sourceAddress = PacketUtils.getSourceAddress(packet);
					InetAddress destinationAddress = PacketUtils.getDistinationAddress(packet);
					client.receivePacket(message, sequence, hop, sourceAddress, destinationAddress);
					lastPrintedPacket = i;
				}
			}
			if(holes.size() != 0) {
				for(int i=0; i<holes.size(); i++) {
					byte[] msg = ("[NACK] " + holes.get(i) + " DUMMY_WORD").getBytes();
					DatagramPacket p = new DatagramPacket(msg,msg.length,client.getMyAddress(), client.getPort());
					client.resendPacket(p);
				}
			}
			try {
				this.sleep(1000);
			} catch (InterruptedException e) { e.printStackTrace(); }
		}
	}
}
