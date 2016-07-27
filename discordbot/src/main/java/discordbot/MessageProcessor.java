package discordbot;

import java.util.ArrayList;
import java.util.List;

import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
import listeners.CreateListener;

public class MessageProcessor extends Thread{
	List<Channel> channelList = new ArrayList<Channel>();
	List<String>  messageList = new ArrayList<String>();
	
	public boolean isActive = false;
	public boolean jsEnabled = false;
	public boolean requireMention = true;
	
	private Rainbot rainbot;
	
	public MessageProcessor(Rainbot rainbot){
		 this.rainbot = rainbot;
	}
	
	public void run() {
	        System.out.println("Hello from a thread!");
			 sendMessages();
    }
	
	
	public void sendMessages(){
		if(channelList.size() > 0 && messageList.size() > 0){
			Channel c = channelList.get(0);
			String s = messageList.get(0);
			c.sendMessage(s);
			channelList.remove(0);
			messageList.remove(0);
		}
		
		try {
			sleep(1100); //to avoid automute from sending messages too quickly
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		sendMessages();
	}
	
	public void queueMessage(Channel channel, String message){
		channelList.add(channel);
		messageList.add(message);
	}
	
	public String parseCommand(Channel channel, String command){
		String s = command;
		if(s.equalsIgnoreCase("pickuser")){
			ArrayList<User> users = new ArrayList<User>();
			for(User u : channel.getServer().getMembers()){
				users.add(u);
			}
			User randomUser;
			do{
				randomUser = users.get((int)(Math.random()*users.size()));
			}while (randomUser.isYourself());
			return "I choose " + randomUser.getName()+ "#" + randomUser.getDiscriminator();
		}
		else if(s.length() >= 5 && s.substring(0,5).equals("pick ")){
			s = s.substring(5);
			String[] sArray = s.split(" ");
			return "I choose " + sArray[(int)(Math.random()*sArray.length)];
		}	
		else if(s.length() >= 5 && s.substring(0,5).equals("spam ")){
			s = s.substring(5);
			String[] sArray = s.split(" ");
			String spam = "";
			for(int i=0; i<250 && spam.length() < 1500; i++){
				spam += sArray[(int)(Math.random()*sArray.length)] + " ";
			}
			return spam;
		}		
		else if(s.equalsIgnoreCase("coinflip")){
			int coinflip = (int)(Math.round(Math.random()));
			if(coinflip == 1){
				return "heads";
			}else if(coinflip == 0){
				return "tails";
			}
		}
		else if(s.equalsIgnoreCase("diceroll")){
			return ""+(int)(Math.random()*6 + 1);
		}
		else if (s.equalsIgnoreCase("ping")) {
			return "pong";
        }
		else if (s.equalsIgnoreCase("bye")) {
			return "bye!!!!";
        }
		else if (s.equalsIgnoreCase("hi") | s.equalsIgnoreCase("hello")) {
			return "hi!!!!";
        }
		else if(s.equalsIgnoreCase("hey jay where do you live")){
			return "hey jay where do you live\nhey jay where do you live\nhey jay where do you live\nhey jay where do you live\nhey jay where do you live";
        }
		else if(s.toLowerCase().contains("feelsbadman")){
			return ":frowning:";
        }
		
		else if(jsEnabled){
			return parseAsJs(command);
		}
		return null;
	}
	
	public void receiveMessage(Message message){
		if(!message.getAuthor().isBot() && isActive){
    		if(!(requireMention) || message.getMentions().contains(rainbot.getImplDiscordAPI().getYourself()) && message.getMentions().size() == 1){
    			String command = "";
    			if(message.getMentions().contains(rainbot.getImplDiscordAPI().getYourself())){
        			command = message.getContent().substring(message.getContent().indexOf(">")+2);
    			}else{
    				command = message.getContent();
    			}	
    			String result = parseCommand(message.getChannelReceiver(), command);
    			if(result != null){
    				queueMessage(message.getChannelReceiver(), result);
    			}
    		}
		}
	}
	
	public String parseAsJs(String command){
		String result = "";
		ScriptRunner scriptRunner = new ScriptRunner(command);
        try {
            Thread t = new Thread(scriptRunner);
            t.start();
            result = scriptRunner.processScript();
            Thread.sleep(200);
            t.interrupt();
            Thread.sleep(200);
            t.stop();
        } catch(InterruptedException ie) {
            throw new RuntimeException(ie);
        }
        return result;
	}
}
