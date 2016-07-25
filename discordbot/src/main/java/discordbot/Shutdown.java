package discordbot;

public class Shutdown extends Thread{
	private Rainbot rainbot;
	
	public Shutdown(Rainbot rainbot){
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