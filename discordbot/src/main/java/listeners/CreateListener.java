package listeners;

import java.util.ArrayList;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.listener.message.MessageCreateListener;
import discordbot.Window;

@SuppressWarnings("restriction")
public class CreateListener implements MessageCreateListener{
	private Window parentWindow;
	public CreateListener(Window parentWindow){
		this.parentWindow = parentWindow;
	}
	public boolean isActive = false;
	public boolean jsEnabled = false;
	public boolean requireMention = true;
	
    public void onMessageCreate(DiscordAPI api, Message message) {
    	
    	if(message.getChannelReceiver().equals(parentWindow.getCurrentChannel())){
    		parentWindow.addToConsoleLog("[" + message.getAuthor().getName() + "#" + message.getAuthor().getDiscriminator() + "] " + message.getContent());
    	}   	
    	
    	
    	if(!message.getAuthor().isBot() && isActive){
    		if(!(requireMention) || message.getMentions().contains(api.getYourself()) && message.getMentions().size() == 1){
    			String s = "";
    			if(message.getMentions().contains(api.getYourself())){
        			s = message.getContent().substring(message.getContent().indexOf(">")+2);
    			}else{
    				s = message.getContent();
    			}
	    		if(s.equalsIgnoreCase("pickuser")){
	    			ArrayList<User> users = new ArrayList<User>();
	    			for(User u : message.getChannelReceiver().getServer().getMembers()){
	    				users.add(u);
	    			}
	    			User randomUser;
	    			do{
	    				randomUser = users.get((int)(Math.random()*users.size()));
	    			}while (randomUser.isYourself());
	    			message.reply("I choose " + randomUser.getName()+ "#" + randomUser.getDiscriminator());
	    		}
	    		else if(s.length() >= 4 && s.substring(0,5).equals("pick ")){
	    			s = s.substring(5);
	    			String[] sArray = s.split(" ");
	    			message.reply("I choose " + sArray[(int)(Math.random()*sArray.length)]);
	    		}	
	    		else if(s.length() >= 4 && s.substring(0,5).equals("spam ")){
	    			s = s.substring(5);
	    			String[] sArray = s.split(" ");
	    			String spam = "";
	    			for(int i=0; i<250 && spam.length() < 1500; i++){
	    				spam += sArray[(int)(Math.random()*sArray.length)] + " ";
	    			}
	    			message.reply(spam);
	    		}		
	    		else if(s.equalsIgnoreCase("coinflip")){
	    			int coinflip = (int)(Math.round(Math.random()));
	    			if(coinflip == 1){
	    				message.reply("heads");
	    			}else if(coinflip == 0){
	    				message.reply("tails");
	    			}
	    		}
	    		else if(s.equalsIgnoreCase("diceroll")){
	    			message.reply(""+(int)(Math.random()*6 + 1));
	    		}
	    		else if (s.equalsIgnoreCase("ping")) {
	                message.reply("pong");
	            }
	    		else if (s.equalsIgnoreCase("bye")) {
	                message.reply("bye!!!!");
	            }
	    		else if (s.equalsIgnoreCase("hi") | s.equalsIgnoreCase("hello")) {
	                message.reply("hi!!!!");
	            }
	    		else if(s.equalsIgnoreCase("hey jay where do you live")){
	            	message.reply("hey jay where do you live\nhey jay where do you live\nhey jay where do you live\nhey jay where do you live\nhey jay where do you live");
	            }
	    		else if(s.toLowerCase().contains("feelsbadman")){
	            	message.reply(":frowning:");
	            }
	            
	    		if(jsEnabled){
		    	    ScriptEngineManager mgr = new ScriptEngineManager();
		    	    ScriptEngine engine = mgr.getEngineByName("JavaScript");	    
		    	    if(s.equals("2+2")){
		    	    	message.reply("5");
		    	    }
		    	    else if(!s.contains("exit()")){
			    	    try {
			    			message.reply(engine.eval(s).toString());
			    		} catch (ScriptException e) {
			    			//Do nothing
			    		}
		    	    }
	    		}
    		}
    		
    	}	
    }    
}
