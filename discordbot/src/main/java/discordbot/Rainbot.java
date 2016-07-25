package discordbot;

import com.google.common.util.concurrent.FutureCallback;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.ImplDiscordAPI;
import de.btobastian.javacord.Javacord;
import listeners.CreateListener;
import listeners.DeleteListener;
import listeners.EditListener;

public class Rainbot {

	private ImplDiscordAPI implApi;
	private Window parentWindow;
	
	public CreateListener createListener = new CreateListener();
	public EditListener editListener = new EditListener();
	public DeleteListener deleteListener = new DeleteListener();
	
	private boolean isConnected = false;
	
	private Rainbot thisRainbot = this;
	
	public Rainbot(Window parentWindow){
		this.parentWindow = parentWindow;
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
	        		Runtime.getRuntime().addShutdownHook(new Shutdown(thisRainbot));
	            	isConnected = true;
	            	api.setGame("nekopara");
	            	api.setIdle(false);
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
			implApi.setIdle(true);
			implApi.getSocketAdapter().getWebSocket().disconnect(1000);
			parentWindow.setProgressBar(0);
        	parentWindow.setLblUser("Not logged in");
        	parentWindow.updateServerComboBox();
        	implApi = null;
        	parentWindow.updateServerComboBox();
		}else{
			System.out.println("Didn't do anything, not already connected.");
		}
	}
}
