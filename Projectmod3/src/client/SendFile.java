package client;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.*;

public class SendFile implements Runnable{
	Client c;
	File file;
	InetAddress target;
	int port;
	byte[] fileParts;
	String ext;
	String name;
	
	public SendFile(Client c, File f, InetAddress target, int port){
		this.c = c;
		file = f;
		this.target = target;
		this.port = port;
		
		String pathString = file.getPath();
		Path path = Paths.get(pathString);
		try {
			fileParts = Files.readAllBytes(path);
		} catch (IOException e) {
			System.out.println("Fuck the File!");
		}
		
		ext = FilenameUtils.getExtension(pathString);
		name = FilenameUtils.getName(pathString);
		
	}

	@Override
	public void run() {
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
	
}
