package client;

public class Encryption2 {
	
	int shift;
	
	public Encryption2(){
		shift = 3;
	}
	
	public byte[] encrypt(byte[] data){
		for (int i = 0; i < data.length; i++){
			data[i] = (byte)(i - shift);
		}
		return data;
	}
	
	public byte[] decrypt(byte[] data){
		for (int i = 0; i < data.length; i++){
			data[i] = (byte)(i + shift);
		}
		return data;
	}

	public static void main(String[] args){
		Encryption2 encryption = new Encryption2();
		byte[] hoi = "hallo".getBytes();
		hoi = encryption.encrypt(hoi);
		System.out.println(new String(hoi));
		hoi = encryption.decrypt(hoi);
		System.out.println(new String(hoi));
	}
}
