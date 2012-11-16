package no.hig.sss.sirc;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.swing.JTextField;

public class InputField extends JTextField  {
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
						connectionManagement.connect("FlashSirc", line[1]);
				}
				else if(cmd.equals("msg")) {	// Private messages
					if(line.length > 2) {
						connectionManagement.privMsg(line[1], restLine(line, 2), TabComponent.PM);
					}
				}
				else if(cmd.equals("wc")) {	 // Close window
					String partMsg = null;
					if(line.length > 1)
						partMsg = restLine(line, 1);
					connectionManagement.closeChat(identifier, type, partMsg);
				}
				else {
					connectionManagement.channelMsg("Console", "Unknown command", TabComponent.CONSOLE); 
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} else {	// Just normal chat
			if(type == TabComponent.CHANNEL)
				connectionManagement.channelMsg(identifier, text, type);
			else
				connectionManagement.privMsg(identifier, text, type);		
		}	
	}	 
}
