package discordbot;

import window.Window;

public class RainbotShutdown extends Thread{
	private Rainbot rainbot;
	private Window window;
	
	public RainbotShutdown(Rainbot rainbot){
		this.rainbot = rainbot;
	}
	
	public void run() {
		rainbot.disconnect();
		rainbot.createListener.dailyLogger.saveMessageList();
    		try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	} 
}