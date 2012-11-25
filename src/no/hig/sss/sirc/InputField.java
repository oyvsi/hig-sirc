package no.hig.sss.sirc;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.swing.JTextField;

public class InputField extends JTextField {
	private ConnectionManagement connectionManagement;
	private int type;
	private String identifier;
		
	public InputField(int type, String identifier) {
		super();
		connectionManagement = sIRC.conManagement;
		this.type = type;
		this.identifier = identifier;
		
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
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	private String restLine(String[] line, int startAt) {
		String msg = "";
		for(int i = startAt; i < line.length; i++)
			msg = msg.concat(line[i] + " ");
		msg = msg.substring(0, msg.length()-1);	// Remove last space
		return msg;
	}
	
	//TODO: Needs cleanup. Maybe do regexp. Now it does not support multiple spaces in output to irc
	public void parseInput(String text) {
		//System.out.println("Input: " + text);
		if(text.charAt(0) == '/') {	// commands
			try {
				String[] line = text.split("\\s+");	// needs try/catch
				String cmd = line[0].toLowerCase().substring(1);
				System.out.println("cmd: " + cmd);
					
				if(cmd.equals("join") || cmd.equals("j")) {
					if(line.length == 2) { // Where to validate? 
						connectionManagement.joinChannel(line[1]);
					}
				}
				else if(cmd.equals("connect")) {
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
				
				else if(cmd.equals("topic")) {
					if(type == TabComponent.CHANNEL && line.length > 1) {
						String topicMsg = restLine(line, 1);
						connectionManagement.setTopic(identifier, topicMsg);
					}
				}
				
				else if(cmd.equals("away")) {
					if(line.length > 1) {
						String awayMsg = restLine(line, 1);
						connectionManagement.away(awayMsg);
					} else {
						connectionManagement.away(null);
					}
				}

				else {
					sIRC.tabContainer.consoleMsg(sIRC.i18n.getStr("error.unknownCommand"));
				}
			} catch (Exception e) {
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
