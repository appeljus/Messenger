package GUI;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import client.Client;

public class PersonalChat extends JFrame implements KeyListener, ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ChatWindow chatWindow;
	Client client;
	String hisName;
	String myName;

	Container cont;
	JPanel mainFrame;
	JPanel sendBar;

	JTextField typeArea = new JTextField();
	DefaultListModel<String> list = new DefaultListModel<String>();
	JList<String> textArea = new JList<String>(list);
	JScrollPane msgScroller;

	JButton send;

	Dimension windowSize = new Dimension(400, 300);

	public PersonalChat(ChatWindow chatWindow, Client c, String myName1,
			String hisName2) {
		super("Chatting with " + hisName2);
		client = c;
		myName = myName1;
		hisName = hisName2;
		this.chatWindow = chatWindow;
		init();
	}

	private void init() {
		cont = getContentPane();
		setSize(windowSize);
		setBackground(Color.BLACK);

		mainFrame = new JPanel();
		mainFrame.setBackground(Color.DARK_GRAY);
		mainFrame.setLayout(new BorderLayout());
		mainFrame.addKeyListener(this);

		sendBar = new JPanel();
		sendBar.setBackground(Color.DARK_GRAY);

		typeArea.setEditable(true);

		typeArea.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 3));

		typeArea.addKeyListener(this);
		sendBar.setLayout(new GridBagLayout());

		msgScroller = new JScrollPane(textArea);

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.gridx = 0;
		c.gridy = 0;

		sendBar.add(typeArea, c);

		c.gridx = 1;

		send = new JButton("Send");
		send.setPreferredSize(new Dimension(96, 32));
		send.addActionListener(this);
		sendBar.add(send);

		// ////////////////////////////

		mainFrame.add(sendBar, BorderLayout.SOUTH);
		mainFrame.add(msgScroller, BorderLayout.CENTER);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		cont.add(mainFrame);
		setVisible(true);
	}

	private void addText(String txt) {
		list.addElement(myName + ": " + txt);
		typeArea.setText("");
		txt = (myName + " " + txt);
		client.sendPrivate(hisName, txt);
		this.textArea.ensureIndexIsVisible(list.getSize() - 1);
	}
	
	public void incoming(String txt) {
		txt = txt.replace("8)", "😎");
		txt = txt.replace(":)", "😉");
		list.addElement(txt);
		textArea.ensureIndexIsVisible(list.getSize() - 1);
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		typeArea.requestFocus(true);
		if (arg0.getKeyCode() == 10 && !typeArea.getText().equals("")) {
			String txt = typeArea.getText();
			this.addText(txt);
			// also.. send the text
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == this.send) {
			String txt = typeArea.getText();
			this.addText(txt);
			// also.. send the text
		}
	}
	
	public String getTarget(){
		return hisName;
	}
}