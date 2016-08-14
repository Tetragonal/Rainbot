package window;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.Server;
import de.btobastian.javacord.entities.User;
import discordbot.Rainbot;
import discordbot.RainbotProperties;

public class Window extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField tokenField;
	private Rainbot rainbot = new Rainbot(this);
	private static RainbotProperties rainbotProperties = new RainbotProperties();
	
	private JProgressBar connectedBar;
	private JLabel lblUser;
	private JComboBox<String> textChannelComboBox;
	private JComboBox<String> serverComboBox;
	private JToggleButton btnConnect;
	public JCheckBox checkBoxSaveProperties;
	private JCheckBox messageProcessorCheckBox;
	private JCheckBox editListenerCheckBox;
	private JCheckBox deleteListenerCheckBox;
	private JCheckBox requireMentionCheckBox;
	private JCheckBox jsCheckBox;
	private JTextArea consoleTextArea;
	private JTextArea sendTextArea;
	private JCheckBox sendAsCommandCheckBox;
	private DefaultListModel<String> userListModel;
	
	private boolean loadFlag = false;
	
    static WindowTraversalPolicy newPolicy;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Window frame = new Window();
					frame.setVisible(true);
					if(rainbotProperties.getProperty("saveProperties") != null && rainbotProperties.getProperty("saveProperties").equals("true")){
						frame.getProperties();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * Create the frame.
	 */
	public Window() {
		setTitle("Rainbot Discord client");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1024, 768);
		
		Runtime.getRuntime().addShutdownHook(new WindowShutdown(this));
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
		
		consoleTextArea = new JTextArea();
		consoleTextArea.setMargin(new Insets(-10,10,0,10));
		consoleTextArea.setEditable(false);
		JScrollPane consoleScrollPane = new JScrollPane(consoleTextArea);	
		consoleScrollPane.setBounds(41, 36, 756, 519);
		consoleScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		consoleScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        contentPane.add(consoleScrollPane);
		
		tokenField = new JTextField();
		tokenField.setBounds(41, 582, 191, 20);
		contentPane.add(tokenField);
		tokenField.setColumns(10);
		
		JLabel lblToken = new JLabel("Token");
		lblToken.setBounds(42, 566, 46, 14);
		contentPane.add(lblToken);
		
		messageProcessorCheckBox = new JCheckBox("Enable commands");
		messageProcessorCheckBox.setBounds(817, 66, 181, 23);
		messageProcessorCheckBox.addItemListener(new ItemListener() {
		    public void itemStateChanged(ItemEvent e) {
		        if(e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
		            rainbot.createListener.messageProcessor.isActive = true;
		            if(rainbot.getImplDiscordAPI()!= null){
		            	rainbot.getImplDiscordAPI().setIdle(false);
		            }
		        } else {//checkbox has been deselected
		            rainbot.createListener.messageProcessor.isActive = false;
		            if(rainbot.getImplDiscordAPI()!= null){
		            	rainbot.getImplDiscordAPI().setIdle(true);
		            }
		        };
		    }
		});
		contentPane.add(messageProcessorCheckBox);
		
		JLabel lblOptions = new JLabel("Options");
		lblOptions.setBounds(884, 45, 46, 14);
		contentPane.add(lblOptions);
		
		deleteListenerCheckBox = new JCheckBox("Notify on delete");
		deleteListenerCheckBox.setBounds(817, 168, 181, 23);
		deleteListenerCheckBox.addItemListener(new ItemListener() {
		    public void itemStateChanged(ItemEvent e) {
		        if(e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
		            rainbot.deleteListener.isActive = true;
		        } else {//checkbox has been deselected
		            rainbot.deleteListener.isActive = false;
		        };
		    }
		});
		contentPane.add(deleteListenerCheckBox);
		
		jsCheckBox = new JCheckBox("Enable Javascript parser");
		jsCheckBox.setBounds(837, 92, 181, 23);
		jsCheckBox.addItemListener(new ItemListener() {
		    public void itemStateChanged(ItemEvent e) {
		        if(e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
		            rainbot.createListener.messageProcessor.jsEnabled = true;
		        } else {//checkbox has been deselected
		            rainbot.createListener.messageProcessor.jsEnabled = false;
		        };
		    }
		});
		contentPane.add(jsCheckBox);
		
		editListenerCheckBox = new JCheckBox("Notify on edit");
		editListenerCheckBox.setBounds(817, 142, 181, 23);
		editListenerCheckBox.addItemListener(new ItemListener() {
		    public void itemStateChanged(ItemEvent e) {
		        if(e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
		            rainbot.editListener.isActive = true;
		        } else {//checkbox has been deselected
		            rainbot.editListener.isActive = false;
		        };
		    }
		});
		contentPane.add(editListenerCheckBox);
		
		connectedBar = new JProgressBar();
		connectedBar.setBounds(85, 619, 100, 14);
		connectedBar.setForeground(Color.green);
		connectedBar.setBackground(Color.red);
		contentPane.add(connectedBar);
		
		btnConnect = new JToggleButton("Connect");
		btnConnect.setBounds(85, 633, 100, 30);
		btnConnect.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ev) {
				if(ev.getStateChange()==ItemEvent.SELECTED){
					rainbot.connect(tokenField.getText());
				}else if(ev.getStateChange()==ItemEvent.DESELECTED){
					rainbot.disconnect();
				}
			}
		});
		contentPane.add(btnConnect);
				
		
        serverComboBox = new JComboBox<String>();
		serverComboBox.setBounds(807, 362, 191, 20);
		serverComboBox.addItemListener(new ItemListener () {
		    public void itemStateChanged(ItemEvent event) {
		       if (event.getStateChange() == ItemEvent.SELECTED) {
		  		  addToConsoleLog("Joined server " + getCurrentServer());
			      updateChannelComboBox(getCurrentServer());
			      updateUserList(getCurrentServer());
		       }
		    } 
		});
		contentPane.add(serverComboBox);
		
		JLabel lblSendMessage = new JLabel("Send message");
		lblSendMessage.setBounds(270, 566, 117, 20);
		contentPane.add(lblSendMessage);
		
		
		sendTextArea = new JTextArea();		
		sendTextArea.setMargin(new Insets(2,5,5,5));
		sendTextArea.addKeyListener(new KeyListener(){
		    public void keyPressed(KeyEvent e) {
		        if (e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown()) {
		        	sendMessage();
		        	sendTextArea.setText(null);
		        	e.consume();
		        }
		        else if (e.getKeyCode() == KeyEvent.VK_ENTER && e.isShiftDown()) {
		        	sendTextArea.setText(sendTextArea.getText()+ "\n");
		        }else if (e.getKeyCode() == KeyEvent.VK_ENTER){
//		        	sendTextArea.setText(null);
		        	 e.consume();
		        }
		    }
			public void keyReleased(KeyEvent e) {}
			public void keyTyped(KeyEvent arg0) {}
		});
		JScrollPane sendTextScrollPane = new JScrollPane (sendTextArea);
		sendTextScrollPane.setBounds(270, 589, 526, 84);
	    sendTextScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        sendTextScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        contentPane.add(sendTextScrollPane);
		
		
		JButton btnSend = new JButton("Send");
		btnSend.setBounds(706, 674, 89, 23);
		btnSend.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	sendMessage();
	            sendTextArea.requestFocusInWindow();            
		    }
		});
		contentPane.add(btnSend);
		
		lblUser = new JLabel("Not logged in");
		lblUser.setBounds(41, 11, 756, 14);
		contentPane.add(lblUser);
		
		JLabel lblSelectServer = new JLabel("Select Server");
		lblSelectServer.setBounds(807, 345, 191, 14);
		contentPane.add(lblSelectServer);
		
        textChannelComboBox = new JComboBox<String>();
		textChannelComboBox.setBounds(807, 410, 191, 20);
		textChannelComboBox.addItemListener(new ItemListener () {			
		    public void itemStateChanged(ItemEvent event) {
		       if (event.getStateChange() == ItemEvent.SELECTED && loadFlag == false) {
		    	   addToConsoleLog("Joined channel: " + getCurrentChannel());
		       }
		    } 
		});
		contentPane.add(textChannelComboBox);
		
		JLabel lblSelectTextChannel = new JLabel("Select Text Channel");
		lblSelectTextChannel.setBounds(807, 393, 191, 14);
		contentPane.add(lblSelectTextChannel);
		
		JButton btnUpdateLists = new JButton("Refresh");
		btnUpdateLists.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addToConsoleLog("Server list refreshed.");
				updateServerComboBox();
			}
		});
		btnUpdateLists.setBounds(910, 331, 89, 23);
		contentPane.add(btnUpdateLists);
		
		requireMentionCheckBox = new JCheckBox("Require mention");
		requireMentionCheckBox.setSelected(true);
		requireMentionCheckBox.setBounds(837, 116, 181, 23);
		requireMentionCheckBox.addItemListener(new ItemListener() {
		    public void itemStateChanged(ItemEvent e) {
		        if(e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
		            rainbot.createListener.messageProcessor.requireMention = true;
		        } else {//checkbox has been deselected
		            rainbot.createListener.messageProcessor.requireMention = false;
		        };
		    }
		});
		contentPane.add(requireMentionCheckBox);
		
		checkBoxSaveProperties = new JCheckBox("Save Preferences");
		checkBoxSaveProperties.setBounds(75, 674, 129, 23);
		checkBoxSaveProperties.addItemListener(new ItemListener() {
		    public void itemStateChanged(ItemEvent e) {
		        if(e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
		            setProperties();
		        } else {//checkbox has been deselected
		            clearProperties();
		        };
		    }
		});
		contentPane.add(checkBoxSaveProperties);
		
		sendAsCommandCheckBox = new JCheckBox("Send as command");
		sendAsCommandCheckBox.setBounds(269, 673, 149, 23);
		contentPane.add(sendAsCommandCheckBox);
		
		JLabel lblUserList = new JLabel("User List");
		lblUserList.setBounds(807, 442, 78, 14);
		contentPane.add(lblUserList);
		
		userListModel = new DefaultListModel<String>();
		JList<String> userList = new JList<String>(userListModel);
		userList.addMouseListener( new MouseAdapter()
		{
		    public void mousePressed(MouseEvent e)
		    {
		        if ( SwingUtilities.isRightMouseButton(e) )
		        {
		            JList<?> list = (JList<?>)e.getSource();
		            int row = list.locationToIndex(e.getPoint());
		            list.setSelectedIndex(row);
		            
		            sendTextArea.setText(sendTextArea.getText() + ((User) getCurrentServer().getMembers().toArray()[row]).getMentionTag());
		            sendTextArea.requestFocusInWindow();
		        }
		    }

		});
		JScrollPane scrollPane = new JScrollPane(userList);
		scrollPane.setBounds(807, 459, 191, 196);
		contentPane.add(scrollPane);
		
		Vector<Component> order = new Vector<Component>(7);
	    order.add(tokenField);
	    newPolicy = new WindowTraversalPolicy(order);
	    setFocusTraversalPolicy(newPolicy);
		
		
	}
	
	public void setProgressBar(int percent) {
		connectedBar.setValue(percent);
	}
	
	public void setLblUser(String text) {
		lblUser.setText(text);
	}
	
	public void updateServerComboBox(){
		//clear list
		serverComboBox.removeAllItems();
		//populate list
		if(rainbot.getImplDiscordAPI() != null){
	    	for(Server s : rainbot.getImplDiscordAPI().getServers()){
	    		serverComboBox.addItem(s.getName());
	    	}
		}else{
    		updateChannelComboBox(null);
    		updateUserList(null);
    	}
	}
	
	public void updateChannelComboBox(Server s){
		loadFlag = true; //don't output system log while adding channels
		//clear list
		textChannelComboBox.removeAllItems();
		//populate list
		if(s != null){
			for(Channel c : s.getChannels()){
				textChannelComboBox.addItem(c.getName());
			}
			addToConsoleLog("Joined channel: " + getCurrentChannel());
		}
		loadFlag = false;
		
	}
	
	public void updateUserList(Server s){
		//clear list
		userListModel.clear();
		//populate list
		if(s != null){
			for(User u : s.getMembers()){
				String info = u.getName() + "#" + u.getDiscriminator();
				if(u.isBot()){
					info = info + " [Bot]";
				}
				userListModel.addElement(info);
			}
		}
	}
	
	public Server getCurrentServer(){
		ArrayList<Server> serverList = new ArrayList<Server>();
		if(rainbot.getImplDiscordAPI() != null){
		    for(Server s : rainbot.getImplDiscordAPI().getServers()){
			    serverList.add(s);
		    }
		    if(serverComboBox.getSelectedIndex() != -1){
		    	return serverList.get(serverComboBox.getSelectedIndex());
		    }
		}
		return null;
	}
	
	public Channel getCurrentChannel(){
		ArrayList<Channel> channelList = new ArrayList<Channel>();
		if(getCurrentServer() != null){
			for(Channel c : getCurrentServer().getChannels()){
				channelList.add(c);
			}
			return channelList.get(textChannelComboBox.getSelectedIndex());
		}
		return null;
	}
	
	public void setBtnConnectToggle(boolean toggle){
		btnConnect.setSelected(toggle);
	}
	
	public void getProperties(){
		tokenField.setText(rainbotProperties.getProperty("Token"));
		
		messageProcessorCheckBox.setSelected(Boolean.valueOf(rainbotProperties.getProperty("Enable Commands")));
		jsCheckBox.setSelected(Boolean.valueOf(rainbotProperties.getProperty("Enable Javascript parser")));
		requireMentionCheckBox.setSelected(Boolean.valueOf(rainbotProperties.getProperty("Require mention")));
		editListenerCheckBox.setSelected(Boolean.valueOf(rainbotProperties.getProperty("Notify on edit")));
		deleteListenerCheckBox.setSelected(Boolean.valueOf(rainbotProperties.getProperty("Notify on delete")));
		
		
		
		checkBoxSaveProperties.setSelected(Boolean.valueOf(rainbotProperties.getProperty("saveProperties")));
	}
	
	public void setProperties(){
		rainbotProperties.setProperty("saveProperties", "true");
		
		rainbotProperties.setProperty("Token", tokenField.getText());
		
		rainbotProperties.setProperty("Enable Commands", Boolean.toString(messageProcessorCheckBox.isSelected()));
		rainbotProperties.setProperty("Enable Javascript parser", Boolean.toString(jsCheckBox.isSelected()));
		rainbotProperties.setProperty("Require mention", Boolean.toString(requireMentionCheckBox.isSelected()));
		rainbotProperties.setProperty("Notify on edit", Boolean.toString(editListenerCheckBox.isSelected()));
		rainbotProperties.setProperty("Notify on delete", Boolean.toString(deleteListenerCheckBox.isSelected()));
	}
	
	public void clearProperties(){
		rainbotProperties.setProperty("saveProperties", "false");

		rainbotProperties.clearProperty("Token");
		
		rainbotProperties.clearProperty("Enable Commands");
		rainbotProperties.clearProperty("Enable Javascript parser");
		rainbotProperties.clearProperty("Require mention");
		rainbotProperties.clearProperty("Notify on edit");
		rainbotProperties.clearProperty("Notify on delete");
	}
	
	public void addToConsoleLog(String text){
		String s = consoleTextArea.getText();
		while(s.length() > 50000){
			s = s.substring(s.indexOf("\n", 1));
		}
		consoleTextArea.setText(s + "\n" + text);
		consoleTextArea.setCaretPosition(consoleTextArea.getDocument().getLength());
		try {
			saveToLog(text);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveToLog(String text) throws Exception {
	   FileOutputStream out = new FileOutputStream("console.log", true);
	   text = text + "\r\n";
	   out.write(text.getBytes());
	   out.close();
	} 	
	
	public void sendMessage(){
    	if(getCurrentChannel() != null && sendTextArea.getText() != ""){
    		if(sendAsCommandCheckBox.isSelected()){
    			String result = rainbot.messageProcessor.parseCommand(sendTextArea.getText(), getCurrentChannel(), rainbot.getImplDiscordAPI().getYourself(), null);
    			if(result != null){
    				rainbot.messageProcessor.queueMessage(getCurrentChannel(), "`" + sendTextArea.getText() + "`\n\n" + result);
    			}
    		}else{
        		rainbot.messageProcessor.queueMessage(getCurrentChannel(), sendTextArea.getText());
    		}
        	sendTextArea.setText(null);
    	}
	}	
	
	public Rainbot getRainbot(){
		return rainbot;
	}
}