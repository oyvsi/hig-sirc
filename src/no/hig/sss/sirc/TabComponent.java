package no.hig.sss.sirc;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;

/**
 * Class holding the different components creating a channel window or pm window
 * 
 * @author Oyvind Sigerstad, Nils Slaaen, Bjorn-Erik Strand
 *
 */
public class TabComponent extends JPanel {
	
	private static final long serialVersionUID = 1L;

	// Constants used for tab and text format type
	public final static int CONSOLE = 0;	
	public final static int PM = 1;
	public final static int CHANNEL = 2;	
	public final static int INFO = 3;
	
	
	private int type;
	private String identifier;
	private JTextPane chatArea;
	private JScrollPane scrollPaneChat;
	private UserModel userModel;
	private UserList userList;
	private InputField inputArea;
	private JTextField topText;

	/**
	 * Constructor - Creates either a pm tab or channel tab
	 * @param type channel or pm
	 * @param identifier the name to be set for the tab
	 */
	public TabComponent(int type, String identifier) {
		setLayout(new BorderLayout());
		this.identifier = identifier;
		
		if(type == PM) {
			this.type = PM;
		}
		
		else if(type == CHANNEL) {
			this.type = CHANNEL;
			userModel = new UserModel(identifier);
			userList = new UserList(identifier, userModel);
			JScrollPane scrollPaneUsers = new JScrollPane(userList);
			add(scrollPaneUsers, BorderLayout.EAST);
		}
			
		chatArea = new JTextPane();
		chatArea.setEditable(false);
		inputArea = new InputField(type, identifier);
		
		add(inputArea, BorderLayout.SOUTH);
		add(chatArea, BorderLayout.NORTH);
		if(type == CHANNEL || type == PM)
			add(makeTopText(), BorderLayout.NORTH);

		scrollPaneChat = new JScrollPane(chatArea);
		add(scrollPaneChat);
	}
	
	/**
	 * Creates the text field containing either topic or nick of user
	 * @return JTextField the text field
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
	 * Sets the top text if channel or pm tab
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
	 * @param message the text to be appended
	 * @param type the type of style to apply to the text
	 */
	public void addText(String message, int type) {
		Document document = chatArea.getDocument();
		SimpleAttributeSet format;
			// grab the text style
		if(type == INFO)
			format = Options.infoFormat.format();
		else if(type == PM)
			format = Options.pmFormat.format();
		else if(type == CONSOLE)
			format = Options.consoleFormat.format();
		else
			format = Options.channelFormat.format();
			// add the text to the chat area with appropriate style 
		try {
			document.insertString(document.getLength(), message + "\n", format);
		} catch (BadLocationException e) {
			System.out.println("Error adding text to tab");
			e.printStackTrace();
		}
			// Add text first, then set scroll
	    SwingUtilities.invokeLater(new Thread() {
	    	public void run() {
	    		JScrollBar scrollBar = scrollPaneChat.getVerticalScrollBar();
	    		scrollBar.setValue(scrollBar.getMaximum());
	    	}
	    });
	}

	/**
	 * Returns the identifier of this tab
	 * @return String the identifier
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
	 * @return the type of the tab
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * Returns the user container for this tab
	 * @return UserContainer - The container
	 */
	public UserModel getUserModel() {
		return userModel;
	}	
}
