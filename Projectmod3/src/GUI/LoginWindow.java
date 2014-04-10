package GUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

public class LoginWindow extends JFrame implements ActionListener, KeyListener{
	
	Container c;
	JButton loginButton;
	JTextField nameField;
	
	Dimension windowSize = new Dimension(300, 64);

	public LoginWindow(){
		super("Log In");
		init();
	}
	
	public void init(){
		c = getContentPane();
		setSize(windowSize);
		setBackground(Color.BLACK);
		
		c.setLayout(new BorderLayout());
		
		loginButton = new JButton("Connect");
		loginButton.addActionListener(this);
		
		nameField = new JTextField();
		nameField.addKeyListener(this);
		nameField.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 3));
		
		c.add(nameField,BorderLayout.CENTER);
		c.add(loginButton,BorderLayout.EAST);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new LoginWindow();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String txt = nameField.getText();
		if(txt.length() != 0 && !txt.contains(" ") && !txt.equals("/w")){
			System.out.println(txt);
			this.dispose();
			new ChatWindow(txt);
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode() == 10) {
			String txt = nameField.getText();
			if(txt.length() != 0 && !txt.contains(" ") && !txt.equals("/w")){
				this.dispose();
				new ChatWindow(txt);
			}
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

}