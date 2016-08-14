package message;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.btobastian.javacord.entities.message.Message;

public class MessageData implements Serializable{
	private static final long serialVersionUID = -2973824070721066287L;

	public String messageID;
	
	public String messageContent;
	public String authorName;
	public String authorDiscriminator;
	public String authorID;
	
	public String serverReceiverID;
	public String channelReceiverID;
	
	public String messageTimestamp;
	
	public MessageData(Message message){
    	Calendar calendar = message.getCreationDate();
    	calendar.add(Calendar.MILLISECOND, message.getCreationDate().getTimeZone().getRawOffset());
    	boolean inDs = message.getCreationDate().getTimeZone().inDaylightTime(new Date());
    	if(inDs){
    		calendar.add(Calendar.HOUR, 1);
    	}
    	SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	messageTimestamp = sf.format(calendar.getTime());

    	messageID = message.getId();
    	
		messageContent = message.getContent();
		authorName = message.getAuthor().getName();
		authorDiscriminator = message.getAuthor().getDiscriminator();
		authorID = message.getAuthor().getId();
		
		serverReceiverID = message.getChannelReceiver().getServer().getId();
		channelReceiverID = message.getChannelReceiver().getId();
	}
	
	public String toString(){
		return "[" + authorName + "#" + authorDiscriminator + "] (" + messageTimestamp + ") " + messageContent;
	}
	
}
