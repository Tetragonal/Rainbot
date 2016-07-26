package discordbot;

public class WindowShutdown extends Thread{
	private Window window;
	public WindowShutdown(Window window){
		this.window = window;
	}
	public void run() {
		window.setProperties();
    		try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	} 
}