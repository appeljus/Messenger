package client;

import GUI.ChatWindow;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.*;

import javax.swing.JOptionPane;

import tests.Echo;

public class Client extends Thread {

	private ChatWindow chatwindow;
	private static final int port = 4242;
	private MulticastSocket s;
	private InetAddress myAddress;
	private InetAddress group;
	private String myName;
	private HashMap<Integer, Boolean> stillAlive = new HashMap<Integer, Boolean>();
	private HashMap<Integer, Integer> nameIndex = new HashMap<Integer, Integer>();
	private Timer timer;
	private PacketLog packetLog;
	public ReceiveFile receiveFileInstance;
	private int currentSeq;
	private int hopCount;
	private Encryption encryption;
	private int deviceNr;
	private LogChecker logChecker;

	public Client(ChatWindow c, String name) {
		myName = name;
		packetLog = new PacketLog();
		currentSeq = 1;
		hopCount = 3;
		chatwindow = c;
		receiveFileInstance = new ReceiveFile(this);
		encryption = new Encryption();
		encryption.setPassword("Doif");
		logChecker = new LogChecker(this, packetLog);
		Thread t = new Thread(logChecker);
		t.start();

		try {
			Enumeration<NetworkInterface> e = NetworkInterface
					.getNetworkInterfaces();
			while (e.hasMoreElements()) {
				NetworkInterface n = (NetworkInterface) e.nextElement();
				Enumeration<InetAddress> ee = n.getInetAddresses();
				while (ee.hasMoreElements()) {
					InetAddress i = (InetAddress) ee.nextElement();
					if (n.getDisplayName().contains("wlan")) {
						myAddress = i;
					}
				}
			}
			byte[] addr = myAddress.getAddress();
			deviceNr = ((int) addr[3]) & 0xFF;
			stillAlive.put(deviceNr, true);
			nameIndex.put(deviceNr, 0);
			group = InetAddress.getByName("228.5.6.7");
			s = new MulticastSocket(port);
			s.joinGroup(group);
			this.sendPacket("[BROADCAST]: " + myName + " DUMMY_WORD");
			t = new Thread(this);
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

	public int getPort() {
		return port;
	}

	public int getDeviceNr() {
		return deviceNr;
	}

	public String getClientName() {
		return myName;
	}

	public synchronized void incrementSeqNr() {
		currentSeq++;
		if (currentSeq == 256) {
			currentSeq = 0;
		}
	}

	public ChatWindow getChatWindow() {
		return chatwindow;
	}

	public Encryption getEncryption() {
		return encryption;
	}

	public void processPacket(byte[] message, int sequenceNr, int hopCount,
			InetAddress sourceAddress, InetAddress destinationAddress,
			int lengte) {
		byte[] decrypted = encryption.decryptData(message);
		//String txt = new String((message));
		// Of als encryption is toegevoegd:
		String txt = new String(decrypted);

		if (txt.startsWith("[BROADCAST]") && !sourceAddress.equals(myAddress)) {
			String[] words = txt.split(" ");
			int hisNr = ((int) sourceAddress.getAddress()[3]) & 0xFF;
			if (words[1].equals(myName)) {
				this.sendPacket("[NAME_IN_USE]: " + words[1] + " STUFF");
			}
			if(!stillAlive.containsKey(hisNr)) {
				System.out.println("YO " + words[1]);
				chatwindow.updateNames(words[1]);
				int index = chatwindow.pNameList.indexOf(words[1]);
				nameIndex.put(hisNr, index);
			}
			stillAlive.put(hisNr, true);
		} else if (txt.startsWith("[NAME_IN_USE]: ")
				&& !sourceAddress.equals(myAddress)) {
			String[] words = txt.split(" ");
			if (myName.equals(words[1])) {
				chatwindow.dispose();
				String name = JOptionPane.showInputDialog("What is your name?");
				if (name != null)
					new ChatWindow(name, null);
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

		// else if (txt.startsWith("[NACK]")) {

		// }

		else if (txt.startsWith("[FILE]")) {
			byte[] fileBytes = new byte[1001];
			System.arraycopy(message, 6, fileBytes, 0, 1001);
			receiveFileInstance.receiveFile(fileBytes, false, "", "");
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
			byte[] file = new byte[1001 - (count)];
			System.arraycopy(message, 10, file, 0, file.length);
			System.arraycopy(message, 6, extBytes, 0, 3);
			int devNr = ((int) sourceAddress.getAddress()[3]) & 0xFF;
			String name = chatwindow.pNameList.get(nameIndex.get(devNr));
			receiveFileInstance.receiveFile(file, true, new String(extBytes),
					name);
		}

		else if (!sourceAddress.equals(myAddress)) {
			chatwindow.incoming(txt);
		}
	}

	public void sendTestPacket(String message, int seq) {
		byte[] data = PacketUtils.getData((message.getBytes()), seq, hopCount,
				myAddress, group);

		DatagramPacket packetToSend = new DatagramPacket(data, data.length,
				group, port);
		packetLog.addSendPacket(packetToSend);

		try {
			s.send(packetToSend);
		} catch (IOException e) {
			System.out.println("WE HAVE A PROBLEM AT THE TEST SEND METHOD!!");
			System.out.println("ERMAGHERD!! D:");
		}
	}

	public void sendPacket(String message) {
		if (!message.startsWith("[") && !(chatwindow instanceof Echo)) {
			chatwindow.incoming(message);
		}

		//byte[] data = PacketUtils.getData((message.getBytes()), currentSeq,
			//	hopCount, myAddress, group);
		// Of als encryption toegevoegd is:
		byte[] data = PacketUtils.getData((encryption.encryptData(message.getBytes())),currentSeq, hopCount, myAddress, group);

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

	public void sendPacket(byte[] message, boolean isFile) {
		//byte[] data = PacketUtils.getData(message, currentSeq, hopCount,myAddress, group);
		byte[] data = PacketUtils.getData((encryption.encryptData(message)),currentSeq, hopCount, myAddress, group);
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

	public synchronized void checkConnections() {
		List<Integer> remove = new ArrayList<Integer>();
		for (Integer i : stillAlive.keySet()) {
			if (!stillAlive.get(i)) {
				chatwindow.incoming(chatwindow.pNameList.get(nameIndex.get(i))+ " has left.");
				chatwindow.disconnect(chatwindow.pNameList.get(nameIndex.get(i)));
				packetLog.removeDevice(i);
				logChecker.removeDevice(i);
				remove.add(i);
			} else
				stillAlive.put(i, false);
		}
		for (int i = 0; i < remove.size(); i++) {
			stillAlive.remove(remove.get(i));
			nameIndex.remove(remove.get(i));
		}
		stillAlive.put(deviceNr, true);
	}

	public void routePacket() {
		byte[] data = new byte[1034];
		DatagramPacket packet = new DatagramPacket(data, data.length);

		try {
			s.receive(packet);
			byte[] message = PacketUtils.getMessage(packet);
			int sequence = PacketUtils.getSequenceNr(packet);
			int hop = PacketUtils.getHopCount(packet);
			InetAddress sourceAddress = PacketUtils.getSourceAddress(packet);
			InetAddress destinationAddress = PacketUtils.getDistinationAddress(packet);
			int devNr = ((int) (sourceAddress.getAddress()[3]) & 0xFF);
			String txt = new String(encryption.decryptData(message));
			if (txt.startsWith("[NACK]")) {
				String[] words = txt.split(" ");
				int missedI = Integer.parseInt(words[1]);
				DatagramPacket p = packetLog.getPacketSend(missedI);
				if (p == null) {
					byte[] data2 = PacketUtils.getData(("[MSG_LOST] " + missedI + " DUMMY_WORD").getBytes(), 0, getHopCount(),getMyAddress(), sourceAddress);
					p = new DatagramPacket(data2, data2.length, sourceAddress,getPort());
				} else {
					p.setAddress(sourceAddress);
				}
				resendPacket(p);
			} else if (txt.startsWith("[MSG_LOST]")) {
				String[] words = txt.split(" ");
				int missedI = Integer.parseInt(words[1]);
				logChecker.seqGone(devNr, missedI);
			} else if (sourceAddress.getHostAddress().startsWith("192.168.5.") || sourceAddress.getHostAddress().startsWith("228.5.6.7")) {
				if(!myAddress.equals(destinationAddress)) {
					if(myAddress.equals(sourceAddress)) {
						return;
					}
					else if(hop > 0) {
						hop--;
						byte[] pData = PacketUtils.getData(message, sequence, hop, sourceAddress, destinationAddress);
						DatagramPacket packetToSend = new DatagramPacket(pData,pData.length, destinationAddress, port);
						packetLog.addSendPacket(packetToSend);
						try {
							s.send(packetToSend);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					else {
						return;
					}
				}
				if(myAddress.equals(destinationAddress) || group.equals(destinationAddress)){
					packetLog.addReceivePacket(devNr, sequence, packet);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while (!s.isClosed()) {
			routePacket();
		}
	}

}
