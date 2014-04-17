package client;

public class Encryption {
	
	int shift;
	
	public Encryption(){
		shift = shift + ((int)"D".getBytes()[0]) & 0xFF;
		shift = shift + ((int)"o".getBytes()[0]) & 0xFF;
		shift = shift + ((int)"i".getBytes()[0]) & 0xFF;
		shift = shift + ((int)"f".getBytes()[0]) & 0xFF;
	}
	
	public byte[] encryptData(byte[] data){
		for (int i = 0; i < data.length; i++){
			data[i] = (byte)(data[i] - shift);
		}
		return data;
	}
	
	public byte[] decryptData(byte[] data){
		for (int i = 0; i < data.length; i++){
			data[i] = (byte)(data[i] + shift);
		}
		return data;
	}
	
	public int getShift(){
		return shift;
	}

	public static void main(String[] args){
		Encryption encryption = new Encryption();
		byte[] hoi = "Hallo dit is een Doif-encryptie.".getBytes();
		hoi = encryption.encryptData(hoi);
		System.out.println(new String(hoi));
		hoi = encryption.decryptData(hoi);
		System.out.println(new String(hoi));
	}
}
