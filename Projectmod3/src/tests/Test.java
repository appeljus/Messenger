package tests;

import GUI.ChatWindow;

public class Test {
	ChatWindow c;
	
	public Test(){
		c = new ChatWindow("Tester");
		runTests();
	}
	
	private void runTests(){
		printTestResult("test van de test xD", "Hoi", "hoi");
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
		new Test();
	}
}
