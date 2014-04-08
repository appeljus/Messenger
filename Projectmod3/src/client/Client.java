package client;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

import GUI.ChatWindow;

public class Client extends Thread {
	ChatWindow chatwindow;
	////dsdsdsdsdsd
	static int port = 4242;
	String id1 = "192.168.5.1";
	String id2 = "192.168.5.2";
	String id3 = "192.168.5.3";
	String id4 = "192.168.5.4";
	MulticastSocket s;
	InetAddress group;

	public Client(ChatWindow c) {
		chatwindow = c;
		try {
			group = InetAddress.getByName("228.5.6.7");
			s = new MulticastSocket(port);
			s.joinGroup(group);
			Thread t = new Thread(this);
			t.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run(){
		while(true){
			receivePacket();
		}
	}

	public String getIP() {
		String result = null;
		try {
			result = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public void receivePacket() { 
		try {
			byte[] buf = new byte[1024];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);

			s.receive(packet);
			byte[] receiveData = packet.getData();
			String txt = new String(receiveData,"UTF-8");
			chatwindow.incoming(txt);
			String destination = packet.getAddress().toString();
			if (!destination.equals(this.getIP())) {
				s.send(packet);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendPacket(String message) {
		byte[] data = message.getBytes();
		DatagramPacket packetToSend = new DatagramPacket(data, data.length, group, port);
		try {
			s.send(packetToSend);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
