package client;

import java.io.IOException;
import java.net.*;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
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
	KeyPair keyPair;
	List<Key> pubKeys = new ArrayList<Key>();
	List<Boolean> stillAlive = new ArrayList<Boolean>();
	Timer timer;
	
	HashMap<Integer, List<Integer>> seqNrs = new HashMap<Integer, List<Integer>>();
	int current_sqn = 0;
	
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
		keyPair = Encryption.generateKey();
		pubKeys.add(keyPair.getPublic());
		stillAlive.add(true);
		try {
			group = InetAddress.getByName("228.5.6.7");
			s = new MulticastSocket(port);
			s.joinGroup(group);
			this.sendPacket(myName, pubKeys.get(0));
			Thread t = new Thread(this);
			t.start();
			timer = new Timer();
			timer.scheduleAtFixedRate(new SecondTimer(this), 1000, 1000);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}

	public String getClientName() {
		return myName;
	}

	public Key getPubKey() {
		return pubKeys.get(0);
	}

	public void run() {
		while (!s.isClosed()) {
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
	
	public byte[] removeZeros(byte[] data){
		int count = 0;
		for(int i=data.length-1; i>0; i--){
			if(data[i] == 0){
				count++;
			}
			else break;
		}
		byte[] returnData = new byte[data.length-count-1];
		for(int i=0; i<returnData.length; i++){
			returnData[i] = data[i];
		}
		return returnData;
	}
	
	public Key extractKey(byte[] data) {
		byte[] betterData = removeZeros(data);
		byte[] keyData = new byte[162];
		
		for(int i=0; i<betterData.length-162; i++){
			data[i] = betterData[i];
		}
		for(int i=13; i<175; i++){
			keyData[i - 13] = betterData[i];
		}
		
		Key k2 = null;
		byte[] bytes = keyData;
		try {
			k2 = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(bytes));
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) { 
			e.printStackTrace();
		}
		return k2;
	}
	
	public byte[] removeFirst(byte[] data, int num){
		byte[] returnByte = new byte[data.length-num];
		for(int i=num; i<data.length; i++){
			returnByte[i-num] = data[i];
		}
		return returnByte;
	}
	
	public void receivePacket() {
		try {
			byte[] buf = new byte[1024];
			DatagramPacket packet = new DatagramPacket(buf, buf.length); //[BC]: NAME

			s.receive(packet);
			byte[] receiveData = packet.getData();
			String txt = new String(receiveData, "UTF-8");
			
			if (txt.startsWith("[BROADCAST]:") && !packet.getAddress().equals(myAddress)) {
				String[] words = txt.split(" ");
				if (words[1].equals(myName)) {
					this.sendPacket("[NAME_IN_USE]: " + words[1] + " STUFF");
				}
				if(chatwindow.pNameList.contains(words[1])){
					stillAlive.set(chatwindow.pNameList.indexOf(words[1]),true);
				}
				else {
					chatwindow.updateNames(words[1]);
					stillAlive.add(true);
					//Key k = extractKey(receiveData);
					//pubKeys.add(k);
				}
			} else if (txt.startsWith("[NAME_IN_USE]: ") && !packet.getAddress().equals(myAddress)) {
				System.out.println(txt);
				String[] words = txt.split(" ");
				if (myName.equals(words[1])) {
					chatwindow.dispose();
					new LoginWindow();
					s.close();
					timer.cancel();
					
				}
			}
			else if(txt.startsWith("[PRIV_MSG]: ")){
				String[] words = txt.split(" ");
				if(words[1].equals(myName)){
					String data = "";
					for(int i=3; i<words.length; i++){
						data = data + words[i] + " ";
					}
					//data = data.substring(0, data.length()-1);
					chatwindow.privateIncoming(words[2], data);
				}
			}
			
			else if(!packet.getAddress().equals(myAddress)){
				chatwindow.incoming(txt);
			}
			
			InetAddress addr = packet.getAddress();
			byte[] addrB = addr.getAddress();
			int deviceNr = ((int)(addrB[3])) & 0xFF;
			deviceNr--;
			
			if(seqNrs.containsKey(deviceNr)){
				seqNrs.get(deviceNr).add(3);//#########################
			}
			else{
				seqNrs.put(deviceNr, new ArrayList<Integer>());
				seqNrs.get(deviceNr).add(3);//#########################
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendPacket(String message) {
		if(!message.startsWith("[")) chatwindow.incoming(message);
		byte[] data = message.getBytes();
		DatagramPacket packetToSend = new DatagramPacket(data, data.length,
				group, port);
		try {
			s.send(packetToSend);
		} catch (IOException e) {
			e.printStackTrace();
		}
		current_sqn++;
	}
	
	public void sendPacket(String message, Key k) {
		/*byte[] bCast = "[BROADCAST]: ".getBytes();
		message = " " + message + " ";
		byte[] data = message.getBytes();
		byte[] key = k.getEncoded();
		byte[] filler = { new Byte((byte) 255) } ;
		byte[] destination = new byte[bCast.length + data.length + key.length + filler.length];
		System.arraycopy(bCast, 0, destination, 0, bCast.length);
		System.arraycopy(key, 0, destination, bCast.length, key.length);
		System.arraycopy(data, 0, destination, bCast.length + key.length, data.length);
		System.arraycopy(filler, 0, destination, bCast.length + key.length + data.length, filler.length);*/
		
		byte[] destination = ("[BROADCAST]: " + message + " STUFF").getBytes();
		DatagramPacket packetToSend = new DatagramPacket(destination, destination.length, group, port);
		try {
			s.send(packetToSend);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendPrivate(String target, String message) {
		byte[] returnBytes = ("[PRIV_MSG]: " + target + " " + message).getBytes();
		DatagramPacket packetToSend = new DatagramPacket(returnBytes, returnBytes.length, group, port);
		try {
			s.send(packetToSend);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void checkConnections(){
		for(int i=0; i<chatwindow.pNameList.size(); i++){
			if(!stillAlive.get(i)){
				chatwindow.incoming(chatwindow.pNameList.get(i) + " has left!");
				chatwindow.disconnect(chatwindow.pNameList.get(i));
				//pubKeys.remove(i);
				stillAlive.remove(i);
			}
			else stillAlive.set(i, false);
		}
		stillAlive.set(0, true);
	}
}
