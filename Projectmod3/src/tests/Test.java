package tests;

import GUI.ChatWindow;

public class Test extends Thread{
	ChatWindow c;
	
	public Test(){
		c = new ChatWindow("Tester", this);
	}
	
	public void run(){
		
	}
	
	// all tests here
	private void basicConnection(){
		String sentMsg = "Awesome test message";
		c.addText(c.generateLine(sentMsg));
		String receivedMsg = null;
		while (receivedMsg != null){
			
		}
	}
	
	// utills here
	
	public void incoming(String txt){
		
	}
	
	private void printTestResult(String description, Object expected, Object result){
		System.out.println("--- New test ---");
		System.out.println(description);
		System.out.println("Expected: " + expected.toString());
		System.out.println("Result: " + result.toString());
		System.out.print("Test result: ");
		if(expected.equals(result))
			System.out.println("Positive");
		else
			System.out.println("Negative");
		System.out.println("--- End of test ---");
	}
	
	public static void main(String[] arg0){
		Thread thread = new Thread(new Test());
		thread.start();
	}
}
