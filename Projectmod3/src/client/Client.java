package client;

import GUI.ChatWindow;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.util.*;

import javax.swing.JOptionPane;

import tests.Echo;

/**
 * The Class Client for our multihop-chatprogram.
 * @author Kevin, Tim, Kimberly, Martijn
 * @version 1.0
 */
public class Client extends Thread {

    /**
     * De window van de client
     */
	private ChatWindow chatwindow;
    /**
     * Het standaard poortnummer
     */
	private static final int port = 4242;
    /**
     * De multicast socket van de client, waarover de pakketten gestuurd worden
     */
    private MulticastSocket s;
    /**
     * Het IP-adres van de gebruiker
     */
    private InetAddress myAddress;
    /**
     * Het multicast adres
     */
	private InetAddress group;
    /**
     * De naam die de gebruiker heeft ingevoerd
     */
	private String myName;
    /**
     * Een lijst met alle clients die geconnect zijn
     */
	private HashMap<Integer, Boolean> stillAlive = new HashMap<Integer, Boolean>();
    /**
     * Een lijst met de namen van de clients
     */
	private HashMap<Integer, Integer> nameIndex = new HashMap<Integer, Integer>();
    /**
     * Een timer
     */
	private Timer timer;
    /**
     * De log van de pakketten
     */
	private PacketLog packetLog;
    /**
     * Ontvangen van bestanden
     */
	public ReceiveFile receiveFileInstance;
    /**
     * Het huidige sequence nummer van de client
     */
	private int currentSeq;
    /**
     * De standaard hopcount die met een pakket mee wordt gestuurd
     */
	private int hopCount;
    /**
     * Beveiligen van pakketten
     */
	private Encryption encryption;
    /**
     * Het nummer van de client
     */
	private int deviceNr;
    /**
     * Checker van de log
     */
	private LogChecker logChecker;

	/**
	 * De constructor van <code>Client</code>
	 * @param c de <code>ChatWindow</code> van de gebruiker.
	 * @param name de naam van de gebruiker.
	 */
	public Client(ChatWindow c, String name) {
		myName = name;
		packetLog = new PacketLog();
		currentSeq = 1;
		hopCount = 3;
		chatwindow = c;
		receiveFileInstance = new ReceiveFile(this);
		encryption = new Encryption();
		logChecker = new LogChecker(this, packetLog);
		Thread t = new Thread(logChecker);
		t.start();

        /*
        Probeer het eigen IP adres op te vragen
         */
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
	
	/**
	 * Een getter voor het huidige Sequence number.
	 * @return het huidige Sequence number.
	 */
	public int getCurrentSeq() {
		return currentSeq;
	}

	/**
	 * Een getter voor het huidige Hop count.
	 * @return het huidige Hop count.
	 */
	public int getHopCount() {
		return hopCount;
	}

	/**
	 * Een getter voor het huidige <code>group</code> IP.
	 * @return het huidige <code>group</code> IP.
	 */
	public InetAddress getGroup() {
		return group;
	}

	/**
	 * Een getter voor het <code>IP Adres</code> van de gebruiker.
	 * @return het <code>IP Adres</code> van de gebruiker.
	 */
	public InetAddress getMyAddress() {
		return myAddress;
	}

	/**
	 * Een getter voor de huidige <code>Port</code>.
	 * @return de huidige <code>Port</code>.
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Een getter voor het <code>Device Number</code> van de gebruiker.
	 * @return het <code>Device Number</code> van de gebruiker.
	 */
	public int getDeviceNr() {
		return deviceNr;
	}

	/**
	 * Een getter voor de naam van de gebruiker.
	 * @return de naam van de gebruiker.
	 */
	public String getClientName() {
		return myName;
	}

	/**
	 * Een method om het sequence nummer op te hogen.
	 */
	public synchronized void incrementSeqNr() {
		currentSeq++;
		if (currentSeq == 256) {
			currentSeq = 0;
		}
	}
	
	/**
	 * Een getter voor de <code>ChatWindow</code> van de gebruiker.
	 * @return the user's <code>ChatWindow</code>.
	 */
	public ChatWindow getChatWindow() {
		return chatwindow;
	}

	/**
	 * Een getter voor de huidige <code>Encryption3</code> van de gebruiker.
	 * @return de huidige <code>Encryption3</code> van de gebruiker.
	 */
	public Encryption getEncryption() {
		return encryption;
	}

	/**
	 * De method om een <code>Packet</code> te verwerken.
	 * @param message de <code>Byte-Array</code> die werd gestuurd.
	 * @param sequenceNr de <code>Integer</code> die de sequence number van de <code>Packet</code> representeerd.
	 * @param hopCount de <code>Integer</code> die de hop count van de <code>Packet</code> representeeerd.
	 * @param sourceAddress de <code>IP Address</code> van de zender.
	 * @param destinationAddress de <code>IP Address</code> van de ontvanger.
	 * @param lengte de lengte van de <code>message</code>.
	 */
	public void processPacket(byte[] message, int sequenceNr, int hopCount,
			InetAddress sourceAddress, InetAddress destinationAddress,
			int lengte) {
		byte[] decrypted = encryption.decryptData(message);
		//String txt = new String((message));
		// Of als encryption is toegevoegd:
		String txt = new String(decrypted);

        /*
        Als het bericht een broadcast is, update de lijst met clients die geconnect zijn
         */
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
				stillAlive.put(hisNr, true);
			}
		}
        /*
        Als het bericht een name_in_use is
         */
        else if (txt.startsWith("[NAME_IN_USE]: ")
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
        /*
        Als het een privé bericht is, laat het alleen aan de ontvanger zien
         */
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
        /*
        Als het een bestand is, ontvang het bestand via receive file
         */
		else if (txt.startsWith("[FILE]")) {
			System.out.println("FILE");
			byte[] fileBytes = new byte[995];
			System.arraycopy(message, 6, fileBytes, 0, 995);
			receiveFileInstance.receiveFile(fileBytes, false, "", "",0);
		}
        /*
        Als het einde van het bestand bereikt is, sla het ergens op
         */
		else if (txt.startsWith("[EOF]")) {
			System.out.println("EOF");
			byte[] extBytes = new byte[3];
			int count = 0;
			for (int i = message.length - 1; i > 20; i--) {
				if (message[i] == 0) {
					count++;
				} else
					break;
			}
			byte[] file = new byte[995 - (count)];
			byte[] numPacket = new byte[6];
			System.arraycopy(message, 16, file, 0, file.length);
			System.arraycopy(message, 6, extBytes, 0, 3);
			System.arraycopy(message, 9, numPacket, 0, 6);
			int devNr = ((int) sourceAddress.getAddress()[3]) & 0xFF;
			String name = chatwindow.pNameList.get(nameIndex.get(devNr));
			int length = Integer.parseInt(new String(numPacket));
			receiveFileInstance.receiveFile(file, true, new String(extBytes),name,length);
		}

		else if (!sourceAddress.equals(myAddress)) {
			chatwindow.incoming(txt);
		}
	}

