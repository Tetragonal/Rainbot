package discordbot;

public class RainbotShutdown extends Thread{
	private Rainbot rainbot;
	
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