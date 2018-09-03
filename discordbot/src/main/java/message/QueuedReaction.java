package message;

import de.btobastian.javacord.entities.message.Message;

public class QueuedReaction {
	private Message message;
	private String reaction;
	
	public QueuedReaction(Message message, String reaction){
		this.message = message;
		this.reaction = reaction;
	}
	
	public void execute(){	
		message.addUnicodeReaction(reaction);
	}
}
