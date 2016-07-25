package listeners;

import java.util.Calendar;
import java.util.Date;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.listener.message.MessageDeleteListener;

public class DeleteListener implements MessageDeleteListener{
	public boolean isActive = false;
	public void onMessageDelete(DiscordAPI api, Message message) {
		if(!message.getAuthor().isBot() && isActive){
	    	Calendar calendar = message.getCreationDate();
	    	calendar.add(Calendar.MILLISECOND, message.getCreationDate().getTimeZone().getRawOffset());
	    	boolean inDs = message.getCreationDate().getTimeZone().inDaylightTime(new Date());
	    	if(inDs){
	    		calendar.add(Calendar.HOUR, 1);
	    	}
	    	Date timestamp = calendar.getTime();
			message.reply(message.getAuthor().getName() + "#" + message.getAuthor().getDiscriminator() + "'s message was deleted.\nOriginal comment (" + timestamp + "):\n     " + message.getContent());
		}
	}
}