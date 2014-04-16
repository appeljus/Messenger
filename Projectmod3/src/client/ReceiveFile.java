package client;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import GUI.ChatWindow;

public class ReceiveFile extends Thread {
	Client client;
	byte[] file = new byte[0];
	String sender = "DUDE";
	String ext;

	public ReceiveFile(Client client) {
		this.client = client;
	}

	public void run() {
		createFile(ext);
	}

	public void receiveFile(byte[] e, boolean EOF, String ext, String sender) {
		addToFile(e);
		if (EOF) {
			this.ext = ext;
			this.sender = sender;
			Thread t = new Thread(this);
			t.start();
		}
	}

	private void addToFile(byte[] e) {
		byte[] temp = new byte[e.length + file.length];
		System.arraycopy(file, 0, temp, 0, file.length);
		System.arraycopy(e, 0, temp, file.length, e.length);
		file = temp;
	}

	public void createFile(String ext) {
		FileOutputStream fOutS;

		final JFileChooser fc = new JFileChooser();
		int option = 1;
		Object[] options = { "Yes", "No" };
		option = JOptionPane.showOptionDialog(null, "Do you want to accept a " + ext + "-file from " + sender + "?", "Incoming file", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		if (option == 0) {
			fc.setDialogType(JFileChooser.SAVE_DIALOG);
			int returnVal = fc.showSaveDialog(client.getChatWindow());
			String path = "C:";
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				path = fc.getSelectedFile().getPath();
				client.getChatWindow().incoming(
						"Saved file at: " + path + "." + ext);
				System.out.println(new String(file));
				try {
					fOutS = new FileOutputStream(path + "." + ext);
					fOutS.write(file);
					fOutS.close();
					Desktop.getDesktop().open(new File(path + "." + ext));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		file = new byte[0];
		return;
	}
}
