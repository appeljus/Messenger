package client;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import GUI.ChatWindow;
import GUI.LoginWindow;

public class Client extends Thread {
	ChatWindow chatwindow;
	static int port = 4242;
	MulticastSocket s;
	InetAddress group;
	String myName;
	InetAddress myAddress;
	List<String> pubKeys = new ArrayList<String>();

	public Client(ChatWindow c, String name) {
		myName = name;
		try {
			Enumeration e = NetworkInterface.getNetworkInterfaces();
			while (e.hasMoreElements()) {
				NetworkInterface n = (NetworkInterface) e.nextElement();
				Enumeration ee = n.getInetAddresses();
				while (ee.hasMoreElements()) {
					InetAddress i = (InetAddress) ee.nextElement();
					if(n.getDisplayName().contains("wlan")){
						myAddress = i;
					}
				}
			}
		} catch (SocketException e1) {
			e1.printStackTrace();
		}

		chatwindow = c;
		pubKeys.add("THISISMYPUBKEY");
		try {
			group = InetAddress.getByName("228.5.6.7");
			s = new MulticastSocket(port);
			s.joinGroup(group);
			this.sendPacket("[BROADCAST]: " + myName + " " + pubKeys.get(0));
			Thread t = new Thread(this);
			t.start();
			Timer timer = new Timer();
			timer.scheduleAtFixedRate(new SecondTimer(this), 1000, 1000);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}

	public String getClientName() {
		return myName;
	}

	public String getPubKey() {
		return pubKeys.get(0);
	}

	public void run() {
		while (true) {
			receivePacket();
		}
	}

	public String getIP() {
		String result = null;
		try {
			result = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
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
			String txt = new String(receiveData, "UTF-8");
			txt = txt.substring(1);
			if (txt.startsWith("[BROADCAST]:") && !packet.getAddress().equals(myAddress)) {
				String[] words = txt.split(" ");
				if (words[1].equals(myName)) {
					this.sendPacket("[NAME_IN_USE]: " + words[1] + " STUFF");
				}
				chatwindow.updateNames(words[1]);
				if (words.length == 4) {
					pubKeys.add(words[3]);
				}
			} else if (txt.startsWith("[NAME_IN_USE]: ") && !packet.getAddress().equals(myAddress)) {
				String[] words = txt.split(" ");
				if (myName.equals(words[1])) {
					chatwindow.dispose();
					new LoginWindow();
				}
			}
			else if(!packet.getAddress().equals(myAddress)){
				chatwindow.incoming(txt);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendPacket(String message) {
		if(!message.startsWith("[")) chatwindow.incoming(message);
		message = "0" + message;
		byte[] data = message.getBytes();
		DatagramPacket packetToSend = new DatagramPacket(data, data.length,
				group, port);
		try {
			s.send(packetToSend);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}