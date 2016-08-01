package discordbot;

import com.google.common.util.concurrent.FutureCallback;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.ImplDiscordAPI;
import de.btobastian.javacord.Javacord;
import listeners.CreateListener;
import listeners.DeleteListener;
import listeners.EditListener;
import message.MessageProcessor;
import window.Window;

public class Rainbot {
	private Rainbot thisRainbot = this;
	private Window parentWindow;
	
	private ImplDiscordAPI implApi;
	public CreateListener createListener;
	public EditListener editListener;
	public DeleteListener deleteListener;
	public MessageProcessor messageProcessor;
	
	public boolean isConnected = false;
	
	public Rainbot(Window parentWindow){
		this.parentWindow = parentWindow;
		messageProcessor = new MessageProcessor(this);
		messageProcessor.start();
		createListener = new CreateListener(parentWindow, messageProcessor, this);
		editListener = new EditListener();
		deleteListener = new DeleteListener();
		Runtime.getRuntime().addShutdownHook(new RainbotShutdown(thisRainbot));
	}
	
	public void connect(String token){
		if(!isConnected){
			implApi = (ImplDiscordAPI) Javacord.getApi(token, true);
	        implApi.connect(new FutureCallback<DiscordAPI>() {
	            public void onSuccess(DiscordAPI api) {
	            	api.registerListener(createListener);
	            	api.registerListener(editListener);
	            	api.registerListener(deleteListener);
	                try {
	                	Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	            	isConnected = true;
	            	api.setGame("nekopara");
	            	parentWindow.setProgressBar(100);
	            	parentWindow.setLblUser("Logged in as bot user: " + api.getYourself().getName() + "#" + api.getYourself().getDiscriminator());
	            	parentWindow.addToConsoleLog("Logged in as bot user: " + api.getYourself().getName() + "#" + api.getYourself().getDiscriminator());
	            	parentWindow.updateServerComboBox();
	            	createListener.createTimers();
            	}
	            public void onFailure(Throwable t) {
	            	parentWindow.setBtnConnectToggle(false);
	                t.printStackTrace();
	            }
	        });
		}else{
			System.out.println("Didn't do anything, already connected.");
		}
	}
	
	public ImplDiscordAPI getImplDiscordAPI(){
		return implApi;
	}
	
	public Window getParentWindow(){
		return parentWindow;
	}

	public void disconnect() {
		if(isConnected){
        	isConnected = false;
			implApi.setGame(null);
			implApi.getSocketAdapter().getWebSocket().disconnect(1000);
			parentWindow.setProgressBar(0);
        	parentWindow.setLblUser("Not logged in");
        	implApi = null;
        	parentWindow.updateServerComboBox();
        	parentWindow.addToConsoleLog("Disconnected from account.");
		}else{
			System.out.println("Didn't do anything, not already connected.");
		}
	}
	
}
