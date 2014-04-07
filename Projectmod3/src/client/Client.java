package client;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

import protocol.*;

public class Client {
	static int port = 4242;
	String id1 = "192.168.5.1";
	String id2 = "192.168.5.2";
	String id3 = "192.168.5.3";
	String id4 = "192.168.5.4";
	MulticastSocket s;
	ArrayList<String> array = new ArrayList<String>();
	
	
	public static void main(String[] args) {
	}
	
	public Client() {
		try {
			s = new MulticastSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void receive() { 
		try {
			byte[] buf = new byte[1024];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			
			while(true) {
				s.receive(packet);
				byte[] receiveData = packet.getData();
				InetAddress destination = packet.getAddress();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void send(Packet packet) { 
		InetAddress address = InetAddress.getByName(host);
	}
}
