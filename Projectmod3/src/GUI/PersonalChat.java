package GUI;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import client.Client;

/**
 * Deze klasse is gebaseert op ChatWindow. Deze klasse is om een apart window te maken om te praten met 1 persoon.
 * @author Tim
 *
 */
public class PersonalChat extends JFrame implements KeyListener, ActionListener {
	
	private static final long serialVersionUID = 1L;
	ChatWindow chatWindow;
	Client client;
	String hisName;
	String myName;

	BufferedImage sendBuff;
	ImageIcon sendB;

	Container cont;
	JPanel mainFrame;
	JPanel sendBar;

	JTextField typeArea = new JTextField();
	DefaultListModel<String> list = new DefaultListModel<String>();
	JList<String> textArea = new JList<String>(list);
	JScrollPane msgScroller;

	JButton send;

	Dimension windowSize = new Dimension(400, 300);

	/**
	 * Constructor.
	 * @param chatWindow ChatWindow waaraan deze personal chat gekoppeld is.
	 * @param c De bijbehorende client van chatWindow.
	 * @param myName1 De naam van persoon 
	 * @param hisName2
	 */
	public PersonalChat(ChatWindow chatWindow, Client c, String myName1,
			String hisName2) {
		super("Chatting with " + hisName2);
		client = c;
		myName = myName1;
		hisName = hisName2;
		this.chatWindow = chatWindow;
		init();
	}
	/**
	 * Deze methode is eigenlijk een extensie van de constructor.
	 * Hier worden de JComponents aan gemaakt en de Icons etc.
	 * Eigenlijk word hier gewoon het hele window aangemaakt.
	 */
	private void init() {
		cont = getContentPane();
		setSize(windowSize);
		setBackground(Color.BLACK);

		mainFrame = new JPanel();
		mainFrame.setBackground(Color.DARK_GRAY);
		mainFrame.setLayout(new BorderLayout());
		mainFrame.addKeyListener(this);

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int index = chatWindow.doIHaveWindow(hisName);
				if (index >= 0)
					chatWindow.pChats.remove(index);
			}
		});

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

		send = new JButton();
		send.setPreferredSize(new Dimension(96, 32));
		send.addActionListener(this);
		sendBar.add(send);

		// ////////////////////////////

		try {
			sendBuff = ImageIO.read(new FileInputStream("res/Send.png"));
			sendB = new ImageIcon(sendBuff);
		} catch (IOException e) {
			System.out.println("Fuck the images!");
		}
		send.setIcon(sendB);
		send.setIconTextGap(12);

		mainFrame.add(sendBar, BorderLayout.SOUTH);
		mainFrame.add(msgScroller, BorderLayout.CENTER);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		cont.add(mainFrame);
		setVisible(true);

	}
	/**
	 * Deze methode word aangeroepen als er een nieuw stuk text is getyped in de typeArea.
	 * @param txt De nieuwe text die ingevoerd word.
	 */
	private void addText(String txt) {
		list.addElement(myName + ": " + txt);
		typeArea.setText("");
		txt = (myName + " " + txt);
		client.sendPrivate(hisName, txt);
		this.textArea.ensureIndexIsVisible(list.getSize() - 1);
	}
	/**
	 * Deze methode word aangeroepen als er een nieuwe regel geprint moet worden.
	 * @param txt De nieuwe regel die binnen is gekomen.
	 */
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
		}
	}

	public String getTarget() {
		return hisName;
	}
}