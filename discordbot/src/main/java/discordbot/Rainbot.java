package discordbot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.google.common.util.concurrent.FutureCallback;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.ImplDiscordAPI;
import de.btobastian.javacord.Javacord;
import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.message.MessageHistory;
import listeners.CreateListener;
import listeners.DeleteListener;
import listeners.EditListener;
import message.DailyLogger;
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
	
	public String token = null;
	
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
		this.token = token;
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
	        		loadOfflineMessages();
            	}
	            public void onFailure(Throwable t) {
	            	parentWindow.setBtnConnectToggle(false);
	                t.printStackTrace();
	            }
	        });
		}else{
			parentWindow.addToConsoleLog("Didn't do anything, already connected.");
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
			parentWindow.addToConsoleLog("Didn't do anything, not already connected.");
		}
	}
	
	public void loadOfflineMessages(){
		System.out.println("loading offline messages");
		
		DailyLogger dailyLogger = createListener.dailyLogger;
		
		if(dailyLogger.messageList.size() == 0){
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			dailyLogger = new DailyLogger(this, sdf.format(calendar.getTime()));
		}
		
		ArrayList<Future<MessageHistory>> futureMessageHistoryList = new ArrayList<Future<MessageHistory>>();
		
		//doesnt go back multiple days atm
		
		//create list
		for(Channel c : implApi.getChannels()){
			for (int i=dailyLogger.messageList.size()-1; i >= 0 ; i--){
				if(dailyLogger.messageList.get(i).channelReceiverID.equals(c.getId())){
					Future<MessageHistory> futureMessageHistory = c.getMessageHistoryAfter(dailyLogger.messageList.get(i).messageID, 9999);
					futureMessageHistoryList.add(futureMessageHistory);
					i = -1; //stop loop
				}
			}
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String formattedDate = sdf.format(dailyLogger.currentDate);
		//process list
		for(Future<MessageHistory> futureMessageHistory : futureMessageHistoryList){
			
			MessageHistory messageHistory = null;
			try{
				messageHistory = futureMessageHistory.get();
				for(Message m : messageHistory.getMessagesSorted()){
			    	Calendar messageDate = m.getCreationDate();
			    	messageDate.add(Calendar.MILLISECOND, m.getCreationDate().getTimeZone().getRawOffset());
			    	boolean inDs = m.getCreationDate().getTimeZone().inDaylightTime(new Date());
			    	if(inDs){
			    		messageDate.add(Calendar.HOUR, 1);
			    	}
					
					if(sdf.format(messageDate.getTime()).equals(formattedDate)){
						System.out.println("loaded offline message  " + m.getContent());
						dailyLogger.addMessage(m);
					}
					else{
						dailyLogger.saveMessageList();

						dailyLogger = new DailyLogger(this,sdf.format(messageDate.getTime()));
						formattedDate = sdf.format(dailyLogger.currentDate);
						System.out.println(sdf.format(messageDate.getTime())+ " " + formattedDate + " " + "new day, loaded offline message  " + m.getContent());
						dailyLogger.addMessage(m);
					}
				}
				dailyLogger.saveMessageList();
				createListener.dailyLogger.loadMessageList();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}	
		}
	}
}