	/**
	 * De method die een <code>Test-Packet</code> stuurt.
	 * @param message de <code>message</code> om mee te sturen.
	 * @param seq het sequence nummer om te gebruiken. 
	 */
	public void sendTestPacket(String message, int seq) {
		byte[] data = PacketUtils.getData(encryption.encryptData(message.getBytes()), seq, hopCount,
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

	/**
	 * De method om een <code>message</code> te verzenden.
	 * @param message the <code>message</code> om te verzenden.
	 */
	public void sendPacket(String message) {
		if (!message.startsWith("[") && !(chatwindow instanceof Echo)) {
			chatwindow.incoming(message);
		}

		//byte[] data = PacketUtils.getData((message.getBytes()), currentSeq, hopCount, myAddress, group);
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

	/**
	 * De method om een <code>File</code> te verzenden.
	 * @param message de <code>Byte-Array</code> die een stuk van de <code>File</code> representeerd.
	 * @param isFile een <code>Boolean</code> om deze method van de andere <code>sendPacket()</code> te onderscheiden.
	 */
	public void sendPacket(byte[] message, boolean isFile) {
		byte[] data = PacketUtils.getData(message, currentSeq, hopCount,myAddress, group);
		//byte[] data = PacketUtils.getData((encryption.encryptData(message)),currentSeq, hopCount, myAddress, group);
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

	/**
	 * De method om een <code>Packet</code> te verzenden.
	 * @param packet de <code>Packet</code> om te verzenden.
	 */
	public void resendPacket(DatagramPacket packet) {
		try {
			s.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("WE HAVE A PROBLEM AT THE RESEND METHOD!!");
			System.out.println("ERMAGHERD!! D:");
		}
	}
	
	/**
	 * De method om een <code>File</code> klaar te maken om te verzenden.
	 * @param f de <code>File</code> om te verzenden.
	 */
	public void sendFile(File f) {
		SendFile sender = new SendFile(this, f, group, port);
		Thread t = new Thread(sender);
		t.start();
	}

	/**
	 * De method om een <code>PrivateMessage</code> te verzenden.
	 * @param target de <code>Target</code> om de <code>PrivateMessage</code> naar toe te zenden.
	 * @param message de <code>message</code> om te verzenden.
	 */
	public void sendPrivate(String target, String message) {
		String returnString = "[PRIV_MSG]: " + target + " " + message;
		sendPacket(returnString);
	}
	
	/**
	 * De method om te testen of andere gebruikers nog geconnect zijn.
	 */
	public synchronized void checkConnections() {
		List<Integer> remove = new ArrayList<Integer>();
		for (Integer i : stillAlive.keySet()) {
			if (!stillAlive.get(i)) {
				if(nameIndex.containsKey(i)) {
					chatwindow.incoming(chatwindow.pNameList.get(nameIndex.get(i))+ " has left.");
					chatwindow.disconnect(chatwindow.pNameList.get(nameIndex.get(i)));
				}
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

	/**
	 * De method die een <code>Packet</code> of doorstuurt, of dropped, of opslaat in een <code>PacketLog</code>.
	 */
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
			String txt = new String(message);
			
			if(stillAlive.containsKey(devNr)) stillAlive.put(devNr, true);
			
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
					if(hop == 0) packetLog.addReceivePacket(devNr, sequence, packet);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * TODO ==> Martijn, doe er wat aan.
	 */
	public void testRTT(){
		long time = System.currentTimeMillis();
		byte[] timeCommand = "[TIME_STAMP]".getBytes();
		byte[] timeUnit = ByteBuffer.allocate(8).putLong(time).array();
		byte[] message = new byte[timeCommand.length + timeUnit.length];
		System.arraycopy(timeCommand, 0, message, 0, timeCommand.length);
		System.arraycopy(timeUnit, 0, message, timeCommand.length, message.length);
		sendPacket(message, false);
	}

	/**
	 * De method die <code>routePacket</code> blijft aanroepen om <code>Packets</code> te kunnen ontvangen.
	 */
	public void run() {
		while (!s.isClosed()) {
			routePacket();
		}
	}

}
