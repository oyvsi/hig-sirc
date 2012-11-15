package no.hig.sss.sirc;

import java.awt.BorderLayout;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import jerklib.Channel;
import jerklib.Session;

public class TabComponent extends JPanel {
	public final static int CONSOLE = 0;
	public final static int PM = 0;
	public final static int CHANNEL = 1;
	
	private int type;
	private String identifier;
	private JTextArea chatArea;
	private JScrollPane scrollPane;
	private UsersContainer userContainer;
	private JList users;
	JTextField inputArea;
	ConnectionManagement cm = sIRC.conManagement;
	
	public TabComponent(int type, String identifier) {
		setLayout(new BorderLayout());
		
		this.identifier = identifier;
		if(type == PM) {
			this.type = PM;
		}
		else if(type == CHANNEL) {
			this.type = CHANNEL;
			userContainer = new UsersContainer(cm.getUsers(identifier));
			users = new JList(userContainer);
			add(users, BorderLayout.EAST);
		}	
		chatArea = new JTextArea();
		chatArea.setEditable(false);
		inputArea = new InputField(type, identifier);
		add(inputArea, BorderLayout.SOUTH);
		add(chatArea, BorderLayout.NORTH);
		scrollPane = new JScrollPane(chatArea);
		add(scrollPane);
	}
	
	public void addText(String message) {
		chatArea.append(message + "\n");
	    SwingUtilities.invokeLater(new Thread() {
	    	public void run() {
	    		JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
	    		scrollBar.setValue(scrollBar.getMaximum());
	    	}
	    });
	}
	
}