package message;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JLabel;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

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
	private PasteProcessor pasteProcessor;
	private ScriptEngineManager factory;
	
	public MessageProcessor(Rainbot rainbot){
		 this.rainbot = rainbot;
		 fileProcessor = new FileProcessor();
		 pasteProcessor = new PasteProcessor();
		 factory = new ScriptEngineManager();
		 
		 //for some reason trying to create a ScriptEngine in Scriptrunner.java causes NoClassDefFoundError, doing this somehow prevents it from happening
	        // create a JavaScript engine
	        ScriptEngine engine = factory.getEngineByName("JavaScript");
	        	try {
					engine.eval("2");
				} catch (ScriptException e) {
					e.printStackTrace();
				}

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
			return "I choose `" + randomUser.getName()+ "#" + randomUser.getDiscriminator() + "`";	
		}
		else if (s.startsWith("google ")){
			s = s.substring(7);
			s = s.replace(" ", "%20");
			return "https://www.google.com/search?q=" + s + "&btnI";
		}else if (s.startsWith("stackoverflow ")){
			s = s.substring(14);
			s = s.replace(" ", "+");
			return "http://stackoverflow.com/search?q=" + s;
		}
		else if(s.startsWith("pick ")){
			s = s.substring(5);
			String[] sArray = s.split(" ");
			return "I choose " + sArray[(int)(Math.random()*sArray.length)];
		}
		else if(s.startsWith("rate ")){
			s = s.substring(5);
			return "I rate " + s + " " + ((int)(Math.random()*10) + 1) + "/10";
		}
		else if(s.startsWith("spam ")){
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
		else if(s.startsWith("roll")){
			if(s.equalsIgnoreCase("roll")){
				return "`1d6` :game_die: " + (int)(Math.random()*6 + 1);
			}
			s = s.substring(5);
			String diceString = "";
			int total = 0;
			int A = Integer.parseInt(s.substring(0,s.indexOf("d")));
			int X = Integer.parseInt(s.substring(s.indexOf("d")+1));
			diceString += "`"+A+"d"+X + "` :game_die: ";
			while(A>0){
				int roll = (int)(Math.random()*X + 1);
				diceString += roll;
				total += roll;
				A--;
				if(A>0){
					diceString +=  ", ";
				}
			}
			diceString += "\n`Total: " + total + "`";
			return diceString;
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
		    	return "uploaded img \"" + fileProcessor.getFullFilename(folderName, fileName) + "\"";
			}else{
				return "Upload failed";
			}
		}
		else if(s.equalsIgnoreCase("imagelist")){
			return fileProcessor.getFileList();
		}
		else if(s.startsWith("dir ")){
			s = s.substring(4);
			return fileProcessor.getFilesInDirectory(s);
		}
		else if(s.equalsIgnoreCase("randomimage")){
			File image = fileProcessor.getRandomFile();
			
			if(message != null){
				message.replyFile(image, image.getName());
			}else{
				channel.sendFile(image, image.getName());
			}
			return null;
		}
		else if(s.startsWith("randomimage ")){
			s = s.substring(12);
			
			File image = fileProcessor.getRandomFile(s);
			
			if(message != null){
				message.replyFile(image, image.getName());
			}else{
				channel.sendFile(image, image.getName());
			}
			return null;
		}
		else if (s.startsWith("getimage ")){
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
		else if(s.startsWith("getfolder ")){
			s = s.substring(10);
			ArrayList<InputStream> zippedFolderList = fileProcessor.getZippedFolders(s);
			for(int i=0; i<zippedFolderList.size(); i++){
				if(message != null){
					message.replyFile(zippedFolderList.get(i), s+"-"+i+".zip");
				}else{
					channel.sendFile(zippedFolderList.get(i), s+"-"+i+".zip");
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	    	return null;
		}
		else if (s.startsWith("removeimage ")){
			s = s.substring(12);
			
			String folderName = s.substring(0, s.indexOf(" "));
			String fileName = s.substring(s.indexOf(" ")+1);
			String fullFileName = fileProcessor.getFullFilename(folderName, fileName);
			fileProcessor.removeFile(folderName, fileName);
			return "removed " + folderName + "\\" + fullFileName;
		}
        else if(s.equalsIgnoreCase("help") || s.equalsIgnoreCase("help2")){
        	String helpString = ""
        			+ "`help`\n\n"
        			+ "Bot info:\n"
        			+ "    help\n"
        			+ "    help2 (broadcasts to channel)\n"
        			+ "    status\n"
        			+ "    github\n\n"
        			+ "Utilities:\n"
        			+ "    pickuser\n"
        			+ "    roll [AdX]\n"
        			+ "    coinflip\n"
        			+ "    rate [arg]\n"
        			+ "    pick [arg0] [arg1] ...\n"
        			+ "    spam [arg0] [arg1] ...\n"
        			+ "    google [query]\n"
        			+ "    stackoverflow [query]\n"
        			+ "    latex [equation]\n"
        			+ "    paste[language] [content] (e.g. 'pastejava')\n\n"
        			+ "Logging:\n"
        			+ "    log\n"
        			+ "    logdate [yyyy-mm-dd]\n"
        			+ "    find [text]\n"
        			+ "    finddate [yyyy-mm-dd] [text]\n"
        			+ "    useractivity  (message count)\n"
        			+ "    useractivity2 (word count)\n\n"
        			+ "Image/File hosting:\n"
        			+ "    upload [directory] [filename]   (Attach file to be uploaded)\n"
        			+ "    getimage [directory] [filename]\n"
        			+ "    getfolder [directory]\n"
        			+ "    removeimage [directory] [filename]\n"
        			+ "    randomimage\n"
        			+ "    randomimage [directory]\n"
        			+ "    dir [directory]\n"
        			+ "    imagelist\n\n"
        			+ "Replies:\n"
        			+ "    ping -> pong\n"
        			+ "    bye -> bye!!!!\n"
        			+ "    hi -> hi!!!!\n"
        			+ "    feelsbadman -> :frowning:";
        	if(s.equalsIgnoreCase("help")){
            	author.sendMessage(helpString);
            	if(message != null){
            		message.delete();
            	}
        	}
        	else{
        		message.reply(helpString);
        	}
        	return null;
        }
        else if(s.equalsIgnoreCase("status")){
        	int time = (int) ManagementFactory.getRuntimeMXBean().getUptime();
        	time /= 1000;
        	int seconds = (int) (time % 60);
        	time /= 60;
        	int minutes = time % 60;
        	time /= 60;
        	int hours = time % 24;
        	time /= 24;
        	int days = time;
        	String uptimeString = "";
        	if(days > 0){
        		uptimeString = days + " days, ";
        	}
        	if(hours > 0){
        		uptimeString += hours + " hours, ";
        	}
        	if(minutes > 0){
        		uptimeString += minutes + " minutes, ";
        	}
        	uptimeString += seconds + " seconds";
        	
        	author.sendMessage(""
        			+ "`status`\n"
        			+ "Running on `" + System.getProperty("os.name") + "`\n"
        			+ "Bot uptime: `" + uptimeString + "`\n\n"
        			+ "Enable commands: `" + isActive + "`\n"
        			+ "    Enable javascript parser: `" + jsEnabled + "`\n"
        			+ "    Require mention: `" + requireMention + "`\n"
        			+ "Notify on edit: `" + rainbot.editListener.isActive + "`\n"
        			+ "Notify on delete: `" + rainbot.deleteListener.isActive + "`\n");
        	if(message != null){
        		message.delete();
        	}
        	return null;
        }
        else if(s.equalsIgnoreCase("github")){
        	author.sendMessage("https://github.com/Tetragonal/Rainbot");
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
					message.replyFile(findStream, "instances of '" + s + "'.log", "Found " + instanceCount + " instances of '" + s + "' in today's log");
				}else{
					channel.sendFile(findStream, "instances of '" + s + "'.log", "Found " + instanceCount + " instances of '" + s + "' in today's log");
				}
			}
			try {
				findStream.close();
			} catch (IOException e) {
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
					message.replyFile(findStream, "instances of '" + s + "'.log", "Found " + instanceCount + " instances of '" + s + "' in " + logName + "'s log");
				}else{
					channel.sendFile(findStream, "instances of '" + s + "'.log", "Found " + instanceCount + " instances of '" + s + "' in " + logName + "'s log");
				}
			}
			try {
				findStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
        else if(s.equalsIgnoreCase("log")){
        	Object[] logResults = rainbot.createListener.dailyLogger.getLog(channel);
        	InputStream logStream = (InputStream) logResults[0];
        	int messageCount = (int) logResults[1];
			if(messageCount > 0){
				if(message != null){
					message.replyFile(logStream, "message.log", "Log contains " + messageCount + " messages");
				}else{
					channel.sendFile(logStream, "message.log", "Log contains " + messageCount + " messages");
				}
			}
			try {
				logStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	return null;
        }
		
        else if(s.startsWith("logdate ")){
			s = s.substring(8);
        	DailyLogger dailyLogger = getDailyLogger(s);
        	Object[] logResults = dailyLogger.getLog(channel);
        	InputStream logStream = (InputStream) logResults[0];
        	int messageCount = (int) logResults[1];
			if(messageCount > 0){
				if(message != null){
					message.replyFile(logStream, s + ".log", "Log contains " + messageCount + " messages");
				}else{
					channel.sendFile(logStream, s + ".log", "Log contains " + messageCount + " messages");
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
        	return rainbot.createListener.dailyLogger.getMessageCount(channel);
        }
        else if(s.equalsIgnoreCase("useractivity2")){
        	return rainbot.createListener.dailyLogger.getLetterCount(channel);
        }
        else if(s.startsWith("latex ")){
        	String latex = s.substring(6);
        	TeXFormula formula = new TeXFormula(latex);
        	TeXIcon icon = formula.new TeXIconBuilder().setStyle(TeXConstants.STYLE_DISPLAY).setSize(20).build();
        	icon.setInsets(new Insets(5, 5, 5, 5));
        	BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        	Graphics2D g2 = image.createGraphics();
        	g2.setColor(Color.white);
        	g2.fillRect(0,0,icon.getIconWidth(),icon.getIconHeight());
        	JLabel jl = new JLabel();
        	jl.setForeground(new Color(0, 0, 0));
        	icon.paintIcon(jl, g2, 0, 0);
        	ByteArrayOutputStream os = new ByteArrayOutputStream();
	   		try {
				ImageIO.write(image, "png", os);
			} catch (IOException e) {
				e.printStackTrace();
			}
	   		InputStream is = new ByteArrayInputStream(os.toByteArray());

			if(message != null){
				message.replyFile(is, "equation.png", latex);
			}else{
				channel.sendFile(is, "equation.png", latex);
			}
        }else if(s.startsWith("paste")){
        	s = s.substring(5);
        	String language = s.substring(0,s.indexOf(" "));
        	s = s.substring(s.indexOf(" ") + 1);
        	if(message != null){
        		if(message.getChannelReceiver().getServer() != null){
	        		String nickname = author.getNick(message.getChannelReceiver().getServer());
	            	message.delete();
	        		return "`" + nickname + "'s paste`\n" + pasteProcessor.createPaste(language, nickname, s);
        		}else return pasteProcessor.createPaste(language, author.getName(), s);
        	}else{
        		return pasteProcessor.createPaste(language, "paste" , s);
        	}
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
		}
	}
	
	public String parseAsJs(String command){
		
		String result = "";
		ScriptRunner scriptRunner = new ScriptRunner(command, factory);
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
