package discordbot;

import java.io.FileOutputStream;

import javax.swing.JTextField;

import com.google.common.util.concurrent.FutureCallback;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.ImplDiscordAPI;
import de.btobastian.javacord.Javacord;
import listeners.CreateListener;
import listeners.DeleteListener;
import listeners.EditListener;

public class Rainbot {
	private Rainbot thisRainbot = this;
	private Window parentWindow;
	
	private ImplDiscordAPI implApi;
	public CreateListener createListener;
	public EditListener editListener;
	public DeleteListener deleteListener;
	public MessageProcessor messageProcessor;
	
	private boolean isConnected = false;
	
	public Rainbot(Window parentWindow){
		this.parentWindow = parentWindow;
		messageProcessor = new MessageProcessor(this);
		messageProcessor.start();
		createListener = new CreateListener(parentWindow, messageProcessor);
		editListener = new EditListener();
		deleteListener = new DeleteListener();
		Runtime.getRuntime().addShutdownHook(new RainbotShutdown(thisRainbot));
	}
	
	public void connect(String email, String password, String token){
		if(!isConnected){
			implApi = (ImplDiscordAPI) Javacord.getApi(email, password);
//			api = Javacord.getApi(email, password);
			implApi.setToken(token, true); // String token, boolean bot
			
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
	            	parentWindow.updateServerComboBox();
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
