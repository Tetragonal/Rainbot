package message;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.message.MessageAttachment;
import discordbot.Rainbot;

public class MessageProcessor extends Thread{
	List<Channel> channelList = new ArrayList<Channel>();
	List<String>  messageList = new ArrayList<String>();
	
	public boolean isActive = false;
	public boolean jsEnabled = false;
	public boolean requireMention = true;
	
	private Rainbot rainbot;
	private FileProcessor fileProcessor;
	
	public MessageProcessor(Rainbot rainbot){
		 this.rainbot = rainbot;
		 fileProcessor = new FileProcessor();
	}
	
	public void run() {
		sendMessages();
    }
	
	public void sendMessages(){		
		while(true){
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
		}
	}
	
	public void queueMessage(Channel channel, String message){
		channelList.add(channel);
		messageList.add(message);
	}
	
	public String parseCommand(String command, Channel channel, User author, Message message){
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
		else if (s.length() >= 7 && s.substring(0,7).equals("google ")){
			s = s.substring(7);
			s = s.replace(" ", "%20");
			return "https://www.google.com/search?q=" + s + "&btnI";
		}else if (s.length() >= 14 && s.substring(0,14).equals("stackoverflow ")){
			s = s.substring(14);
			s = s.replace(" ", "+");
			return "http://stackoverflow.com/search?q=" + s;
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
		else if(s.equalsIgnoreCase("feelsbadman")){
			return ":frowning:";
        }
		else if(s.length() >= 7+1 && s.substring(0,7).equals("upload ") && message.getAttachments().size() > 0){
			s = s.substring(7);
			
			String folderName = s.substring(0, s.indexOf(" "));
			String fileName = s.substring(s.indexOf(" ")+1);
			
			MessageAttachment messageAttachment = message.getAttachments().iterator().next();
			boolean addedFile = fileProcessor.addFile(folderName, fileName, messageAttachment);
			if(addedFile){
		    	return "uploaded img \"" + fileName + "\"";
			}else{
				return "Upload failed";
			}
		}
		else if(s.equalsIgnoreCase("imagelist")){
			return fileProcessor.getImageList();
		}
		else if(s.length() >= 4 && s.substring(0,4).equals("dir ")){
			s = s.substring(4);
			return fileProcessor.getFilesInDirectory(s);
		}
		else if(s.length() >= 12+1 && s.substring(0,12).equals("randomimage ")){
			s = s.substring(12);
			
			File image = fileProcessor.getRandomImage(s);
			
			if(message != null){
				message.replyFile(image, image.getName());
			}else{
				channel.sendFile(image, image.getName());
			}
			return null;
		}
		else if (s.length() >= 9+1 && s.substring(0,9).equals("getimage ")){
			s = s.substring(9);
			
			String folderName = s.substring(0, s.indexOf(" "));
			String fileName = s.substring(s.indexOf(" ")+1);

			File image = fileProcessor.getFile(folderName, fileName);
			if(message != null){
				message.replyFile(image, image.getName());
			}else{
				channel.sendFile(image, image.getName());
			}	
	    	return null;
		}
		else if (s.length() >= 12+1 && s.substring(0,12).equals("removeimage ")){
			s = s.substring(12);
			
			String folderName = s.substring(0, s.indexOf(" "));
			String fileName = s.substring(s.indexOf(" ")+1);

			fileProcessor.removeFile(folderName, fileName);
			return "removed image " + folderName + "/" + fileName;
		}
		//special cases
        else if(s.equalsIgnoreCase("help")){
        	author.sendMessage(""
        			+ "`help`\n\n"
        			+ "Bot info:\n"
        			+ "    help\n"
        			+ "    status\n\n"
        			+ "Utilities:\n"
        			+ "    pickuser\n"
        			+ "    diceroll\n"
        			+ "    coinflip\n"
        			+ "    google [query]\n"
        			+ "    stackoverflow [query]\n"
        			+ "    pick [arg1] [arg2] ...\n"
        			+ "    spam [arg1] [arg2] ...\n"
        			+ "    find [text]\n"
        			+ "    finddate [yyyy-mm-dd] [text]\n"
        			+ "    useractivity\n\n"
        			+ "Image/File hosting:\n"
        			+ "    upload [directory] [filename]   (Attach file to be uploaded)\n"
        			+ "    getimage [directory] [filename]\n"
        			+ "    removeimage [directory] [filename]\n"
        			+ "    randomimage [directory]\n"
        			+ "    dir [directory]\n"
        			+ "    imagelist\n\n"
        			+ "Replies:\n"
        			+ "    ping -> pong\n"
        			+ "    bye -> bye!!!!\n"
        			+ "    hi -> hi!!!!\n"
        			+ "    hey jay where do you live -> hey jay where do you live (x5)\n"
        			+ "    feelsbadman -> :frowning:");
        	if(message != null){
        		message.delete();
        	}
        	return null;
        }
        else if(s.equalsIgnoreCase("status")){
        	author.sendMessage(""
        			+ "`status`\n\n"
        			+ "Enable commands: " + isActive + "\n"
        			+ "    Enable javascript parser: " + jsEnabled + "\n"
        			+ "    Require mention: " + requireMention + "\n"
        			+ "Notify on edit: " + rainbot.editListener.isActive + "\n"
        			+ "Notify on delete: " + rainbot.deleteListener.isActive + "\n");
        	if(message != null){
        		message.delete();
        	}
        	return null;
        }
        else if(s.length() >= 5+3 && s.substring(0,5).equals("find ")){
			s = s.substring(5);
			
			Object[] findResults = rainbot.createListener.dailyLogger.find(channel, s);
			InputStream findStream = (InputStream) findResults[0];
			int instanceCount = (int) findResults[1];

			if(instanceCount > 0){
				if(message != null && instanceCount > 0){
					message.replyFile(findStream, "instances of '" + s + "'.rtf", "Found " + instanceCount + " instances of '" + s + "' in today's log");
				}else{
					channel.sendFile(findStream, "instances of '" + s + "'.rtf", "Found " + instanceCount + " instances of '" + s + "' in today's log");
				}
			}
			try {
				findStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
        else if(s.length() >= 9+3 && s.substring(0,9).equals("finddate ")){
			s = s.substring(9);
			
			String logName = s.substring(0, s.indexOf(" "));
			s = s.substring(s.indexOf(" ")+1);
			
			DailyLogger dailyLogger = getDailyLogger(logName);
			
			Object[] findResults = dailyLogger.find(channel, s);
			InputStream findStream = (InputStream) findResults[0];
			int instanceCount = (int) findResults[1];
			if(instanceCount > 0){
				if(message != null){
					message.replyFile(findStream, "instances of '" + s + "'.rtf", "Found " + instanceCount + " instances of '" + s + "' in " + logName + "'s log");
				}else{
					channel.sendFile(findStream, "instances of '" + s + "'.rtf", "Found " + instanceCount + " instances of '" + s + "' in " + logName + "'s log");
				}
			}
			try {
				findStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
        else if(s.length() >= 3 && s.equalsIgnoreCase("log")){
        	Object[] logResults = rainbot.createListener.dailyLogger.getLog(channel);
        	InputStream logStream = (InputStream) logResults[0];
        	int messageCount = (int) logResults[1];
			if(messageCount > 0){
				if(message != null){
					message.replyFile(logStream, "log.rtf", "Log contains " + messageCount + " messages");
				}else{
					channel.sendFile(logStream, "log.rtf", "Log contains " + messageCount + " messages");
				}
			}
			try {
				logStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	return null;
        }
		
        else if(s.length() >= 8 && s.substring(0,8).equals("logdate ")){
			s = s.substring(8);
        	DailyLogger dailyLogger = getDailyLogger(s);
        	Object[] logResults = dailyLogger.getLog(channel);
        	InputStream logStream = (InputStream) logResults[0];
        	int messageCount = (int) logResults[1];
			if(messageCount > 0){
				if(message != null){
					message.replyFile(logStream, "log " + s + ".rtf", "Log contains " + messageCount + " messages");
				}else{
					channel.sendFile(logStream, "log " + s + ".rtf", "Log contains " + messageCount + " messages");
				}
			}
			try {
				logStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	return null;
        }
		
		
        else if(s.equalsIgnoreCase("useractivity")){
        	return rainbot.createListener.dailyLogger.getUserActivity(channel);
        }
		
		//parse if js if not a command
        else if(jsEnabled){
			return parseAsJs(command);
		}
		return null;
	}
	
	public DailyLogger getDailyLogger(String logName){
		Calendar calendar = Calendar.getInstance();
		Date currentTime = calendar.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if(sdf.format(currentTime).equals(logName)){
			return rainbot.createListener.dailyLogger;
		}
		else return new DailyLogger(rainbot, logName); //makes new logger if loading old stuff
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
    			String result = parseCommand(command, message.getChannelReceiver(), message.getAuthor(), message);
    			if(result != null){
    				queueMessage(message.getChannelReceiver(), result);
    			}
    		}
    		//special cases
//			if (message.getContent().matches("[A-Z^\\s+$]+") && message.getContent().length() >= 4){
//	        	queueMessage(message.getChannelReceiver(), "pls no caps lock");
//	        }
		}
	}
	
	public String parseAsJs(String command){
		String result = "";
		ScriptRunner scriptRunner = new ScriptRunner(command);
        try {
            Thread t = new Thread(scriptRunner);
            t.start();
            Thread.sleep(200);
            t.interrupt();
            Thread.sleep(200);
            result = scriptRunner.getResult();
            t.stop();
        } catch(InterruptedException ie) {
            throw new RuntimeException(ie);
        }
        return result;
	}
}
