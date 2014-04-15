package tests;

import GUI.ChatWindow;

public class Echo extends ChatWindow{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static String myName = "Echoer";
	
	public Echo(){
		super(myName);
		System.out.println("Echo made");
	}
	
	public void incoming(String txt){
		System.out.println("Echo!");
		super.addText(txt);
	}
}