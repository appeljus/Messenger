package client;

import java.util.TimerTask;

public class SecondTimer extends TimerTask {
	Client client;
	int secCount;
	
	public SecondTimer(Client client){
		this.client = client;
		secCount = 0;
	}

	@Override
	public void run() {
		secCount++;
		if(secCount == 5){
			client.checkConnections();
			secCount = 0;
		}
		client.sendPacket("[BROADCAST]: " + client.getClientName() + " STUFF");
	}
}
