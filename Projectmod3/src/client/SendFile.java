package client;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FilenameUtils;

/**
 * De klasse om een <code>File</code> te verzenden.
 * @author Kevin, Tim, Kimberly, Martijn
 * @version 1.0.0
 */
public class SendFile implements Runnable {

	Client c;
	File file;
	InetAddress target;
	int port;
	byte[] fileParts;
	String ext;
	String name;

	int currentStartI = 0;

	public SendFile(Client c, File f, InetAddress target, int port) {
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
		System.out.println(ext.toString() + " | " + ext);

	}

	@Override
	public void run() {
		if(ext.length() > 3 || ext.length() < 1){
			c.getChatWindow().incoming("Sorry, we do not support this kind of file.");
			return;
		}
		c.getChatWindow().incoming("Sending file..");
		byte[] data = new byte[995];
		byte[] tagData = new byte[11];
		int j = 0;
		while (currentStartI < fileParts.length) {
			System.out.println("sdsdsdsds");
			int i;
			for (i = 0; i < 995 && currentStartI + i < fileParts.length; i++) {
				data[i] = fileParts[currentStartI + i];
			}
			if (currentStartI < fileParts.length) {
				tagData = "[FILE]".getBytes();
			}
			j = i+1;
			currentStartI += i;
			if(currentStartI < fileParts.length) {
				byte[] data3 = new byte[tagData.length + data.length];
				System.arraycopy(tagData, 0, data3, 0, tagData.length);
				System.arraycopy(data, 0, data3, tagData.length, data.length);
				System.out.println(new String(data3));
				
				c.sendPacket(data3, true);
			}
		}
		if(ext.length() == 3) tagData = ("[EOF][" + ext + "][" + (int)(fileParts.length/995) + "]").getBytes();
		else tagData = ("[EOF][" + ext + "][" + (int)(fileParts.length/995) + "]").getBytes();
		
		System.out.println(tagData.length);
		
		byte[] data3 = new byte[tagData.length + data.length];
		if(data.length > j) data[j] = (byte) 255;
		
		System.arraycopy(tagData, 0, data3, 0, tagData.length);
		System.arraycopy(data, 0, data3, tagData.length, data.length);
		//System.arraycopy(filler, 0, data3, tagData.length + data.length, filler.length);
		
		System.out.println(new String(data3));
		
		c.sendPacket(data3, true);
		c.getChatWindow().incoming("File sent!");
		return;
	}

}
