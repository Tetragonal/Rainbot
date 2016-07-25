package discordbot;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
	
	private JProgressBar connectedBar;
	private JLabel lblUser;
	private JComboBox<Channel> textChannelComboBox;
	private JComboBox<Server> serverComboBox;
	private JToggleButton btnConnect;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Window frame = new Window();
					frame.setVisible(true);
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
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JTextArea consoleTextArea = new JTextArea();
		consoleTextArea.setEditable(false);
		consoleTextArea.setBounds(41, 36, 756, 519);
		contentPane.add(consoleTextArea);
		
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
		
		JCheckBox createListenerCheckBox = new JCheckBox("Enable commands");
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
		
		JCheckBox deleteListenerCheckBox = new JCheckBox("Notify on delete");
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
		
		JCheckBox jsCheckBox = new JCheckBox("Enable Javascript parser");
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
		
		JCheckBox editListenerCheckBox = new JCheckBox("Notify on edit");
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
		          updateChannelComboBox(getCurrentServer());
		       }
		    } 
		});
		contentPane.add(serverComboBox);
		
		JLabel lblSendMessage = new JLabel("Send message");
		lblSendMessage.setBounds(451, 566, 117, 20);
		contentPane.add(lblSendMessage);
		
		final JTextArea sendTextArea = new JTextArea();
//		sendTextArea.setBounds(451, 589, 366, 84);
//		contentPane.add(sendTextArea);
		
		JScrollPane scroll = new JScrollPane (sendTextArea);
		
		scroll.setBounds(451, 589, 366, 84);
	    scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        contentPane.add(scroll);
		
		
		JButton btnSend = new JButton("Send");
		btnSend.setBounds(728, 673, 89, 23);
		btnSend.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	if(getCurrentChannel() != null){
	    			getCurrentChannel().sendMessage(sendTextArea.getText());
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
		contentPane.add(textChannelComboBox);
		
		JLabel lblSelectTextChannel = new JLabel("Select Text Channel");
		lblSelectTextChannel.setBounds(807, 451, 191, 14);
		contentPane.add(lblSelectTextChannel);
		
		JButton btnUpdateLists = new JButton("Refresh");
		btnUpdateLists.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				updateServerComboBox();
			}
		});
		btnUpdateLists.setBounds(860, 517, 89, 23);
		contentPane.add(btnUpdateLists);
		
		JCheckBox requireMentionCheckBox = new JCheckBox("Require mention");
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
		
		JCheckBox chckbxSavePreferences = new JCheckBox("Save Preferences");
		chckbxSavePreferences.setBounds(252, 673, 129, 23);
		contentPane.add(chckbxSavePreferences);
	}
	
	public void setProgressBar(int percent) {
		connectedBar.setValue(percent);
	}
	
	public void setLblUser(String text) {
		lblUser.setText(text);
	}
	
	public void updateServerComboBox(){
		//clear list
		while(serverComboBox.getItemCount() > 0){
			serverComboBox.removeItemAt(0);
		}
		//populate list
		if(rainbot.getImplDiscordAPI() != null){
	    	for(Server s : rainbot.getImplDiscordAPI().getServers()){
	    		serverComboBox.addItem(s);;
	    	}
	    	if(serverComboBox.getItemCount() > 0){
	    		updateChannelComboBox(getCurrentServer());
	    	}else{
	    		updateChannelComboBox(null);
	    	}
		}else{
    		updateChannelComboBox(null);
    	}
	}
	
	public void updateChannelComboBox(Server s){
		//clear list
		while(textChannelComboBox.getItemCount() > 0){
			textChannelComboBox.removeItemAt(0);
		}
		//populate list
		if(s != null){
			for(Channel c : s.getChannels()){
				textChannelComboBox.addItem(c);
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
}