package client;

import sun.net.*;
import java.net.*;

public class Receiver {
	int port = 4242;
	String id1 = "192.168.5.1";
	String id2 = "192.168.5.2";
	String id3 = "192.168.5.3";
	String id4 = "192.168.5.4";
	
	public static void main(String[] args) {
		s.
	}
	
	public void run(int port) {
		try { 
			MulticastSocket s = new MulticastSocket(port);
			Packet receivePacket = new Packet(receiveData, receiveData.length);
			
			while(true) { 
				s.receive(receivePacket);
				
			}
		}
	}
	
}
