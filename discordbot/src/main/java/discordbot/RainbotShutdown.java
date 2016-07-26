package discordbot;

public class RainbotShutdown extends Thread{
	private Rainbot rainbot;
	private Window window;
	
	public RainbotShutdown(Rainbot rainbot){
		this.rainbot = rainbot;
	}
	
	public void run() {
		rainbot.disconnect();
    		try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	} 
}