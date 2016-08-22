package listeners;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.Server;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.listener.message.MessageCreateListener;
import discordbot.Rainbot;
import message.DailyLogger;
import message.MessageProcessor;
import window.Window;

public class CreateListener implements MessageCreateListener{
	
	public MessageProcessor messageProcessor;
	private Window parentWindow;
	private Rainbot rainbot;
	public DailyLogger dailyLogger;
	private Timer midnightTimer = null;
	private Timer saveLogTimer = null;
	
	public CreateListener(Window parentWindow, MessageProcessor messageProcessor, Rainbot rainbot){
		this.parentWindow = parentWindow;
		this.messageProcessor = messageProcessor;
		this.dailyLogger = new DailyLogger(rainbot);
		this.rainbot = rainbot;
	}
	
    public void onMessageCreate(DiscordAPI api, Message message) {
    	if(message.getUserReceiver() != null && !message.getAuthor().equals(api.getYourself())){ //pm
    			message.getUserReceiver().sendMessage(messageProcessor.parseCommand(message.getContent(), null, message.getUserReceiver(), message));
    	}
    	//log messages to console.log
    	if(message.getChannelReceiver() != null){
        	messageProcessor.receiveMessage(message);
        	dailyLogger.addMessage(message);
        	if(message.getChannelReceiver().equals(parentWindow.getCurrentChannel())){
        		parentWindow.addToConsoleLog("[" + message.getAuthor().getName() + "#" + message.getAuthor().getDiscriminator() + "] " + message.getContent());
    		}
    	}    	
    }
    
    public Window getParentWindow(){
    	return parentWindow;
    }
    
    public void createTimers(){
    	if(midnightTimer == null){
    		midnightTimer = new Timer();
		    Calendar date = Calendar.getInstance();
		    date.set(Calendar.HOUR_OF_DAY, 0);
		    date.set(Calendar.MINUTE, 0);
		    date.set(Calendar.SECOND, 0);
		    date.set(Calendar.MILLISECOND, 0);
		    date.add(Calendar.DAY_OF_MONTH, 1);
	
		    // Schedule to run every day at midnight
		    midnightTimer.schedule(new TimerTask(){
		    	public void run() {
		    		for(Server server : rainbot.getImplDiscordAPI().getServers()){
		    			for(Channel channel : server.getChannels()){
		    				messageProcessor.queueMessage(channel, "first");
		    				messageProcessor.queueMessage(channel, messageProcessor.parseCommand("useractivity", channel, parentWindow.getRainbot().getImplDiscordAPI().getYourself(), null));	
		    			}
	    			}
		    		System.out.println("switching to new day");
		    		dailyLogger.saveMessageList();
		    		dailyLogger = new DailyLogger(parentWindow.getRainbot());
	    		}
			}, date.getTime(), 1000*60*60*24);
    	}
    	
    	if(saveLogTimer == null){
    		saveLogTimer = new Timer();
    		//Schedule to save every 30 minutes
    		saveLogTimer.schedule(new TimerTask(){
    			public void run(){
    				dailyLogger.saveMessageList();
    				System.out.println("automatically saved log");
    			}
    		}, 0, 1000*60*30);
    	}
    }
    
}
