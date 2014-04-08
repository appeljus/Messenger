package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.InputStream;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

public class ChatWindow extends JFrame implements KeyListener, ActionListener {
	
	Container cont;
	JPanel mainFrame;
	JPanel menuBar;
	
	private Dimension windowSize = Toolkit.getDefaultToolkit().getScreenSize();;
	
	JTextField typeArea = new JTextField();
	JTextArea textArea = new JTextArea();
	
	JButton invite;
	JButton opt;
	JButton exit;
	
	JLabel title;
	JLabel pt0;
	JLabel pt1;
	JLabel pt2;
	JLabel pt3;
	JLabel pusher;
	
	boolean inviting = false;
	
	Dimension buttonDim = new Dimension(96, 64);
	Dimension ptDim = new Dimension(96, 16);
	Dimension menuBarDim = new Dimension(96, 576);
	Dimension rigidDim = new Dimension(96, 32);

	public ChatWindow (){
		super("SolarMessenger");
		init();
	}
	
	private void init(){
		cont = getContentPane();
		setSize(windowSize);
		setBackground(Color.BLACK);
		
		mainFrame = new JPanel();
		mainFrame.setBackground(Color.DARK_GRAY);
		mainFrame.setLayout(new BorderLayout());		
		
		typeArea.setEditable(true);
		textArea.setEditable(false);
		
		typeArea.setBorder(BorderFactory.createLineBorder(Color.black, 3));
		
		typeArea.addKeyListener(this);
		
		menuBar = new JPanel();
		menuBar.setLayout(new GridBagLayout());
		menuBar.setBackground(Color.DARK_GRAY);
		menuBar.setPreferredSize(menuBarDim);
		//Menu buttons		
		
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.gridx = 0;
		c.gridy = 0;
		
		title = new JLabel("SolarMessenger");
		title.setForeground(Color.WHITE);
		//title.addMouseListener(this);
		title.setPreferredSize(buttonDim);
		title.setMaximumSize(buttonDim);
		title.setMinimumSize(buttonDim);
		c.weighty = 1;
		menuBar.add(title, c);
		
		invite = new JButton("Invite");
		invite.addActionListener(this);
		invite.setPreferredSize(buttonDim);
		invite.setMinimumSize(buttonDim);
		invite.setMaximumSize(buttonDim);
		c.gridy = 1;
		menuBar.add(invite, c);
		
		opt = new JButton("Options");
		opt.addActionListener(this);
		opt.setPreferredSize(buttonDim);
		opt.setMinimumSize(buttonDim);
		opt.setMaximumSize(buttonDim);
		c.gridy = 2;
		menuBar.add(opt, c);
		
		exit = new JButton("Exit");
		exit.addActionListener(this);
		exit.setPreferredSize(buttonDim);
		exit.setMinimumSize(buttonDim);
		exit.setMaximumSize(buttonDim);
		c.gridy = 3;
		menuBar.add(exit, c);
		
		pt0 = new JLabel("None");
		pt0.setForeground(Color.WHITE);
		//pt0.addMouseListener(this);
		pt0.setPreferredSize(ptDim);
		pt0.setMaximumSize(ptDim);
		pt0.setMinimumSize(ptDim);
		c.gridy = 4;
		menuBar.add(pt0, c);
		
		pt1 = new JLabel("None");
		pt1.setForeground(Color.WHITE);
		//pt1.addMouseListener(this);
		pt1.setPreferredSize(ptDim);
		pt1.setMaximumSize(ptDim);
		pt1.setMinimumSize(ptDim);
		c.gridy = 5;
		menuBar.add(pt1, c);
		
		pt2 = new JLabel("None");
		pt2.setForeground(Color.WHITE);
		//pt2.addMouseListener(this);
		pt2.setPreferredSize(ptDim);
		pt2.setMaximumSize(ptDim);
		pt2.setMinimumSize(ptDim);
		c.gridy = 6;
		menuBar.add(pt2, c);
		
		pt3 = new JLabel("None");
		pt3.setForeground(Color.WHITE);
		//pt3.addMouseListener(this);
		pt3.setPreferredSize(ptDim);
		pt3.setMaximumSize(ptDim);
		pt3.setMinimumSize(buttonDim);
		c.gridy = 7;
		menuBar.add(pt3, c);
		
		//pushes other buttons and labels up
		pusher = new JLabel("");
		pusher.setPreferredSize(ptDim);
		pusher.setMaximumSize(ptDim);
		pusher.setMinimumSize(ptDim);
		c.fill = GridBagConstraints.BOTH;
		c.gridy = 8;
		c.weighty = 5000;
		c.anchor = GridBagConstraints.NORTH;
		menuBar.add(pusher, c);
		
		//////////////////////////////
		
		mainFrame.add(typeArea, BorderLayout.SOUTH);
		mainFrame.add(textArea, BorderLayout.CENTER);
		mainFrame.add(menuBar, BorderLayout.WEST);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		cont.add(mainFrame);
		setVisible(true);	
	}
	
	public void addText(String txt){
		textArea.append(txt + "\n");
		typeArea.setText("");
	}
	
	public static void main(String[] arg0){
		ChatWindow x = new ChatWindow();
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if(arg0.getKeyCode() == 10){
			String txt = typeArea.getText();
			this.addText(txt);
			//also.. send the text
		}		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// i dont care
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource() == this.exit){
			System.exit(0);
		}
		else if(arg0.getSource() == this.invite){
			this.addText("Type the ip-address of the person you want to invite in your typing area please.");
			inviting = true;
		}
		else if(arg0.getSource() == this.opt){
			//display options window
		}
	}
}

