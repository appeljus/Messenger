package tests;

import java.util.ArrayList;

import GUI.ChatWindow;

public class Test extends Thread {
	ChatWindow c;

	String lastReceived = null;

	ArrayList<String> received = new ArrayList<String>();

	public Test() {
		c = new ChatWindow("Tester", this);
	}

	public void run() {
		basicConnection();
	}

	// all tests here
	private void basicConnection() {
		String sentMsg = "Awesome test message";
		c.addText(c.generateLine(sentMsg));
		int count = 0;
		while (lastReceived == null) {
			try {
				sleep(200);
			} catch (InterruptedException e) {
				System.out.println("HIJ WIL NIET TUKKEN! :O");
			}
			count++;
		}
		String result;
		if (count > 10)
			result = "NOPE, TIMEOUT!";
		else
			result = lastReceived;
		printTestResult("Test for the basic message sending", sentMsg, result);

		lastReceived = null;
	}
	
	

	// utills here

	public void incoming(String txt) {
		if (lastReceived != null)
			lastReceived = txt;
		received.add(txt);
	}

	private void printTestResult(String description, Object expected,
			Object result) {
		System.out.println("--- New test ---");
		System.out.println(description);
		System.out.println("Expected: " + expected.toString());
		System.out.println("Result: " + result.toString());
		System.out.print("Test result: ");
		if (expected.equals(result))
			System.out.println("Positive");
		else
			System.out.println("Negative");
		System.out.println("--- End of test ---");
	}

	public static void main(String[] arg0) {
		Thread thread = new Thread(new Test());
		thread.start();
	}
}
