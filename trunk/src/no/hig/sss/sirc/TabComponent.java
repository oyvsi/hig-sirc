package no.hig.sss.sirc;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;

import jerklib.Channel;
import jerklib.Session;

public class TabComponent extends JPanel {
	public final static int CONSOLE = 0;
	public final static int PM = 1;
	public final static int CHANNEL = 2;	
	public final static int INFO = 3;
	
	
	private int type;
	private String identifier;
	private JTextPane chatArea;
	private JScrollPane scrollPane;
	private UsersContainer userContainer;
	private UserList userList;
	private InputField inputArea;
	private ConnectionManagement cm = sIRC.conManagement;
	private JTextField topText;

	/**
	 * Constructor - Creates either a pm tab or channel tab
	 * @param type - Channel or pm
	 * @param identifier - The name to be set for the tab
	 */
	
	public TabComponent(int type, String identifier) {
		setLayout(new BorderLayout());
		this.identifier = identifier;
		
		if(type == PM) {
			this.type = PM;
		}
		else if(type == CHANNEL) {
			this.type = CHANNEL;
			userContainer = new UsersContainer(identifier);
			userList = new UserList(identifier, userContainer);
			add(userList, BorderLayout.EAST);
		}
			
			
		chatArea = new JTextPane();
		chatArea.setEditable(false);
		inputArea = new InputField(type, identifier);
		
		add(inputArea, BorderLayout.SOUTH);
		add(chatArea, BorderLayout.NORTH);
		if(type == CHANNEL || type == PM)
			add(makeTopText(), BorderLayout.NORTH);

		scrollPane = new JScrollPane(chatArea);
		add(scrollPane);
	}
	
	/**
	 * Creates the text field containing either topic or nick of user
	 * @return JTextField - The text field
	 */
	private JTextField makeTopText() {
		topText = new JTextField(identifier);
		topText.setBackground(null);
		topText.setBorder(BorderFactory.createLineBorder(Color.white, 0));
		topText.setEditable(false);
		topText.setHorizontalAlignment(JTextField.CENTER);
		
		return topText;
	}
	
	/**
	 * Sets the text
	 * @param text
	 */
	public void setTopText(String text) {
		if(type == CHANNEL || type == PM)
			topText.setText(text);
	}
	
	/**
	 * Sets the focus to the input field
	 */
	public void inFocus() {
		inputArea.requestFocus();
	}
	
	/**
	 * Formats, and inserts the text to the chat/info area of the tab
	 * @param message
	 * @param type
	 */
	public void addText(String message, int type) {
		Document document = chatArea.getDocument();
		SimpleAttributeSet format;

		if(type == INFO)
			format = Options.infoFormat.format();
		else if(type == PM)
			format = Options.pmFormat.format();
		else if(type == CONSOLE)
			format = Options.consoleFormat.format();
		else
			format = Options.channelFormat.format();
		
		try {
			document.insertString(document.getLength(), message + "\n", format);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	    SwingUtilities.invokeLater(new Thread() {
	    	public void run() {
	    		JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
	    		scrollBar.setValue(scrollBar.getMaximum());
	    	}
	    });
	}

	/**
	 * Returns the identifier of this tab
	 * @return String - The identifier
	 */
	public String getIdentifier() {
		return identifier;
	}
	
	/**
	 * Set the identifier 
	 * @param identifier
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
		inputArea.setIdentifier(identifier);
	}
	
	/**
	 * Return the type 
	 * @return 
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * Returns the user container for this tab
	 * @return UserContainer - The container
	 */
	public UsersContainer getUserContainer() {
		return userContainer;
	}	
}
