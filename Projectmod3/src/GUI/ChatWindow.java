package GUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import client.Client;

public class ChatWindow extends JFrame implements KeyListener, ActionListener,
		MouseListener {

	/**
	 * ############################
	 */
	private static final long serialVersionUID = 1L;

	Client client;
	
	Boolean wantPersTabs = true;

	String myName;

	Container cont;
	JPanel mainFrame;
	JPanel menuBar;
	JPanel sendBar;

	private Dimension windowSize = new Dimension(800, 600);

	JTextField typeArea = new JTextField();
	DefaultListModel<String> list = new DefaultListModel<String>();
	JList<String> textArea = new JList<String>(list);
	JScrollPane msgScroller;

	JButton invite;
	JButton opt;
	JButton exit;

	JButton send;

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

	public ChatWindow(String name) {
		super("SolarMessenger");
		client = new Client(this);
		myName = name;
		init();
	}

	private void init() {
		cont = getContentPane();
		setSize(windowSize);
		setBackground(Color.BLACK);

		mainFrame = new JPanel();
		mainFrame.setBackground(Color.DARK_GRAY);
		mainFrame.setLayout(new BorderLayout());

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

		menuBar = new JPanel();
		menuBar.setLayout(new GridBagLayout());
		menuBar.setBackground(Color.DARK_GRAY);
		menuBar.setPreferredSize(menuBarDim);
		// Menu buttons

		title = new JLabel("SolarMessenger");
		title.setForeground(Color.WHITE);
		// title.addMouseListener(this);
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

		pt0 = new JLabel(myName);
		pt0.setForeground(Color.WHITE);
		// pt0.addMouseListener(this);
		pt0.setPreferredSize(ptDim);
		pt0.setMaximumSize(ptDim);
		pt0.setMinimumSize(ptDim);
		c.gridy = 4;
		menuBar.add(pt0, c);

		pt1 = new JLabel("None");
		pt1.setForeground(Color.WHITE);
		pt1.addMouseListener(this);
		pt1.setPreferredSize(ptDim);
		pt1.setMaximumSize(ptDim);
		pt1.setMinimumSize(ptDim);
		c.gridy = 5;
		menuBar.add(pt1, c);

		pt2 = new JLabel("None");
		pt2.setForeground(Color.WHITE);
		pt2.addMouseListener(this);
		pt2.setPreferredSize(ptDim);
		pt2.setMaximumSize(ptDim);
		pt2.setMinimumSize(ptDim);
		c.gridy = 6;
		menuBar.add(pt2, c);

		pt3 = new JLabel("None");
		pt3.setForeground(Color.WHITE);
		pt3.addMouseListener(this);
		pt3.setPreferredSize(ptDim);
		pt3.setMaximumSize(ptDim);
		pt3.setMinimumSize(ptDim);
		c.gridy = 7;
		menuBar.add(pt3, c);

		// pushes other buttons and labels up
		pusher = new JLabel("");
		pusher.setPreferredSize(ptDim);
		pusher.setMaximumSize(ptDim);
		pusher.setMinimumSize(ptDim);
		c.fill = GridBagConstraints.BOTH;
		c.gridy = 8;
		c.weighty = 5000;
		c.anchor = GridBagConstraints.NORTH;
		menuBar.add(pusher, c);

		// ////////////////////////////

		mainFrame.add(sendBar, BorderLayout.SOUTH);
		mainFrame.add(msgScroller, BorderLayout.CENTER);
		mainFrame.add(menuBar, BorderLayout.WEST);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		cont.add(mainFrame);
		setVisible(true);
	}

	private void addText(String txt) {
		String[] words = txt.split(" ");

		if (words.length >= 3 && words[1].equals("/w")) {
			typeArea.setText(words[1] + " " + words[2] + " ");
			// whisper
			String target = words[2];
			words[1] = "";
			words[2] = "";
			String data = "";
			if (words.length == 3) {
				data = " ";
			} else {
				for (int i = 3; i < words.length; i++) {
					data = data + " " + words[i];
				}
			}
			data = "To " + target + ": " + data;
			list.addElement(data + "\n");
			textArea.ensureIndexIsVisible(list.getSize() - 1);
		} else {
			client.sendPacket(txt);
			typeArea.setText("");
			list.addElement(txt + "\n");
			textArea.ensureIndexIsVisible(list.getSize() - 1);
		}

	}
	
	public void incoming(String txt){
		typeArea.setText("");
		list.addElement(txt + "\n");
		textArea.ensureIndexIsVisible(list.getSize() - 1);
	}

	private String generateLine(String text) {
		return myName + ": " + text;
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode() == 10) {
			String txt = typeArea.getText();
			txt = generateLine(txt);
			this.addText(txt);
			// also.. send the text
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
		if (arg0.getSource() == this.exit) {
			System.exit(0);
		} else if (arg0.getSource() == this.invite) {
			this.addText("Nope");
		} else if (arg0.getSource() == this.opt) {
			// display options window
		} else if (arg0.getSource() == this.send) {
			String txt = typeArea.getText();
			this.addText(generateLine(txt));
			// also.. send the text
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		if (arg0.getSource() == this.pt1 || arg0.getSource() == this.pt2
				|| arg0.getSource() == this.pt3) {
			JLabel x = (JLabel) arg0.getSource();
			if(this.wantPersTabs){
				new PersonalChat(client, myName, x.getText());
			}else{
				typeArea.setText("/w " + x.getText() + " ");
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
}
