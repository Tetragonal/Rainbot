package listeners;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.listener.message.MessageCreateListener;
import discordbot.MessageProcessor;
import discordbot.Rainbot;
import discordbot.Window;

public class CreateListener implements MessageCreateListener{
	
	public MessageProcessor messageProcessor;
	private Window parentWindow;
	
	public CreateListener(Window parentWindow, MessageProcessor messageProcessor){
		this.parentWindow = parentWindow;
		this.messageProcessor = messageProcessor;
	}
	
    public void onMessageCreate(DiscordAPI api, Message message) {
    	
    	//log messages to console.log
    	if(message.getChannelReceiver().equals(parentWindow.getCurrentChannel())){
    		parentWindow.addToConsoleLog("[" + message.getAuthor().getName() + "#" + message.getAuthor().getDiscriminator() + "] " + message.getContent());
    	}
    	
    	messageProcessor.receiveMessage(message);
    }
    
    public Window getParentWindow(){
    	return parentWindow;
    }
}
