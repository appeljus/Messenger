package client;

public class Encryption2 {
	
	int[] shift = new int[4];
	
	public Encryption2(){
		shift[0] = ((int)"D".getBytes()[0]) & 0xFF;
		shift[1] = ((int)"o".getBytes()[0]) & 0xFF;
		shift[2] = ((int)"i".getBytes()[0]) & 0xFF;
		shift[3] = ((int)"f".getBytes()[0]) & 0xFF;
	}
	
	public byte[] encrypt(byte[] data){
		for (int i = 0; i < data.length; i++){
			if(i % 4 == 0) data[i] = (byte)(data[i] - shift[3]);
			if(i % 3 == 0) data[i] = (byte)(data[i] - shift[2]);
			if(i % 2 == 0) data[i] = (byte)(data[i] - shift[1]);
			else data[i] = (byte)(data[i] - shift[0]);
		}
		return data;
	}
	
	public byte[] decrypt(byte[] data){
		for (int i = 0; i < data.length; i++){
			if(i % 4 == 0) data[i] = (byte)(data[i] + shift[3]);
			if(i % 3 == 0) data[i] = (byte)(data[i] + shift[2]);
			if(i % 2 == 0) data[i] = (byte)(data[i] + shift[1]);
			else data[i] = (byte)(data[i] + shift[0]);
		}
		return data;
	}
	
	public int getShift(){
		return shift[0];
	}

	public static void main(String[] args){
		Encryption2 encryption = new Encryption2();
		byte[] hoi = "Hallo dit is een Doif-encryptie.".getBytes();
		hoi = encryption.encrypt(hoi);
		System.out.println(new String(hoi));
		hoi = encryption.decrypt(hoi);
		System.out.println(new String(hoi));
	}
}
