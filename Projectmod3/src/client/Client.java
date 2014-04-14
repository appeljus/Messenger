package client;

import GUI.ChatWindow;
import GUI.LoginWindow;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.security.Key;
import java.util.*;

public class Client extends Thread {

	private ChatWindow chatwindow;
	private static final int port = 4242;
	private MulticastSocket s;
	private InetAddress myAddress;
	private InetAddress group;
	private String myName;
	private List<Boolean> stillAlive = new ArrayList<Boolean>();
	private Timer timer;
	private PacketLog packetLog;
	private ReceiveFile receiveFileInstance;
	private static final int BUFFER_SIZE = 16;
	private ArrayList<DatagramPacket> lastMsgs = new ArrayList<DatagramPacket>();
	private HashMap<Integer, Integer> seqNrs = new HashMap<Integer, Integer>();
	private int currentSeq;
	private int hopCount;
	private Encryption encryption;

	public Client(ChatWindow c, String name) {
		myName = name;
		packetLog = new PacketLog();
		currentSeq = 1;
		hopCount = 4;
		chatwindow = c;
		receiveFileInstance = new ReceiveFile(this);
		stillAlive.add(true);
		encryption = new Encryption();
		encryption.setPassword("Doif");

		try {
			Enumeration e = NetworkInterface.getNetworkInterfaces();
			while (e.hasMoreElements()) {
				NetworkInterface n = (NetworkInterface) e.nextElement();
				Enumeration ee = n.getInetAddresses();
				while (ee.hasMoreElements()) {
					InetAddress i = (InetAddress) ee.nextElement();
					if (n.getDisplayName().contains("wlan")) {
						myAddress = i;
					}
				}
			}

			group = InetAddress.getByName("228.5.6.7");
			s = new MulticastSocket(port);
			s.joinGroup(group);
			this.sendPacket("[BROADCAST]: " + myName + " DUMMY_WORD");
			Thread t = new Thread(this);
			t.start();
			timer = new Timer();
			timer.scheduleAtFixedRate(new SecondTimer(this), 1000, 1000);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getCurrentSeq() {
		return currentSeq;
	}

	public int getHopCount() {
		return hopCount;
	}

	public InetAddress getGroup() {
		return group;
	}

	public InetAddress getMyAddress() {
		return myAddress;
	}

	public String getClientName() {
		return myName;
	}

	public synchronized void incrementSeqNr() {
		currentSeq++;
	}

	public ChatWindow getChatWindow() {
		return chatwindow;
	}

	public Encryption getEncryption() {
		return encryption;
	}

	public void receivePacket(byte[] message, int sequenceNr, int hopCount,
			InetAddress sourceAddress, InetAddress destinationAddress) {
		String txt = new String((message));

		if (txt.startsWith("[BROADCAST]") && !sourceAddress.equals(myAddress)) {
			String[] words = txt.split(" ");
			if (words[1].equals(myName)) {
				this.sendPacket("[NAME_IN_USE]: " + words[1] + " STUFF");
			}
			if (chatwindow.pNameList.contains(words[1])) {
				stillAlive.set(chatwindow.pNameList.indexOf(words[1]), true);
			} else {
				chatwindow.updateNames(words[1]);
				stillAlive.add(true);
			}

		} else if (txt.startsWith("[NAME_IN_USE]: ")
				&& !sourceAddress.equals(myAddress)) {
			String[] words = txt.split(" ");
			if (myName.equals(words[1])) {
				chatwindow.dispose();
				new LoginWindow();
				s.close();
				timer.cancel();

			}
		}

		else if (txt.startsWith("[PRIV_MSG]: ")) {
			String[] words = txt.split(" ");
			if (words[1].equals(myName)) {
				String data = "";
				for (int i = 3; i < words.length; i++) {
					data = data + words[i] + " ";
				}
				chatwindow.privateIncoming(words[2], data);
			}
		}

		else if (txt.startsWith("[NACK]: ")) {
			String[] words = txt.split(" ");
			int missedI = Integer.parseInt(words[1]);
			resendPacket(packetLog.getPacketSend(missedI));
		}

		else if (txt.startsWith("[FILE]")) {
			byte[] fileBytes = new byte[1003];
			System.arraycopy(message, 6, fileBytes, 0, 1003);
			receiveFileInstance.receiveFile(fileBytes, false, "");
		}

		else if (txt.startsWith("[EOF]")) {
			byte[] extBytes = new byte[3];
			int count = 0;
			for (int i = message.length - 1; i > 20; i--) {
				if (message[i] == 0) {
					count++;
				} else
					break;
			}
			byte[] file = new byte[1003 - (count - 1)];
			System.arraycopy(message, 11, file, 0, file.length);
			System.arraycopy(message, 6, extBytes, 0, 3);
			receiveFileInstance.receiveFile(file, true, new String(extBytes));
		}

		else if (!sourceAddress.equals(myAddress)) {
			chatwindow.incoming(txt);
		}

		byte[] addrB = sourceAddress.getAddress();
		int deviceNr = ((int) (addrB[3])) & 0xFF;
		deviceNr--;

		if (!seqNrs.containsKey(deviceNr)) {
			seqNrs.put(deviceNr, sequenceNr);
		} else {
			if (seqNrs.get(deviceNr) + 1 < sequenceNr) {
				for (int i = seqNrs.get(deviceNr) + 1; i < sequenceNr; i++) {
					String msg = "[NACK]: " + i + " DUMMY_WORD ";
					sendPacket(msg);
				}
			}
			seqNrs.put(deviceNr, sequenceNr);
		}
	}

	public void sendPacket(String message) {
		if (!message.startsWith("[")) {
			chatwindow.incoming(message);
		}

		byte[] data = PacketUtils.getData((message.getBytes()), currentSeq,
				hopCount, myAddress, group);

		DatagramPacket packetToSend = new DatagramPacket(data, data.length,
				group, port);

		// lastMsgs.add(packetToSend);

		packetLog.addSendPacket(packetToSend);

		try {
			s.send(packetToSend);
		} catch (IOException e) {
			System.out.println("WE HAVE A PROBLEM AT THE SEND METHOD!!");
			System.out.println("ERMAGHERD!! D:");
		}
		incrementSeqNr();
	}

	public void sendPacket(byte[] message, boolean isFile) {
		byte[] data = PacketUtils.getData(message, currentSeq, hopCount,
				myAddress, group);
		DatagramPacket packetToSend = new DatagramPacket(data, data.length,
				group, port);
		packetLog.addSendPacket(packetToSend);

		try {
			s.send(packetToSend);
		} catch (IOException e) {
			System.out.println("WE HAVE A PROBLEM AT THE SEND METHOD!!");
			System.out.println("ERMAGHERD!! D:");
		}
		incrementSeqNr();
	}

	public void resendPacket(DatagramPacket packet) {
		try {
			s.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("WE HAVE A PROBLEM AT THE RESEND METHOD!!");
			System.out.println("ERMAGHERD!! D:");
		}
	}

	public void sendFile(File f) {
		SendFile sender = new SendFile(this, f, group, port);
		Thread t = new Thread(sender);
		t.start();
	}

	public void sendPrivate(String target, String message) {
		String returnString = "[PRIV_MSG]: " + target + " " + message;
		sendPacket(returnString);
	}

	public void checkConnections() {
		for (int i = 0; i < chatwindow.pNameList.size(); i++) {
			if (!stillAlive.get(i)) {
				chatwindow.incoming(chatwindow.pNameList.get(i) + " has left!");
				chatwindow.disconnect(chatwindow.pNameList.get(i));
				stillAlive.remove(i);
			} else
				stillAlive.set(i, false);
		}
		stillAlive.set(0, true);
	}

	public void forwardPacket() {
		byte[] data = new byte[1034];
		DatagramPacket packet = new DatagramPacket(data, data.length);

		try {
			s.receive(packet);
			
			byte[] message = PacketUtils.getMessage(packet);
			int sequence = PacketUtils.getSequenceNr(packet);
			int hop = PacketUtils.getHopCount(packet);
			InetAddress sourceAddress = PacketUtils.getSourceAddress(packet);
			InetAddress destinationAddress = PacketUtils.getDistinationAddress(packet);
			
			int deviceNr = ((int) sourceAddress.getAddress()[3]) & 0xFF;
			if(!packetLog.hasDevice(deviceNr)) {
				packetLog.addSequenceNr(deviceNr, sequence);
			}
			int latestSeq = packetLog.getLatestSeq(deviceNr);
			
			System.out.println(latestSeq + " | "+ sequence+ " | "+ sourceAddress.getHostAddress());

			if (!sourceAddress.getHostAddress().startsWith("192.168.5.")) {
			} 
			else if (!sourceAddress.equals(myAddress)) {
				hop--;
				if (!destinationAddress.equals(myAddress) && hop != 0) {
					byte[] dataToSend = PacketUtils.getData(message, sequence, hop, group, destinationAddress);
					resendPacket(new DatagramPacket(dataToSend, dataToSend.length, group, port));
				}
				
				else if (destinationAddress.equals(myAddress) || destinationAddress.equals(group)) {
					if (latestSeq + 1 == sequence) {
						System.out.println("RECEIVEd");
						receivePacket(message, sequence, hop, sourceAddress, destinationAddress);
					} else if (latestSeq + 1 < sequence) {
						for (int i = latestSeq + 1; i < sequence; i++) {
							String msg = "[NACK]: " + i + " DUMMY_LORD";
							System.out.println("NACKER");
							DatagramPacket packetToSend = new DatagramPacket( msg.getBytes(), msg.getBytes().length, sourceAddress, port);
							resendPacket(packetToSend);
						}
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while (!s.isClosed()) {
			forwardPacket();
		}
	}

}
