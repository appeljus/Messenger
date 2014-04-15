package client;

import java.nio.ByteBuffer;

public class Encryption2 {
	
	int shift;
	
	public Encryption2(){
		shift = shift + (int)"D".getBytes()[0];
		shift = shift + (int)"o".getBytes()[0];
		shift = shift + (int)"i".getBytes()[0];
		shift = shift + (int)"f".getBytes()[0];
		System.out.println(shift + "");
	}
	
	public byte[] encrypt(byte[] data){
		for (int i = 0; i < data.length; i++){
			data[i] = (byte)(data[i] - shift);
		}
		return data;
	}
	
	public byte[] decrypt(byte[] data){
		for (int i = 0; i < data.length; i++){
			data[i] = (byte)(data[i] + shift);
		}
		return data;
	}
	
	public int getShift(){
		return shift;
	}

	public static void main(String[] args){
		Encryption2 encryption = new Encryption2();
		byte[] hoi = "Hallo".getBytes();
		hoi = encryption.encrypt(hoi);
		System.out.println(new String(hoi));
		hoi = encryption.decrypt(hoi);
		System.out.println(new String(hoi));
	}
}
