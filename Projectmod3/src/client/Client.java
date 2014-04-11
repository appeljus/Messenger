package client;

import java.io.File;
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
    PacketLog packetLog;
	
	private static final int BUFFER_SIZE = 16;
	ArrayList<DatagramPacket> lastMsgs = new ArrayList<DatagramPacket>();
	HashMap<Integer, Integer> seqNrs = new HashMap<Integer, Integer>();
	int currentSeq = 0;
	
	int hopCount = 0; // dummy .. temp... dinges..
	
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
			this.sendPacket("[BROADCAST]: " + myName);
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
	
	private synchronized void incrementSeqNr(){
		currentSeq++;
	}
	
	public void receivePacket() {
		try {
			byte[] buf = new byte[1024];
			DatagramPacket packet = new DatagramPacket(buf, buf.length); //[BC]: NAME

			s.receive(packet);
			byte[] receiveData = packet.getData();

            byte[] message = new byte[receiveData.length - 10];
            String txt = new String(message);
            System.arraycopy(receiveData, 10, message, 0, receiveData.length - 10);

			int thisSeq = receiveData[0];
			int thisHop = receiveData[1];
			int nextHop = thisHop - 1;
            byte[] src = {receiveData[2], receiveData[3], receiveData[4], receiveData[5]};
            byte[] dst = {receiveData[6], receiveData[7], receiveData[8], receiveData[9]};
			InetAddress source = InetAddress.getByAddress(src);
			InetAddress destination = InetAddress.getByAddress(dst);
			
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
			
			else if(txt.startsWith("[NACK]: ")){
				String[] words = txt.split(" ");
				int missedI = Integer.parseInt(words[1]);
				if(missedI < currentSeq-BUFFER_SIZE){
					byte[] data = "[TOO_LATE]".getBytes();
					DatagramPacket rePacket = new DatagramPacket(data, data.length,
							group, port);
					retransmit(rePacket);
				}else{
				int iOfList = BUFFER_SIZE-(currentSeq-missedI)-1;
				retransmit(lastMsgs.get(iOfList));
				}
			}
			else if(txt.startsWith("[TOO_LATE]")){
				
			}
			
			else if(!packet.getAddress().equals(myAddress)){
				chatwindow.incoming(txt);
			}
			
			InetAddress addr = packet.getAddress();
			byte[] addrB = addr.getAddress();
			int deviceNr = ((int)(addrB[3])) & 0xFF;
			deviceNr--;
			
			if(!seqNrs.containsKey(deviceNr)){
				seqNrs.put(deviceNr, thisSeq);
			} else {
				if(seqNrs.get(deviceNr)+1 < thisSeq){
					for(int i = seqNrs.get(deviceNr)+1; i < thisSeq; i++){
						String msg = "[NACK]: " + i;
					}
				}
				seqNrs.put(deviceNr, thisSeq);
			}
			
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void retransmit(DatagramPacket packet){
		try {
			s.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendPacket(String message) {		
		if(!message.startsWith("[")) chatwindow.incoming(message);
		
		byte[] data = Packet.getData(message.getBytes(), currentSeq, hopCount, myAddress, group);

        DatagramPacket packetToSend = new DatagramPacket(data, data.length,
				group, port);
		
		lastMsgs.add(packetToSend);
		if(lastMsgs.size() > BUFFER_SIZE){
			lastMsgs.remove(0);
		}
		try {
			s.send(packetToSend);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		incrementSeqNr();
	}
	
	public void sendFile(File f){
		SendFile sender = new SendFile(f, group, port);
		Thread t = new Thread(sender);
		t.start();
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

    public void forwardPacket(DatagramPacket packet){
        byte[] data = packet.getData();
        byte[] sourceAddress = {data[2], data[3], data[4], data[5]};
        byte[] destinationAddress = {data[6], data[7], data[8], data[9]};
        try {
            if (!InetAddress.getByAddress(destinationAddress).equals(myAddress)){
                if(InetAddress.getByAddress(sourceAddress).equals(myAddress)){
                    //Drop packet
                }
                if (data[0] > 0){
                    data[0] = (byte)((int)data[0] - 1);
                    //Send packet
                }
                else{
                    //Drop packet
                }

            }
            else {
                if (!packetLog.containsSeq(data[0])){
                    //Accept packet
                }
                else{
                    //Drop packet
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

}
