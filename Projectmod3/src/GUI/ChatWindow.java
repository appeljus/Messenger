package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.InputStream;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class ChatWindow extends JFrame implements KeyListener {
	
	Container c;
	JPanel mainFrame;
	
	private int scrWidth = 800;
	private int scrHeigth = 600;
	private Dimension windowSize = new Dimension(scrWidth, scrHeigth);
	
	JTextArea typeArea = new JTextArea();
	JTextArea textArea = new JTextArea();

	public ChatWindow (){
		super("SolarMessenger");
		init();
	}
	
	private void init(){
		c = getContentPane();
		setSize(windowSize);
		setBackground(Color.BLACK);
		
		mainFrame = new JPanel();
		mainFrame.setBackground(Color.DARK_GRAY);
		mainFrame.setLayout(new BorderLayout());		
		
		typeArea.setEditable(true);
		textArea.setEditable(false);
		
		typeArea.addKeyListener(this);
		
		mainFrame.add(typeArea, BorderLayout.SOUTH);
		mainFrame.add(textArea, BorderLayout.CENTER);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		c.add(mainFrame);
		setVisible(true);	
	}
	
	public static void main(String[] arg0){
		ChatWindow x = new ChatWindow();
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if(arg0.getKeyCode() == 10){
			String txt = typeArea.getText();
			
		}
		char c = arg0.getKeyChar();
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// i dont care
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}

