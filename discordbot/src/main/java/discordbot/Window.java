package discordbot;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileOutputStream;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.Server;

public class Window extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField usernameField;
	private JTextField tokenField;
	private JPasswordField passwordField;
	private Rainbot rainbot = new Rainbot(this);
	private static RainbotProperties rainbotProperties = new RainbotProperties();
	
	private JProgressBar connectedBar;
	private JLabel lblUser;
	private JComboBox<Channel> textChannelComboBox;
	private JComboBox<Server> serverComboBox;
	private JToggleButton btnConnect;
	private JCheckBox checkBoxSaveProperties;
	private JCheckBox createListenerCheckBox;
	private JCheckBox editListenerCheckBox;
	private JCheckBox deleteListenerCheckBox;
	private JCheckBox requireMentionCheckBox;
	private JCheckBox jsCheckBox;
	private JTextArea consoleTextArea;
	
	private boolean loadFlag = false;

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
		consoleTextArea.setMargin(new Insets(-10,10,10,10));
		consoleTextArea.setEditable(false);
		JScrollPane consoleScrollPane = new JScrollPane(consoleTextArea);	
		consoleScrollPane.setBounds(41, 36, 756, 519);
		consoleScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		consoleScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        contentPane.add(consoleScrollPane);

		
		usernameField = new JTextField();
		usernameField.setBounds(41, 589, 191, 20);
		contentPane.add(usernameField);
		usernameField.setColumns(10);
		
		tokenField = new JTextField();
		tokenField.setBounds(41, 674, 191, 20);
		contentPane.add(tokenField);
		tokenField.setColumns(10);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(41, 631, 191, 20);
		contentPane.add(passwordField);
		
		JLabel lblUsername = new JLabel("Email");
		lblUsername.setBounds(41, 575, 60, 14);
		contentPane.add(lblUsername);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(41, 613, 60, 14);
		contentPane.add(lblPassword);
		
		JLabel lblToken = new JLabel("Token");
		lblToken.setBounds(41, 659, 46, 14);
		contentPane.add(lblToken);
		
		createListenerCheckBox = new JCheckBox("Enable commands");
		createListenerCheckBox.setBounds(817, 66, 181, 23);
		createListenerCheckBox.addItemListener(new ItemListener() {
		    public void itemStateChanged(ItemEvent e) {
		        if(e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
		            rainbot.createListener.isActive = true;
		        } else {//checkbox has been deselected
		            rainbot.createListener.isActive = false;
		        };
		    }
		});
		contentPane.add(createListenerCheckBox);
		
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
		            rainbot.createListener.jsEnabled = true;
		        } else {//checkbox has been deselected
		            rainbot.createListener.jsEnabled = false;
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
		
		
		UIManager.put("ProgressBar.selectionBackground",Color.WHITE);
		connectedBar = new JProgressBar();
		connectedBar.setBounds(262, 613, 100, 14);
		connectedBar.setForeground(Color.green);
		connectedBar.setBackground(Color.red);
		contentPane.add(connectedBar);
		
		btnConnect = new JToggleButton("Connect");
		btnConnect.setBounds(262, 626, 100, 30);
		btnConnect.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ev) {
				if(ev.getStateChange()==ItemEvent.SELECTED){
					rainbot.connect(usernameField.getText(), passwordField.getPassword().toString(), tokenField.getText());
				}else if(ev.getStateChange()==ItemEvent.DESELECTED){
					rainbot.disconnect();
				}
			}
		});
		contentPane.add(btnConnect);
				
		
        serverComboBox = new JComboBox<Server>();
		serverComboBox.setBounds(807, 420, 191, 20);
		serverComboBox.addItemListener(new ItemListener () {
		    public void itemStateChanged(ItemEvent event) {
		       if (event.getStateChange() == ItemEvent.SELECTED) {
		  		  addToConsoleLog("Joined server " + getCurrentServer());
		          updateChannelComboBox(getCurrentServer());
		       }
		    } 
		});
		contentPane.add(serverComboBox);
		
		JLabel lblSendMessage = new JLabel("Send message");
		lblSendMessage.setBounds(451, 566, 117, 20);
		contentPane.add(lblSendMessage);
		
		final JTextArea sendTextArea = new JTextArea();		
        DefaultCaret caret = (DefaultCaret)sendTextArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane sendTextScrollPane = new JScrollPane (sendTextArea);	
		sendTextScrollPane.setBounds(451, 589, 366, 84);
	    sendTextScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        sendTextScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        contentPane.add(sendTextScrollPane);
		
		
		JButton btnSend = new JButton("Send");
		btnSend.setBounds(728, 673, 89, 23);
		btnSend.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	if(getCurrentChannel() != null){
	    			getCurrentChannel().sendMessage(sendTextArea.getText()); //callback
	            	sendTextArea.setText(null);
		    	}
		    }
		});
		contentPane.add(btnSend);
		
		lblUser = new JLabel("Not logged in");
		lblUser.setBounds(41, 11, 756, 14);
		contentPane.add(lblUser);
		
		JLabel lblSelectServer = new JLabel("Select Server");
		lblSelectServer.setBounds(807, 397, 191, 14);
		contentPane.add(lblSelectServer);
		
        textChannelComboBox = new JComboBox<Channel>();
		textChannelComboBox.setBounds(807, 468, 191, 20);
		textChannelComboBox.addItemListener(new ItemListener () {			
		    public void itemStateChanged(ItemEvent event) {
		       if (event.getStateChange() == ItemEvent.SELECTED && loadFlag == false) {
		    	   addToConsoleLog("Joined channel: " + getCurrentChannel());
		       }
		    } 
		});
		contentPane.add(textChannelComboBox);
		
		JLabel lblSelectTextChannel = new JLabel("Select Text Channel");
		lblSelectTextChannel.setBounds(807, 451, 191, 14);
		contentPane.add(lblSelectTextChannel);
		
		JButton btnUpdateLists = new JButton("Refresh");
		btnUpdateLists.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addToConsoleLog("Server list refreshed.");
				updateServerComboBox();
			}
		});
		btnUpdateLists.setBounds(860, 517, 89, 23);
		contentPane.add(btnUpdateLists);
		
		requireMentionCheckBox = new JCheckBox("Require mention");
		requireMentionCheckBox.setSelected(true);
		requireMentionCheckBox.setBounds(837, 116, 181, 23);
		requireMentionCheckBox.addItemListener(new ItemListener() {
		    public void itemStateChanged(ItemEvent e) {
		        if(e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
		            rainbot.createListener.requireMention = true;
		        } else {//checkbox has been deselected
		            rainbot.createListener.requireMention = false;
		        };
		    }
		});
		contentPane.add(requireMentionCheckBox);
		
		checkBoxSaveProperties = new JCheckBox("Save Preferences");
		checkBoxSaveProperties.setBounds(252, 673, 129, 23);
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
	    		serverComboBox.addItem(s);
	    	}
		}else{
    		updateChannelComboBox(null);
    	}
	}
	
	public void updateChannelComboBox(Server s){
		loadFlag = true; //don't output system logs while updating
		//clear list
		textChannelComboBox.removeAllItems();
		//populate list
		if(s != null){
			for(Channel c : s.getChannels()){
				textChannelComboBox.addItem(c);
			}
			addToConsoleLog("Joined channel: " + getCurrentChannel());
		}
		loadFlag = false;
		
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
		usernameField.setText(rainbotProperties.getProperty("Email"));
		passwordField.setText(rainbotProperties.getProperty("Pw"));
		tokenField.setText(rainbotProperties.getProperty("Token"));
		
		createListenerCheckBox.setSelected(Boolean.valueOf(rainbotProperties.getProperty("Enable Commands")));
		jsCheckBox.setSelected(Boolean.valueOf(rainbotProperties.getProperty("Enable Javascript parser")));
		requireMentionCheckBox.setSelected(Boolean.valueOf(rainbotProperties.getProperty("Require mention")));
		editListenerCheckBox.setSelected(Boolean.valueOf(rainbotProperties.getProperty("Notify on edit")));
		deleteListenerCheckBox.setSelected(Boolean.valueOf(rainbotProperties.getProperty("Notify on delete")));
		
		
		
		checkBoxSaveProperties.setSelected(Boolean.valueOf(rainbotProperties.getProperty("saveProperties")));
	}
	
	public void setProperties(){
		rainbotProperties.setProperty("saveProperties", "true");
		
		rainbotProperties.setProperty("Email", usernameField.getText());
		rainbotProperties.setProperty("Pw", passwordField.getPassword().toString());
		rainbotProperties.setProperty("Token", tokenField.getText());
		
		rainbotProperties.setProperty("Enable Commands", Boolean.toString(createListenerCheckBox.isSelected()));
		rainbotProperties.setProperty("Enable Javascript parser", Boolean.toString(jsCheckBox.isSelected()));
		rainbotProperties.setProperty("Require mention", Boolean.toString(requireMentionCheckBox.isSelected()));
		rainbotProperties.setProperty("Notify on edit", Boolean.toString(editListenerCheckBox.isSelected()));
		rainbotProperties.setProperty("Notify on delete", Boolean.toString(deleteListenerCheckBox.isSelected()));
	}
	
	public void clearProperties(){
		rainbotProperties.setProperty("saveProperties", "false");
		
		rainbotProperties.clearProperty("Email");
		rainbotProperties.clearProperty("Pw");
		rainbotProperties.clearProperty("Token");
		
		rainbotProperties.clearProperty("Enable Commands");
		rainbotProperties.clearProperty("Enable Javascript parser");
		rainbotProperties.clearProperty("Require mention");
		rainbotProperties.clearProperty("Notify on edit");
		rainbotProperties.clearProperty("Notify on delete");
	}
	
	public void addToConsoleLog(String text){
		consoleTextArea.setText(consoleTextArea.getText() + "\n" + text);
		try {
			saveToFile(text);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void saveToFile(String text) throws Exception {
	   FileOutputStream out = new FileOutputStream("console.log", true);
	   text = text + "\n";
	   out.write(text.getBytes());
	   out.close();
} 
	
	
//	public void saveToFile(JTextArea textArea) throws Exception {
//		   FileOutputStream out = new FileOutputStream("console.log", true);
//		   out.write(textArea.getText().getBytes());
//		   out.close();
//	} 
	
	
}