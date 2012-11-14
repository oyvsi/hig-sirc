package no.hig.sss.sirc;

import java.awt.BorderLayout;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import jerklib.Channel;
import jerklib.Session;

public class TabComponent extends JPanel {
	public final static int CONSOLE = 0;
	public final static int PM = 0;
	public final static int CHANNEL = 1;
	
	private int type;
	JTextArea chatArea;
	ConnectionManagement cm = sIRC.conManagement;
	
	public TabComponent(int type) {
		setLayout(new BorderLayout());
		if(type == PM) {
			this.type = PM;
		}
		else if(type == CHANNEL) {
			this.type = CHANNEL;
			// add userlist
		}	
		chatArea = new JTextArea();
		add(chatArea);
	}
	
	public void addText(String message) {
		chatArea.append(message + "\n");
		
	}
	
}


	