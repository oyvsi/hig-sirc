package no.hig.sss.sirc;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import jerklib.Channel;
import jerklib.Session;


public class TabComponent extends JPanel {
	JTextArea textField;
	JTextField inputField;
	ConnectionManagement cm = sIRC.conManagement;
	JList users;

	public TabComponent(Channel chan) {
		
		
		textField = new JTextArea();
		inputField = new JTextField();
	
	}		
	
	public JTextArea getTextArea() {
		return textField;
		
	}
		
	public void updateOwnNick() {
		
		
	}
	
	
}


	