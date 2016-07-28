package window;

public class WindowShutdown extends Thread{
	private Window window;
	public WindowShutdown(Window window){
		this.window = window;
	}
	public void run() {
		if(window.checkBoxSaveProperties.isSelected()){
			window.setProperties();
		}
    		try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	} 
}