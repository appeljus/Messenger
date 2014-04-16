package client;

import java.util.TimerTask;

/**
 * De klass die elke seconde een <code>Broadcast</code> stuurt.
 * @author Kevin
 * @version 1.0.0
 */
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
		if(secCount == 3){
			client.checkConnections();
			secCount = 0;
		}
		client.sendPacket("[BROADCAST]: " + client.getClientName()  + " DUMMY_WORD");
	}
}
