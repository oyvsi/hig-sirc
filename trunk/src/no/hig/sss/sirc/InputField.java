package no.hig.sss.sirc;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


import javax.swing.JTextField;

/**
 * This class holds the textfield where the user inputs commands and messages.
 * 
 * @author Oyvind Sigerstad, Nils Slaaen, Bjorn-Erik Strand
 *
 */

public class InputField extends JTextField {
	private ConnectionManagement connectionManagement;
	private int type;	// Type of tab. Defined in TabComponent
	private String identifier;
	
	/**
	 * Constructor
	 * 
	 * @param type - The type of window the inputfield belongs to
	 * @param identifier - The name of the channel, or nickname of user in PM 
	 */
		
	public InputField(int type, String identifier) {
		super();
		connectionManagement = sIRC.conManagement;
		this.type = type;
		this.identifier = identifier;
		
		// Listen for user pressing enter.
		this.addKeyListener(new KeyAdapter() {
	           public void keyReleased(KeyEvent e) {
	               if(e.getKeyCode() == KeyEvent.VK_ENTER ) {
	        	   JTextField textField = (JTextField) e.getSource();
	                String text = textField.getText();
	                textField.setText("");
	                parseInput(text);
	               }
	           }
		});
	}
	/**
	 * Sets the indentifier
	 * 
	 * @param identifier - Name of channel or user
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	/**
	 * Helper function to create string from array split on space 
	 * 
	 * @param line - The array with text 
	 * @param startAt - The index to start at
	 * @return
	 */
	private String restLine(String[] line, int startAt) {
		String msg = "";
		
		for(int i = startAt; i < line.length; i++)
			msg = msg.concat(line[i] + " ");
		msg = msg.substring(0, msg.length()-1);	// Remove last space
		return msg;
	}
	
	/**
	 * Parses input for commands. If not a command it's sent as chat message.
	 * 
	 * @param text - Input text to parse
	 */
	public void parseInput(String text) {
		if(text.length() > 0 && text.charAt(0) == '/') {	// commands
			try {
				String[] line = text.split("\\s+");	// Split input on space
				String cmd = line[0].toLowerCase().substring(1);
					
				if(cmd.equals("join") || cmd.equals("j")) { // Join a channel
					if(line.length == 2)
						connectionManagement.joinChannel(line[1]);
				}
				else if(cmd.equals("connect")) { // Connect to a irc-server
						String nick = sIRC.options.getNick();
						if(line.length == 2) {
							if(nick.length() > 2)
								connectionManagement.connect(nick, line[1]);
							else
								sIRC.tabContainer.consoleMsg(sIRC.i18n.getStr("error.noNick"));
						}
				}
				else if(cmd.equals("msg")) {	// Private messages
					if(line.length > 2) {
						connectionManagement.privMsg(line[1], restLine(line, 2));
					}
				}
				else if(cmd.equals("wc")) {	 // Close window
					String partMsg = null;
					if(line.length > 1)
						partMsg = restLine(line, 1);
					connectionManagement.closeChat(identifier, type, partMsg);
				}

				else if(cmd.equals("disconnect") || cmd.equals("quit")) {	// Close server connection
					String quitMsg = null;
					if(line.length > 1)
						quitMsg = restLine(line, 1);
					connectionManagement.disConnect(quitMsg);
				}
				else if(cmd.equals("nick")) {	// Nick change
					if(line.length == 2) {	
						String newNick = restLine(line, 1);			
						if(connectionManagement.validateNick(newNick))
							connectionManagement.changeNick(newNick);
					}
				}
				
				else if(cmd.equals("me")) {	// Action msg
					if(line.length > 1) {
						String actionMsg = restLine(line, 1);
						connectionManagement.actionMsg(identifier, actionMsg, type);
					}
				}
				
				else if(cmd.equals("topic")) { // Set topic
					if(type == TabComponent.CHANNEL && line.length > 1) {
						String topicMsg = restLine(line, 1);
						connectionManagement.setTopic(identifier, topicMsg);
					}
				}
				
				else if(cmd.equals("away")) { // Set or unset away
					if(line.length > 1) { // We have argument, set away message
						String awayMsg = restLine(line, 1);
						connectionManagement.away(awayMsg);
					} else { // No argument. Unset away.
						connectionManagement.away(null);
					}
				}
					// Input starts with /, but we haven't implemented the command. Give error.
				else {
					sIRC.tabContainer.consoleMsg(sIRC.i18n.getStr("error.unknownCommand"));
				}
			} catch (Exception e) {	// Split failed.
				e.printStackTrace();
			}
		} else {	// Just normal chat
			if(type == TabComponent.CHANNEL)
				connectionManagement.channelMsg(identifier, text);
			else
				connectionManagement.privMsg(identifier, text);		
		}	
	}	 
}
