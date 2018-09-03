package message;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.script.ScriptEngine;
import javax.swing.JLabel;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import com.vdurmont.emoji.EmojiParser;

import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.message.MessageAttachment;
import discordbot.Rainbot;
import jdk.nashorn.api.scripting.ClassFilter;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

public class MessageProcessor extends Thread{
	List<Object> recipientList = new ArrayList<Object>();
	List<Object>  messageList = new ArrayList<Object>();
	
	public boolean isActive = false;
	public boolean jsEnabled = false;
	public boolean requireMention = true;
	
	private Rainbot rainbot;
	private FileProcessor fileProcessor;
	private PasteProcessor pasteProcessor;
	private ScriptEngine engine;
	
	public MessageProcessor(Rainbot rainbot){
		 this.rainbot = rainbot;
		 fileProcessor = new FileProcessor();
		 pasteProcessor = new PasteProcessor();
		 
		 NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
		 // create a JavaScript engine
		engine = factory.getScriptEngine(new ClassFilter() {
			// this one simply forbids use of any java classes, including
			// reflection
			@Override
			public boolean exposeToScripts(String string) {
				return false;
			}
		});
		

	}
	
	public void run() {
		sendMessages();
    }
	
	public void sendMessages(){		
		while(true){
			if(recipientList.size() > 0 && messageList.size() > 0){
				Object recipient = recipientList.get(0);
				Object message = messageList.get(0);
				if(message instanceof String){
					if(recipient instanceof Channel){
						((Channel)recipient).sendMessage((String) message);
					}
					else if(recipient instanceof User){
						((User)recipient).sendMessage((String) message);
					}
				}
				else if(message instanceof QueuedReaction){
					((QueuedReaction) message).execute();

				}
				recipientList.remove(0);
				messageList.remove(0);
			}
			try {
				sleep(1100); //to avoid automute from sending messages too quickly
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void queueMessage(Object recipient, Object message){
		recipientList.add(recipient);
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
		else if (s.startsWith("say")){
			s = s.substring(4);
			message.delete();
			return s;
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
			int result = ((int)(Math.random()*10) + 1);
			//lol
			switch(result){
				case 1:
					queueMessage(null, new QueuedReaction(message, EmojiParser.parseToUnicode(":one:")));
					break;
				case 2:
					queueMessage(null, new QueuedReaction(message, EmojiParser.parseToUnicode(":two:")));
					break;
				case 3:
					queueMessage(null, new QueuedReaction(message, EmojiParser.parseToUnicode(":three:")));
					break;
				case 4:
					queueMessage(null, new QueuedReaction(message, EmojiParser.parseToUnicode(":four:")));
					break;
				case 5:
					queueMessage(null, new QueuedReaction(message, EmojiParser.parseToUnicode(":five:")));
					break;
				case 6:
					queueMessage(null, new QueuedReaction(message, EmojiParser.parseToUnicode(":six:")));
					break;
				case 7:
					queueMessage(null, new QueuedReaction(message, EmojiParser.parseToUnicode(":seven:")));
					break;
				case 8:
					queueMessage(null, new QueuedReaction(message, EmojiParser.parseToUnicode(":eight:")));
					break;
				case 9:
					queueMessage(null, new QueuedReaction(message, EmojiParser.parseToUnicode(":nine:")));
					break;
				case 10:
					queueMessage(null, new QueuedReaction(message, EmojiParser.parseToUnicode(":keycap_ten:")));
					break;
			}
			
			return "I rate " + s + " " + result + "/10";
		}
		else if(s.startsWith("spam ")){
			queueMessage(null, new QueuedReaction(message, EmojiParser.parseToUnicode("😠")));
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
			queueMessage(null, new QueuedReaction(message, "🏓"));
			return "pong";
        }
		else if (s.equalsIgnoreCase("bye")) {
			queueMessage(null, new QueuedReaction(message, "👋"));
			return "bye!!!!";
        }
		else if (s.equalsIgnoreCase("hi") | s.equalsIgnoreCase("hello")) {
			queueMessage(null, new QueuedReaction(message, "😃"));
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
			boolean addedFile = fileProcessor.addFile(channel.getServer(), folderName, fileName, messageAttachment);
			if(addedFile){
		    	return "uploaded img \"" + fileProcessor.getFullFilename(channel.getServer(), folderName, fileName) + "\"";
			}else{
				return "Upload failed";
			}
		}
		else if(s.equalsIgnoreCase("imagelist")){
			return fileProcessor.getFileList(channel.getServer());
		}
		else if(s.startsWith("dir ")){
			s = s.substring(4);
			return fileProcessor.getFilesInDirectory(channel.getServer(), s);
		}
		else if(s.equalsIgnoreCase("randomimage")){
			File image = fileProcessor.getRandomFile(channel.getServer());
			
			if(message != null){
				message.getChannelReceiver().sendFile(image, image.getName());
			}else{
				channel.sendFile(image, image.getName());
			}
			return null;
		}
		else if(s.startsWith("randomimage ")){
			s = s.substring(12);
			
			File image = fileProcessor.getRandomFile(channel.getServer(), s);
			
			if(message != null){
				message.getChannelReceiver().sendFile(image, image.getName());
			}else{
				channel.sendFile(image, image.getName());
			}
			return null;
		}
		else if (s.startsWith("getimage ")){
			s = s.substring(9);
			
			String folderName = s.substring(0, s.indexOf(" "));
			String fileName = s.substring(s.indexOf(" ")+1);

			File image = fileProcessor.getFile(channel.getServer(), folderName, fileName);
			if(message != null){
				message.getChannelReceiver().sendFile(image, image.getName());
			}else{
				channel.sendFile(image, image.getName());
			}	
	    	return null;
		}
		else if(s.startsWith("getfolder ")){
			s = s.substring(10);
			ArrayList<InputStream> zippedFolderList = fileProcessor.getZippedFolders(channel.getServer(), s);
			for(int i=0; i<zippedFolderList.size(); i++){
				if(message != null){
					message.getChannelReceiver().sendFile(zippedFolderList.get(i), s+"-"+i+".zip");
				}else{
					channel.sendFile(zippedFolderList.get(i), s+"-"+i+".zip");
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
	    	return null;
		}
		else if (s.startsWith("removeimage ")){
			s = s.substring(12);
			
			String folderName = s.substring(0, s.indexOf(" "));
			String fileName = s.substring(s.indexOf(" ")+1);
			String fullFileName = fileProcessor.getFullFilename(channel.getServer(), folderName, fileName);
			fileProcessor.removeFile(channel.getServer(), folderName, fileName);
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
        			+ "    paste[language] [content] (e.g. 'pastejava')\n"
        			+ "    say [text]\n\n"
        			+ "    remindme [time] \"[text]\" (e.g. 'remindme 1 minute \"sleep\")'\n"
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
        	long time = (long) ManagementFactory.getRuntimeMXBean().getUptime();
        	time /= 1000;
        	long seconds = time % 60;
        	time /= 60;
        	long minutes = time % 60;
        	time /= 60;
        	long hours = time % 24;
        	time /= 24;
        	long days = time;
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
        			+ "Notify on delete: `" + rainbot.deleteListener.isActive + "`\n"
        			+ "Log messages: `" + rainbot.createListener.isLogging + "`\n");
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
			
			Object[] findResults = rainbot.createListener.messageLogger.find(channel, s);
			InputStream findStream = (InputStream) findResults[0];
			int instanceCount = (int) findResults[1];

			if(instanceCount > 0){
				if(message != null && instanceCount > 0){
					message.getChannelReceiver().sendFile(findStream, "instances of '" + s + "'.log", "Found " + instanceCount + " instances of '" + s + "' in today's log");
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
			
			MessageLogger messageLogger = getDailyLogger(logName);
			
			Object[] findResults = messageLogger.find(channel, s);
			InputStream findStream = (InputStream) findResults[0];
			int instanceCount = (int) findResults[1];
			if(instanceCount > 0){
				if(message != null){
					message.getChannelReceiver().sendFile(findStream, "instances of '" + s + "'.log", "Found " + instanceCount + " instances of '" + s + "' in " + logName + "'s log");
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
        	Object[] logResults = rainbot.createListener.messageLogger.getLog(channel);
        	InputStream logStream = (InputStream) logResults[0];
        	int messageCount = (int) logResults[1];
			if(messageCount > 0){
				if(message != null){
					message.getChannelReceiver().sendFile(logStream, "message.log", "Log contains " + messageCount + " messages");
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
        	MessageLogger messageLogger = getDailyLogger(s);
        	Object[] logResults = messageLogger.getLog(channel);
        	InputStream logStream = (InputStream) logResults[0];
        	int messageCount = (int) logResults[1];
			if(messageCount > 0){
				if(message != null){
					message.getChannelReceiver().sendFile(logStream, s + ".log", "Log contains " + messageCount + " messages");
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
        	return rainbot.createListener.messageLogger.getMessageCount(channel);
        }
        else if(s.equalsIgnoreCase("useractivity2")){
        	return rainbot.createListener.messageLogger.getLetterCount(channel);
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
				message.getChannelReceiver().sendFile(is, "equation.png", latex);
			}else{
				channel.sendFile(is, "equation.png", latex);
			}
        }else if(s.startsWith("paste")){
        	s = s.substring(5);
        	String language = s.substring(0,s.indexOf(" "));
        	s = s.substring(s.indexOf(" ") + 1);
        	if(message != null){
        		if(message.getChannelReceiver().getServer() != null){
	        		String nickname = author.getNickname(message.getChannelReceiver().getServer());
	            	message.delete();
	        		return "`" + nickname + "'s paste`\n" + pasteProcessor.createPaste(language, nickname, s);
        		}else return pasteProcessor.createPaste(language, author.getName(), s);
        	}else{
        		return pasteProcessor.createPaste(language, "paste" , s);
        	}
    	}else if(s.startsWith("remindme ")){
    		s = s.substring(9);
    		String time = s.substring(0, s.indexOf('\"')); //throws exception if no "
    		String[] units = time.split(" "); //will break the string up into an array
    		int days = 0;
    		int hours = 0;
    		int minutes = 0;
    		int seconds = 0;
    		queueMessage(null, new QueuedReaction(message, EmojiParser.parseToUnicode("⏰")));
    		for(int i=0; i<units.length; i++){
    			switch(units[i]){
					case "day":
					case "days":
						days = Integer.parseInt(units[i-1]);
						break;
    				case "hour":
    				case "hours":
    				case "hr":
    				case "hrs":
    					hours = Integer.parseInt(units[i-1]);
    					break;
    				case "minute":
    				case "minutes":
    				case "mins":
    				case "min":
    					minutes = Integer.parseInt(units[i-1]);
    					break;
    				case "second":
    				case "seconds":
    				case "secs":
    				case "sec":
    					seconds = Integer.parseInt(units[i-1]);
    					break;
    			}
    		}
    		int duration = 24*60*60*days + 60*60*hours + 60*minutes + seconds; //add up our values
    		if(duration == 0){
    			duration = 60*60;
    			minutes = 1;
    		}
    		Timer reminderTimer = new Timer();
    		s = s.substring(s.indexOf('\"')+1);
    		final String reminder = s.substring(0, s.indexOf('\"'));
    		//Schedule to save + restart every 30 minutes
    		reminderTimer.schedule(new TimerTask(){
    			public void run(){
    				queueMessage(channel, author.getMentionTag() + " Reminder: `" + reminder + "`");
    				//author.sendMessage(" Reminder: " + reminder);
    			}
    		}, 1000*duration);
    		author.sendMessage("Reminding you in `" + days + " days, " + hours + " hours, " + minutes + " minutes, and " + seconds + " seconds`");
    	}
    	else if(s.startsWith("poe ")){
    		System.out.println("test");
    		String query = s.substring(4);
    		String filename = query.replaceAll("\\W+", "");
    		try {
    			 ProcessBuilder builder = new ProcessBuilder("phantomjs", "Rainbot poe.js", query , filename);
    			        builder.redirectErrorStream(true);
    			        Process p = builder.start();
    			        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
    			        String line;
    			        while (true) {
    			            line = r.readLine();
    			            if (line == null) { break; }
    			            System.out.println(line);
    			        }
				File f = new File(filename + ".png");
				File f2 = new File(filename +"2.png");
				File f3 = new File(filename +"3.png");
				if(f.isFile() || f2.isFile()){
					System.out.println("exists");
					if(message != null){
						message.reply("<http://pathofexile.gamepedia.com/" + query.replace(" ", "_") + ">");
						message.getChannelReceiver().sendFile(f);
						message.getChannelReceiver().sendFile(f2);
						message.getChannelReceiver().sendFile(f3);
					}else{
						channel.sendMessage("<http://pathofexile.gamepedia.com/" + query.replace(" ", "_") + ">");
						channel.sendFile(f);
						channel.sendFile(f2);
						channel.sendFile(f3);
					}
					Timer deleteTimer = new Timer();
					deleteTimer.schedule(new TimerTask(){
		    			public void run(){
							f.delete();
							f2.delete();
							f3.delete();
		    			}
		    		}, 10000);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
    		return null;
    		/*
    		if message.content.startswith('!poe '):
    	        query = message.content[5:]
    	        print(query)
    	        filename = re.sub(r'\W+', '', query)
    	        os.system('phantomjs "Rainbot poe.js" "' + query + '" ' + filename)
    	        if(os.path.isfile(filename+'.png') or os.path.isfile(filename+'2.png')):
    	            yield from client.send_message(message.channel, "<http://pathofexile.gamepedia.com/" + query.replace(" ", "_") + ">")
    	            try:
    	                yield from client.send_file(message.channel, filename+'.png')
    	            except:
    	                pass
    	            try:
    	                yield from client.send_file(message.channel, filename+'2.png')
    	            except:
    	                pass
    	            try:
    	                yield from client.send_file(message.channel, filename+'3.png')
    	            except:
    	                pass
    	            yield from asyncio.sleep(10)
    	            try:
    	                os.remove(filename+'.png')
    	            except:
    	                pass
    	            try:
    	                os.remove(filename+'2.png')
    	            except:
    	                pass
    	            try:
    	                os.remove(filename+'3.png')
    	            except:
    	                pass
    	        else:
    	            yield from client.send_message(message.channel, "Error, make sure your query is spelled correctly (case-sensitive)")      
    	            */
    	}
		
		
		
		//parse if js if not a command
        else if(jsEnabled){
			return parseAsJs(command);
		}
		return null;
	}
	
	public MessageLogger getDailyLogger(String logName){
		Calendar calendar = Calendar.getInstance();
		Date currentTime = calendar.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if(sdf.format(currentTime).equals(logName)){
			return rainbot.createListener.messageLogger;
		}
		else return new MessageLogger(rainbot, logName); //makes new logger if loading old stuff
	}
	
	public void receiveMessage(Message message){
		if(!message.getAuthor().isBot() && isActive){
    		if(!(requireMention) || message.getMentions().contains(rainbot.getImplDiscordAPI().getYourself())){ // && message.getMentions().size() == 1
    			String command = "";
    			if(message.getMentions().contains(rainbot.getImplDiscordAPI().getYourself())){
    				String mentionTag = rainbot.getImplDiscordAPI().getYourself().getMentionTag();
    				if(!message.getContent().contains(mentionTag)){
    					mentionTag = mentionTag.substring(0, 2) + "!" + mentionTag.substring(2);
    				}
    				command = message.getContent().substring(message.getContent().indexOf(mentionTag) + mentionTag.length()+1);
    			}else{
    				command = message.getContent();
    			}	
    			Object result = parseCommand(command, message.getChannelReceiver(), message.getAuthor(), message);
    			if(result != null){
    				queueMessage(message.getChannelReceiver(), result);
    			}
    		}
		}
	}
	
	public String parseAsJs(String command){
		
		String result = "";
		ScriptRunner scriptRunner = new ScriptRunner(command, engine);
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
