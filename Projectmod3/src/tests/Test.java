package tests;

import java.util.ArrayList;

import GUI.ChatWindow;

/**
 * 
 * @author Tim
 *
 */
public class Test extends Thread {
	ChatWindow c;

	String lastReceived = null;

	ArrayList<String> received = new ArrayList<String>();

	public Test() {
		c = new ChatWindow("Tester", this);
	}

	public void run() {
		basicConnection();
		//seqNrs();
	}

	// all tests here
	private void basicConnection() {
		String sentMsg = "Awesome test message";
		c.addText(c.generateLine(sentMsg));
		int count = 0;
		while (lastReceived == null && count <= 10) {
			try {
				sleep(500);
			} catch (InterruptedException e) {
				System.out.println("HIJ WIL NIET TUKKEN! :O");
			}
			count++;
		}
		System.out.println(lastReceived);
		String result;
		if (count > 10)
			result = "NOPE, TIMEOUT!";
		else
			result = lastReceived;
		printTestResult("Test for the basic message sending", sentMsg, result);

		lastReceived = null;
		received = new ArrayList<String>();
	}
	
	private void seqNrs(){
		c.client.sendTestPacket("a", 5);
		c.client.sendTestPacket("b", 8);
		c.client.sendTestPacket("c", 6);
		c.client.sendTestPacket("d", 7);
		c.client.sendTestPacket("e", 9);
		int count = 0;
		String result = "";
		while(received.size() < 5 && count <= 10){
			count ++;
		}
		if(count > 10)
			result = "NOPE, TIMEOUT!";
		else{
			for(int i = 0; i < received.size(); i++){
				result = result + received.get(i);
			}
		}
			
		printTestResult("Sequence numbers out of sync and starts at 5 ==> 5,8,6,7,9", "a, b, c, d, e", result);
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
