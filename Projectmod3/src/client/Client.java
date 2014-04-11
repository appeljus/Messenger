package client;

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;

import javax.xml.bind.DatatypeConverter;

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
    ReceiveFile receiveFileInstance;
	
	private static final int BUFFER_SIZE = 16;
	ArrayList<DatagramPacket> lastMsgs = new ArrayList<DatagramPacket>();
	HashMap<Integer, Integer> seqNrs = new HashMap<Integer, Integer>();
	int currentSeq;
	
	int hopCount;
	
	public Client(ChatWindow c, String name) {
		myName = name;
        packetLog = new PacketLog();
        currentSeq = 0;
        hopCount = 4;

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
		receiveFileInstance = new ReceiveFile(this);
		keyPair = Encryption.generateKey();
		pubKeys.add(keyPair.getPublic());
		stillAlive.add(true);
		try {
			group = InetAddress.getByName("228.5.6.7");
			s = new MulticastSocket(port);
			s.joinGroup(group);
			this.sendPacket("[BROADCAST]: " + myName + " DUMMY_WORD");
			Thread t = new Thread(this);
			t.start();
			timer = new Timer();
			timer.scheduleAtFixedRate(new SecondTimer(this), 1000, 1000);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}

    public int getCurrentSeq(){
        return currentSeq;
    }

    public int getHopCount(){
        return hopCount;
    }

    public InetAddress getGroup(){
        return group;
    }

    public InetAddress getMyAddress(){
        return myAddress;
    }

	public String getClientName() {
		return myName;
	}

	public Key getPubKey() {
		return pubKeys.get(0);
	}

	public void run() {
		while (!s.isClosed()) {
			forwardPacket();
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
	
	public synchronized void incrementSeqNr(){
		currentSeq++;
	}
	
	public void receivePacket(byte[] message, int sequenceNr, int hopCount, InetAddress sourceAddress, InetAddress destinationAddress) {
		String txt = new String(message);
        byte[] DataToSave = PacketUtils.getData(message, sequenceNr, hopCount, sourceAddress, destinationAddress);
        packetLog.addReceivedPacket(new DatagramPacket(DataToSave, DataToSave.length, sourceAddress, port));

        int thisSeq = sequenceNr;
        int thisHop = hopCount;
        int nextHop = hopCount;
        InetAddress source = sourceAddress;
        InetAddress destination = destinationAddress;

        if (txt.startsWith("[BROADCAST]") && !sourceAddress.equals(myAddress)) {
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

        } else if (txt.startsWith("[NAME_IN_USE]: ") && !sourceAddress.equals(myAddress)) {
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
            System.out.println("|"+words[1]+"|");
            int missedI = Integer.parseInt(words[1]);
            if(missedI < currentSeq-BUFFER_SIZE){
                byte[] data = "[TOO_LATE]".getBytes();
                DatagramPacket rePacket = new DatagramPacket(data, data.length,
                        group, port);
                resendPacket(rePacket);
            }else{
            int iOfList = BUFFER_SIZE-(currentSeq-missedI)-1;
            resendPacket(lastMsgs.get(iOfList));
            }
        }

        else if(txt.startsWith("[TOO_LATE]")){
            //TODO
            // ?????
        }

        else if(txt.startsWith("[FILE]")) {
            //SEQHOPSOUDES[FILE]{file}
            // 1  1  4  4   6      x   = 16 + x = 1024 ==> x = 1008
            byte[] fileBytes = new byte[1003];
            System.arraycopy(message, 6, fileBytes, 0, 1003);
            receiveFileInstance.receiveFile(fileBytes, false, "");
        }

        else if(txt.startsWith("[EOF]")) {
            //SEQHOPSOUDES[EOF][EXT]{file}
            // 1  1  4  4   5    6    x    = 21 + x = 1024 ==> x = 1003
            byte[] fileBytes = new byte[1003];
            byte[] extBytes = new byte[6];
            System.arraycopy(message, 21, fileBytes, 0, 1003);
            System.arraycopy(message, 15, extBytes, 0, 6);
            receiveFileInstance.receiveFile(fileBytes, true, new String(extBytes));
        }

        else if(!sourceAddress.equals(myAddress)){
            chatwindow.incoming(txt);
        }

        byte[] addrB = sourceAddress.getAddress();
        int deviceNr = ((int)(addrB[3])) & 0xFF;
        deviceNr--;

        if(!seqNrs.containsKey(deviceNr)){
            seqNrs.put(deviceNr, thisSeq);
        } else {
            if(seqNrs.get(deviceNr)+1 < thisSeq){
                for(int i = seqNrs.get(deviceNr)+1; i < thisSeq; i++){
                    String msg = "[NACK]: " + i + " DUMMY_WORD ";
                    sendPacket(msg);
                }
            }
            seqNrs.put(deviceNr, thisSeq);
        }
	}

	public void sendPacket(String message) {		
		if(!message.startsWith("[")) chatwindow.incoming(message);
		
		byte[] data = PacketUtils.getData(message.getBytes(), currentSeq, hopCount, myAddress, group);

        DatagramPacket packetToSend = new DatagramPacket(data, data.length,
				group, port);
        
        lastMsgs.add(packetToSend);

		try {
			s.send(packetToSend);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		incrementSeqNr();
	}

    public void resendPacket(DatagramPacket packet){
        try {
            s.send(packet);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
	
	public void sendFile(File f){
		SendFile sender = new SendFile(this, f, group, port);
		Thread t = new Thread(sender);
		t.start();
	}
	
	public void sendPrivate(String target, String message) {
		String returnString = "[PRIV_MSG]: " + target + " " + message;
		sendPacket(returnString);
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

    public void forwardPacket(){
        byte[] data = new byte[1024];
        DatagramPacket packet = new DatagramPacket(data, data.length);

        try {
            s.receive(packet);

            byte[] message = PacketUtils.getMessage(packet);
            int sequence = PacketUtils.getSequenceNr(packet);
            int hop = PacketUtils.getHopCount(packet);
            InetAddress sourceAddress = PacketUtils.getSourceAddress(packet);
            InetAddress destinationAddress = PacketUtils.getDistinationAddress(packet);

            if (!sourceAddress.equals(myAddress) && !packetLog.containsReceivedSeq(sequence)){
                receivePacket(message, sequence, hop, sourceAddress, destinationAddress);
            }
            else if (!sourceAddress.equals(myAddress) && packetLog.containsReceivedSeq(sequence)){
                hop = hop - 1;
                byte[] dataToSend = PacketUtils.getData(message, sequence, hop, sourceAddress, destinationAddress);
                resendPacket(new DatagramPacket(dataToSend, dataToSend.length, sourceAddress, port));
            }

        } catch (   IOException e) {
            e.printStackTrace();
        }
    }
}
