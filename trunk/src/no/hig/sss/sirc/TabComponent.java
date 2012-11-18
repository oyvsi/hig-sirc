package no.hig.sss.sirc;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

public class TabComponent extends JPanel {
	public final static int CONSOLE = 0;
	public final static int PM = 1;
	public final static int CHANNEL = 2;
	
	private int type;
	private String identifier;
	private JTextArea chatArea;
	private JScrollPane scrollPane;
	private UsersContainer userContainer;
	private JList users;
	private JTextField inputArea;
	private ConnectionManagement cm = sIRC.conManagement;
	private JTextField topText;
	
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
		if(type == CHANNEL || type == PM)
			add(makeTopText(), BorderLayout.NORTH);

		scrollPane = new JScrollPane(chatArea);
		add(scrollPane);
	}
	
	private JTextField makeTopText() {
		topText = new JTextField(identifier);
		topText.setBackground(null);
		topText.setBorder(BorderFactory.createLineBorder(Color.white, 0));
		topText.setEditable(false);
		topText.setHorizontalAlignment(JTextField.CENTER);
		
		return topText;
	}
	
	public void setTopText(String text) {
		if(type == CHANNEL || type == PM)
			topText.setText(text);
	}
	
	public void inFocus() {
		inputArea.requestFocus();
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

	public String getIdentifier() {
		return identifier;
	}
	
}