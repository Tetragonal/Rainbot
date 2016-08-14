package message;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.Server;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.message.MessageHistory;
import discordbot.Rainbot;

public class DailyLogger {
	public ArrayList<MessageData> messageList = new ArrayList<MessageData>();
	
	public Date currentDate;
	public SimpleDateFormat sdf;
	
	private Rainbot rainbot;
	
	public DailyLogger(Rainbot rainbot){
		this.rainbot = rainbot;
		Calendar calendar = Calendar.getInstance();
		currentDate = calendar.getTime();
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		loadMessageList();
	}
	
	public DailyLogger(Rainbot rainbot, String logName){
		this.rainbot = rainbot;
		Calendar calendar = Calendar.getInstance();
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			calendar.setTime(sdf.parse(logName));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		currentDate = calendar.getTime();
		loadMessageList(logName);
	}
	
	public void addMessage(Message message){
		//create serializable message
		MessageData messageData = new MessageData(message);
		//add to today's list
		messageList.add(messageData);
	}
	
	public void loadMessageList(){
		loadMessageList(null);
	}
	
	public void loadMessageList(String logName){
		messageList.clear();
		  try {
			  FileInputStream inputFileStream;
			  if(logName == null){
				  inputFileStream = new FileInputStream("./logs/" + sdf.format(currentDate) + ".log");
			  }else{
				  inputFileStream = new FileInputStream("./logs/" + logName + ".log");
			  }
		      ObjectInputStream objectInputStream = new ObjectInputStream(inputFileStream);
		      messageList = (ArrayList<MessageData>)objectInputStream.readObject();
		      objectInputStream.close();
		      inputFileStream.close();
		  } 
		  catch (Exception e)
		  {
			  e.printStackTrace(); 
		  }
		  System.out.println("loaded " + messageList.size() + " messages");
	}
	
	public void saveMessageList(){
		//write today's log to a file
		
    	// Write to disk with FileOutputStream
    	FileOutputStream f_out = null;    	
		try {
			f_out = new FileOutputStream("./logs/" + sdf.format(currentDate) + ".log");
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
    	// Write object with ObjectOutputStream
    	ObjectOutputStream obj_out = null;
		try {
			obj_out = new ObjectOutputStream (f_out);
    	// Write object out to disk
			obj_out.writeObject ( messageList );
			obj_out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *   @return <b>InputStream</b> instances of the term<br>
	 *   <b>int</b> count of the amount of instances
	 */
	public Object[] find(Channel channel, String term) {
		String instances = "\r\n";
		int count = 0;
		for(MessageData messageData : messageList){
			if(messageData.messageContent.contains(term) && channel.getServer().getId().equals(messageData.serverReceiverID)){
				instances = instances + messageData.toString().replace("\n", "\r\n") + "\r\n";
				count++;
			}
		}
		instances = "(" + sdf.format(currentDate) + ") Found " + count + " instances of " + term + " in Server ID " + channel.getServer().getId() + "\r\n\r\n" + instances;
		InputStream stream = new ByteArrayInputStream(instances.getBytes(StandardCharsets.UTF_8));
		return new Object[]{stream, count};		
	}
	
	/**
	 *   @return <b>InputStream</b> log file<br>
	 *   <b>int</b> count of the amount of instances
	 */
	public Object[] getLog(Channel channel){
		String logText = "";
		int logCount = 0;
		for(MessageData messageData : messageList){
			if(channel.getServer().getId().equals(messageData.serverReceiverID)){
				logText = logText + messageData.toString().replace("\n", "\r\n") + "\r\n";
				logCount++;
			}
		}
		logText = sdf.format(currentDate) + " log, Server ID=" + channel.getServer().getId() + "\r\n\r\n" + logText;
		InputStream stream = new ByteArrayInputStream(logText.getBytes(StandardCharsets.UTF_8));
		return new Object[]{stream, logCount};
	}
	
	
	public String getUserActivity(Channel channel){		
    	Calendar calendar = Calendar.getInstance();
    	String activityReport = ""
    			+ "User activity for today in #" + channel.getName() + ":\n\n"
    			+ "```\n"
    			+ String.format("%1$-25s", "Username:") 
    			+ String.format("%1$-8s", "#:") 
    			+ String.format("%1$-10s", "%:")
    			+ "type:"
				+ "\n";
		
		//calendar doesn't convert properly on its own, so using this for now
    	float minutes = 60*calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE);
    	
    	//special case for when printing at exactly midnight
    	if(minutes < 0.05){
    		minutes = 60*24;
    	}
    	
		TreeMap<String, Integer> userActivityMap = new TreeMap<String, Integer>(); //userID, messagesSent
		//create user activity map
		int messageCount = 0;
		for(MessageData messageData : messageList){
			if(messageData.channelReceiverID.equals(channel.getId())){
				if(userActivityMap.containsKey(messageData.authorID)){
					userActivityMap.put(messageData.authorID, userActivityMap.get(messageData.authorID) + 1);
				}else{
					userActivityMap.put(messageData.authorID, 1);
				}
				messageCount++;
			}
		}
		
		DecimalFormat fmt = new DecimalFormat ("0.00");
		//sort users based on messages sent
		Map<String, Integer> sortedMap = sortByComparator(userActivityMap);
		//print stuff based on map		
		for(String userID : sortedMap.keySet()){
			Future<User> futureUser =  rainbot.getImplDiscordAPI().getUserById(userID);
			User user = null;
			try {
				user = futureUser.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			
			activityReport += ""
					+ String.format("%1$-25s", user.getName() + "#" + user.getDiscriminator())
					+ String.format("%1$-8s", userActivityMap.get(userID)) 
					+ String.format("%1$-10s",  fmt.format((100.0*userActivityMap.get(userID)/messageCount)));
			if(user.isBot()){
				 activityReport += "bot";
			}
			activityReport += "\n";
		}
		
		activityReport = activityReport + "\n```\nTotal message count: " + messageCount + " (" + fmt.format(messageCount/minutes) + " messages per minute)\n";
		return activityReport;
		
	}
	
	
	private Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap)
    {
        List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(unsortMap.entrySet());
        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<String, Integer>>()
        {
            public int compare(Entry<String, Integer> o1,
                    Entry<String, Integer> o2)
            {
            		return o2.getValue().compareTo(o1.getValue());
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Entry<String, Integer> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

	
}
