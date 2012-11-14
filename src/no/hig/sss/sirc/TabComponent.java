package no.hig.sss.sirc;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import jerklib.Channel;
import jerklib.Session;

public class TabComponent extends JPanel {
	final int PM = 0;
	final int CHANNEL = 1;
	
	private int type;
	JTextArea chatArea;
	ConnectionManagement cm = sIRC.conManagement;
	
	public TabComponent(int type) {
		if(type == PM) {
			this.type = PM;
		}
		else if(type == CHANNEL) {
			this.type = CHANNEL;
			// add userlist
		}	
		chatArea = new JTextArea();
	}		
	
}


	