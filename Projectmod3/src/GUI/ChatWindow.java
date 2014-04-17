package GUI;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.sound.sampled.Clip;
import javax.swing.*;

import tests.Test;

import client.Client;

/**
 * Deze klasse bevat de main method en is de hoofdklasse van de applicatie.
 * Hier word het hoofdwindow gemaakt en weergeven en word een instantie van Client
 * aangemaakt en zo nodig een instantie van PersoalChat.
 * @Author Tim
 */
public class ChatWindow extends JFrame implements KeyListener, ActionListener,
		MouseListener {
	
	private static final long serialVersionUID = 1L;

	public Client client;

	Test test;

	Boolean wantPersTabs = false;
	ArrayList<PersonalChat> pChats = new ArrayList<PersonalChat>();

	String myName;

	Container cont;
	JPanel mainFrame;
	JPanel menuBar;
	JPanel sendBar;
	JCheckBox checkTabs;

	Dimension windowSize = new Dimension(800, 600);

	JTextField typeArea = new JTextField();
	DefaultListModel<String> list = new DefaultListModel<String>();
	JList<String> textArea = new JList<String>(list);
	JScrollPane msgScroller;

	JButton exit;
	JButton send;
	JButton sendFile;

	JLabel title;
	JLabel pusher;

	BufferedImage iconBuff;
	BufferedImage exitBuff;
	BufferedImage fileBuff;
	BufferedImage sendBuff;

	ImageIcon icon;
	ImageIcon exitB;
	ImageIcon fileB;
	ImageIcon sendB;

	public ArrayList<String> pNameList = new ArrayList<String>();
	DefaultListModel<String> pList = new DefaultListModel<String>();
	JList<String> pArea = new JList<String>(pList);
	JScrollPane pListScroller = new JScrollPane(pArea);

	Dimension buttonDim = new Dimension(96, 64);
	Dimension ptDim = new Dimension(96, 16);
	Dimension menuBarDim = new Dimension(96, 576);

	Clip clip;
	
	/**
	 * Constructor
	 * @param name De naam die je invult in het login window.
	 * @param t Als het een test is, kan dit een Test klasse zijn, anders moet het null zijn.
	 */
	public ChatWindow(String name, Test t) {
		super("Pigeon");
		client = new Client(this, name);
		myName = name;
		test = t;
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

		sendBar = new JPanel();
		sendBar.setBackground(Color.DARK_GRAY);

		typeArea.setEditable(true);
		typeArea.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 3));
		typeArea.addKeyListener(this);

		textArea.setCellRenderer(new MyListRenderer());

		sendBar.setLayout(new GridBagLayout());

		msgScroller = new JScrollPane(textArea);

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		sendBar.add(typeArea, c);
		c.fill = GridBagConstraints.HORIZONTAL;

		c.gridx = 1;

		send = new JButton();
		send.setPreferredSize(new Dimension(96, 32));
		send.addActionListener(this);
		sendBar.add(send);

		menuBar = new JPanel();
		menuBar.setLayout(new GridBagLayout());
		menuBar.setBackground(Color.DARK_GRAY);
		menuBar.setPreferredSize(menuBarDim);
		// Menu buttons

		title = new JLabel("");
		try {
			iconBuff = ImageIO.read(new FileInputStream("res/PigeonTitle.png"));
			exitBuff = ImageIO.read(new FileInputStream("res/Exit.png"));
			fileBuff = ImageIO.read(new FileInputStream("res/File.png"));
			sendBuff = ImageIO.read(new FileInputStream("res/Send.png"));
			icon = new ImageIcon(iconBuff);
			exitB = new ImageIcon(exitBuff);
			fileB = new ImageIcon(fileBuff);
			sendB = new ImageIcon(sendBuff);
		} catch (IOException e) {
			System.out.println("Fuck the images!");
		}

		title.setForeground(Color.WHITE);
		title.setPreferredSize(buttonDim);
		title.setMaximumSize(buttonDim);
		title.setMinimumSize(buttonDim);
		c.weighty = 1;
		menuBar.add(title, c);

		pArea.setBackground(Color.DARK_GRAY);
		pArea.setForeground(Color.WHITE);
		pArea.addMouseListener(this);
		updateNames(myName);
		c.gridy++;
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 5000;
		c.anchor = GridBagConstraints.NORTH;
		menuBar.add(pListScroller, c);

		checkTabs = new JCheckBox(
				"<html>Seperate<br>window for<br>personal<br>chat</html>");
		checkTabs.setSelected(true);
		c.gridy++;
		c.weighty = -5000;
		c.anchor = GridBagConstraints.SOUTH;
		menuBar.add(checkTabs, c);

		sendFile = new JButton("<html>Send<br>File</html>");
		sendFile.addActionListener(this);
		sendFile.setPreferredSize(buttonDim);
		sendFile.setMinimumSize(buttonDim);
		sendFile.setMaximumSize(buttonDim);
		c.gridy++;
		menuBar.add(sendFile, c);

		exit = new JButton();
		exit.addActionListener(this);
		exit.setPreferredSize(buttonDim);
		exit.setMinimumSize(buttonDim);
		exit.setMaximumSize(buttonDim);
		c.gridy++;
		menuBar.add(exit, c);

		// ////////////////////////////
		// set icons
		title.setIcon(icon);
		send.setIcon(sendB);
		send.setIconTextGap(-12);
		exit.setIcon(exitB);
		exit.setIconTextGap(-12);
		sendFile.setIcon(fileB);

		mainFrame.add(sendBar, BorderLayout.SOUTH);
		mainFrame.add(msgScroller, BorderLayout.CENTER);
		mainFrame.add(menuBar, BorderLayout.WEST);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		cont.add(mainFrame);
		setVisible(true);
		typeArea.requestFocusInWindow();
	}
	/**
	 * Deze methode word aangeroepen als er een nieuw stuk text is getyped in de typeArea.
	 * Hier word ook gekeken of het een whisper is en maakt dan zo nodig een nieuwe instantie van PersonalChat.
	 * Tot slot word dan de nieuwe regel in het goede window geprint.
	 * @param txt De nieuwe text die ingevoerd word.
	 */
	public void addText(String txt) {
		String[] words = txt.split(" ");

		if (words.length >= 3 && words[1].equals("/w")) {
			typeArea.setText(words[1] + " " + words[2] + " ");
			String target = words[2];
			if (!pNameList.contains(target)) {
				incoming(target + " isn't connected!");
				typeArea.setText("");
			} else {
				String data = "";
				if (words.length == 3) {
					data = " ";
				} else {
					for (int i = 3; i < words.length; i++) {
						data = data + " " + words[i];
					}
				}
				String toData = "To " + target + ": " + data;
				list.addElement(toData);
				textArea.ensureIndexIsVisible(list.getSize() - 1);
				String fromData = myName + " " + data;
				client.sendPrivate(target, fromData);
			}
		} else {
            client.testRTT();
			client.sendPacket(txt);
			typeArea.setText("");
		}
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
		if (test != null)
			test.incoming(txt);
	}
	
	/**
	 * Deze doet in principe het zelfde als <code>incoming()</code>, maar dan voor private messages.
	 * Hier is dus wel een sender nodig.
	 * @param sender Naam van persoon van wie het berich komt.
	 * @param txt De nieuwe regel die binnen is gekomen.
	 */
	public void privateIncoming(String sender, String txt) {
		if (checkTabs.isSelected()) {
			if (doIHaveWindow(sender) == -1) {
				pChats.add(new PersonalChat(this, client, myName, sender));
			}
			pChats.get(doIHaveWindow(sender)).incoming(sender + ": " + txt);

		} else {
			txt = txt.replace("8)", "😎");
			txt = txt.replace(":)", "😉");
			list.addElement("From " + sender + ": " + txt);
			textArea.ensureIndexIsVisible(list.getSize() - 1);
		}
	}

	/**
	 * Deze methode kijkt of er al een instantie van <code>PersonalChat</code> in de lijst staat.
	 * Dus of er al een apart scherm is voor deze persoon.
	 * @param target Naam van de persoon waarvan je wil testen of er voor hem al een apart window is.
	 * @return true als er al een window voor de target persoon is, false als dit niet het geval is.
	 */
	public int doIHaveWindow(String target) {
		int ret = -1;
		for (int i = 0; i < pChats.size(); i++) {
			if (pChats.get(i).getTarget().equals(target)) {
				ret = i;
				break;
			}
		}
		return ret;
	}

	/**
	 * Deze methode maakt van de text die getyped is in typeArea een duidelijke regel die geprint kan worden.
	 * @param text De text die getyped is.
	 * @return De regel die geprint moet worden.
	 */
	public String generateLine(String text) {
		return myName + ": " + text;
	}
	
	/**
	 * Deze methode word aangeroepen als er een persoon weg is gegaan.
	 * Zijn naam word dan uit de lijst met personen die mee doen gehaald en als er een apart window voor hem was word die gesloten.
	 * @param name Naam van de gene die weg is gegaan.
	 */
	public void disconnect(String name) {
		if (pNameList.contains(name)) {
			int index = pNameList.indexOf(name);
			pList.remove(index);
			pNameList.remove(index);
		}
		int index = doIHaveWindow(name);
		if (index >= 0) {
			pChats.get(index).dispose();
			pChats.remove(index);
		}
	}
	/**
	 * De lijst van mensen die mee doen geupdate. Word aangeroepen als er een bericht van iemand binnen komt.
	 * Voegt een naam toe als die er nog niet in stond en anders laat hij de naam gewoon staan.
	 * @param name Naam van de persoon die er nog blijkt te zijn.
	 */
	public void updateNames(String name) {
		if (!pNameList.contains(name)) {
			pList.addElement(name);
			pNameList.add(name);
			list.addElement(name + " joined Pigeon!");
			textArea.ensureIndexIsVisible(list.getSize() - 1);
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		String[] words = typeArea.getText().split(" ");
		if (arg0.getKeyCode() == 10 && !typeArea.getText().equals("")
				&& !(words.length == 2 && words[0].equals("/w"))
				&& typeArea.getText().length() <= 1001) {
			String txt = typeArea.getText();
			txt = generateLine(txt);
			this.addText(txt);
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		typeArea.requestFocus();
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == this.exit) {
			System.exit(0);
		} else if (arg0.getSource() == this.send) {
			String[] words = typeArea.getText().split(" ");
			if(words[0].equals("/count")){
				int i = 0;
				try{
				i = Integer.parseInt(words[1]);
				} catch(Exception e){
					i = 64;
				}
				for(int j = 0; j < i; j++){
					this.addText(("Counter - ") + j);
				}
				
			}
			else if (!typeArea.getText().equals("")
					&& !(words.length == 2 && words[0].equals("/w"))
					&& typeArea.getText().length() <= 1001) {
				String txt = typeArea.getText();
				this.addText(generateLine(txt));
				typeArea.requestFocusInWindow();
			}

		} else if (arg0.getSource() == sendFile) {
			JFileChooser fc = new JFileChooser();
			int returnVal = fc.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				client.sendFile(fc.getSelectedFile());
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		JList list = (JList) arg0.getSource();
		Rectangle r = list.getCellBounds(0, list.getLastVisibleIndex());
		if (arg0.getClickCount() == 2 && r != null
				&& r.contains(arg0.getPoint())) {
			String person = pNameList.get(list.getSelectedIndex());
			if (checkTabs.isSelected()) {
				this.pChats.add(new PersonalChat(this, client, myName, person));
			} else {
				typeArea.setText("/w " + person + " ");
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {

	}

	@Override
	public void mouseExited(MouseEvent arg0) {

	}

	@Override
	public void mousePressed(MouseEvent arg0) {

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {

	}
/**
 * Main method. Maakt eerst een JOptionPane om je naam in te vullen en als dat een geldige naam is maakt hij een nieuw ChatWindow aan.
 * @param args Niets..
 */
	public static void main(String[] args) {
		String name = JOptionPane.showInputDialog("What is your name?");
		if (name != null)
			new ChatWindow(name, null);
	}

	/**
	 * Een klasse om de lijst voor textArea met kleuren te weergeven.
	 * @author Groep 8
	 *
	 */
	private class MyListRenderer extends DefaultListCellRenderer {
		
		private static final long serialVersionUID = 1L;

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);

			setForeground(Color.BLACK);
			if (index % 2 == 0) {
				setBackground(Color.LIGHT_GRAY);
			}

			String txt = (String) value;
			if (txt.startsWith("From")) {
				setForeground(new Color(0, 0, 255));
			} else if (txt.startsWith("To")) {
				setForeground(new Color(255, 0, 0));
			}

			if (isSelected) {
				setForeground(Color.WHITE);
				setBackground(Color.DARK_GRAY);
			}

			return (this);
		}
	}
}