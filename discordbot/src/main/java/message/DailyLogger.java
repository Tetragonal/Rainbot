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
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
import discordbot.Rainbot;

public class DailyLogger {
	
	public ArrayList<MessageData> messageList = new ArrayList<MessageData>();
	
	public Date currentTime;
	private SimpleDateFormat sdf;
	
	private Rainbot rainbot;
	
	public DailyLogger(Rainbot rainbot){
		this.rainbot = rainbot;
		Calendar calendar = Calendar.getInstance();
		currentTime = calendar.getTime();
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		loadMessageList();
	}
	
	public DailyLogger(Rainbot rainbot, String logName){
		this.rainbot = rainbot;
		Calendar calendar = Calendar.getInstance();
		currentTime = calendar.getTime();
		sdf = new SimpleDateFormat("yyyy-MM-dd");
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
		  try  
		  {
			  FileInputStream inputFileStream;
			  if(logName == null){
				  inputFileStream = new FileInputStream("./logs/" + sdf.format(currentTime) + ".log");
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
//			  e.printStackTrace(); 
		  }
		  System.out.println("loaded " + messageList.size() + " messages");
//
	}
	
	public void saveMessageList(){
		//write today's log to a file
		
    	// Write to disk with FileOutputStream
    	FileOutputStream f_out = null;    	
		try {
			f_out = new FileOutputStream("./logs/" + sdf.format(currentTime) + ".log");
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

    	// Write object with ObjectOutputStream
    	ObjectOutputStream obj_out = null;
		try {
			obj_out = new
				ObjectOutputStream (f_out);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

    	// Write object out to disk
    	try {
			obj_out.writeObject ( messageList );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	try {
			obj_out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 *   @return <b>InputStream</b> instances of the term<br>
	 *   <b>int</b> count of the amount of instances
	 */
	public Object[] find(Channel channel, String term) {
		String instances = "";
		int count = 0;
		for(MessageData messageData : messageList){
			if(messageData.messageContent.contains(term) && channel.getServer().getId().equals(messageData.serverReceiverID)){
				instances = instances + messageData.toString() + "\n";
				count++;
			}
		}
		instances = "(" + sdf.format(currentTime) + ") Found " + count + " instances of " + term + " in Server ID " + channel.getServer().getId() + "\n\n" + instances;
		InputStream stream = new ByteArrayInputStream(instances.getBytes(StandardCharsets.UTF_8));
		return new Object[]{stream, count};		
	}
	
	public String getUserActivity(){
		String activityReport = "User activity for today:\n";
		TreeMap<String, Integer> userActivityMap = new TreeMap<String, Integer>(); //userID, messagesSent
		//create user activity map
		for(MessageData messageData : messageList){
			if(userActivityMap.containsKey(messageData.authorID)){
				userActivityMap.put(messageData.authorID, userActivityMap.get(messageData.authorID) + 1);
			}else{
				userActivityMap.put(messageData.authorID, 1);
			}
		}
		
		//sort users based on messages sent
		Map<String, Integer> sortedMap = sortByComparator(userActivityMap);
		//print stuff based on map		
		for(String userID : sortedMap.keySet()){
			Future<User> futureUser =  rainbot.getImplDiscordAPI().getUserById(userID);
			User user = null;
			try {
				user = futureUser.get();
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			activityReport = activityReport + user.getName() + "#" + user.getDiscriminator() + ": " + userActivityMap.get(userID) + "\n";
		}
		return activityReport;
		
	}
	
	
	private Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap)
    {
        List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(unsortMap.entrySet());
		System.out.println("hi4");
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
		System.out.println("hi5");
        return sortedMap;
    }

	
}