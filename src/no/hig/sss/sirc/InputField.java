package no.hig.sss.sirc;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;

public class InputField extends JTextField  {
	private ConnectionManagement connectionManagement;
	private TabContainer tabs;
	private int type;
	private String identifier;
		
	public InputField(int type, String identifier) {
		super();
		connectionManagement = sIRC.conManagement;
		tabs = sIRC.tabContainer;
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
	//TODO: Needs cleanup
	public void parseInput(String text) {
		if(text.charAt(0) == '/') {
			try {	// Command with argument
				String[] line = text.split("\\s+");
				String cmd = line[0].toLowerCase().substring(1);
				System.out.println("cmd: " + cmd);
				if(cmd.equals("join")) {
					System.out.println("join " + line.length);
					if(line.length == 2) { // Where to validate? 
						System.out.println("All good, passing join");
						connectionManagement.joinChannel(line[1]);
					} else {
						System.out.println("Length is not two");
					}
				}
				else if(cmd.equals("connect")) {
					connectionManagement.connect("GOLVsirc", line[1]);
				}
				else {
					System.out.println("Unknown command");
				}
			} catch (Exception e) {
				e.printStackTrace();
			};
		} else {	// Just normal chat
			if(type == TabComponent.CHANNEL)
				connectionManagement.channelMsg(identifier, text, type);
			else
				connectionManagement.privMsg(identifier, text, type);
			
		}	
	}
	 
}
