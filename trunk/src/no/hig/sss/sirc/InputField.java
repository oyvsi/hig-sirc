package no.hig.sss.sirc;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;

public class InputField extends JTextField  {
	private ConnectionManagement connectionManagement;
		
	public InputField() {
		super();
		connectionManagement = sIRC.conManagement;
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
	
	public void parseInput(String text) {
		if(text.charAt(0) == '/') {
			try {	// Command with argument
				String[] line = text.split("\\s+");
				String cmd = line[0].toLowerCase().substring(1);
				System.out.println("cmd: " + cmd);
				if(cmd == "join") {
					System.out.println("join " + line.length);
					if(line.length == 2) { // Where to validate? 
						System.out.println("All good, passing join");
						connectionManagement.joinChannel(line[1]);
					} else {
						System.out.println("Length is not two");
					}
				} else {
					System.out.println("Unknown command");
				}
			} catch (Exception e) {
				// TODO: handle exception
			};
			//System.out.println(command);
		
		}
		
		
	}
	 
}
