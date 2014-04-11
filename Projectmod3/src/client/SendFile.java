package client;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SendFile implements Runnable{
	Client c;
	File file;
	InetAddress target;
	int port;
	byte[] fileParts;
	String ext;
	
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
		
		String ext = FilenameUtils.getExtension(path);
	}

	@Override
	public void run() {
		
	}
	
}
