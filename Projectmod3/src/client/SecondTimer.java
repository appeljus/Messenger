package client;

import java.util.TimerTask;

public class SecondTimer extends TimerTask {
	Client client;
	
	public SecondTimer(Client client){
		this.client = client;
	}

	@Override
	public void run() {
		client.sendPacket("[BROADCAST]: " + client.getClientName());
	}
}
