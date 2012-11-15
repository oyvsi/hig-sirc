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
			String command = text.substring(1, text.indexOf(""));
			System.out.println(command);
		
		}
		
		
	}
	 
}
