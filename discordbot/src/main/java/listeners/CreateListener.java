package listeners;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.listener.message.MessageCreateListener;
import discordbot.Rainbot;
import message.DailyLogger;
import message.MessageProcessor;
import window.Window;

public class CreateListener implements MessageCreateListener{
	
	public MessageProcessor messageProcessor;
	private Window parentWindow;
	
	public DailyLogger dailyLogger;
	
	public CreateListener(Window parentWindow, MessageProcessor messageProcessor, Rainbot rainbot){
		this.parentWindow = parentWindow;
		this.messageProcessor = messageProcessor;
		this.dailyLogger = new DailyLogger(rainbot);
	}
	
    public void onMessageCreate(DiscordAPI api, Message message) {
    	if(message.getUserReceiver() != null && !message.getAuthor().equals(api.getYourself())){ //pm
    			message.getUserReceiver().sendMessage(messageProcessor.parseCommand(message.getContent(), null, message.getUserReceiver(), message));
    	}
    	messageProcessor.receiveMessage(message);
    	
    	//log messages to console.log
    	if(message.getChannelReceiver() != null && message.getChannelReceiver().equals(parentWindow.getCurrentChannel())){
    		parentWindow.addToConsoleLog("[" + message.getAuthor().getName() + "#" + message.getAuthor().getDiscriminator() + "] " + message.getContent());
    	}    	
    	
    	Calendar calendar = message.getCreationDate();
    	calendar.add(Calendar.MILLISECOND, message.getCreationDate().getTimeZone().getRawOffset());
    	boolean inDs = message.getCreationDate().getTimeZone().inDaylightTime(new Date());
    	if(inDs){
    		calendar.add(Calendar.HOUR, 1);
    	}
    	Date timestamp = calendar.getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	if(sdf.format(dailyLogger.currentTime).equals(sdf.format(timestamp))){ //writing to correct day
        	dailyLogger.addMessage(message);
    	}else{
    		System.out.println("switching to new day");
    		dailyLogger.saveMessageList();
    		dailyLogger = new DailyLogger(parentWindow.getRainbot());
    	}
    	
    }
    
    public Window getParentWindow(){
    	return parentWindow;
    }
}
