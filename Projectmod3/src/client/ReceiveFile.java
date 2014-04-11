package client;

import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFileChooser;

import GUI.ChatWindow;

public class ReceiveFile {
	Client client;
	byte[] file = new byte[0];
	String fileTitle;
	
	public ReceiveFile(Client client) {
		this.client = client;
	}
	
	public void receiveFile(byte[] e, boolean EOF, String ext) {
		addToFile(e);
		if(EOF) createFile(ext);
	}
	
	private void addToFile(byte[] e){
		byte[] temp = new byte[e.length + file.length];
		System.arraycopy(file, 0, temp, 0, file.length);
		System.arraycopy(e, 0, temp, file.length, e.length);
	}
	
	private void createFile(String ext){
		FileOutputStream fOutS;
		JFileChooser fc = new JFileChooser();
		fc.setDialogType(JFileChooser.SAVE_DIALOG);
		int returnVal = fc.showOpenDialog(client.chatwindow);
		String path = "C:";
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			path = fc.getSelectedFile().getPath();
			client.chatwindow.incoming("Saved file at: " + path + "." + ext);
			try {
				fOutS = new FileOutputStream(path + "." + ext);
				fOutS.write(file);
				fOutS.close();
			} catch (IOException e) { e.printStackTrace(); } 
		}
	}
}





