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
	
	public void addText(String message, int type) {
	//	Options.pmFormat.format(message);
		Document document = chatArea.getDocument();
		SimpleAttributeSet format;
		if(type == INFO)
			format = Options.infoFormat.format();
		else if(type == PM)
			format = Options.pmFormat.format();
		else
			format = Options.channelFormat.format();
		
		try {
			document.insertString(document.getLength(), message + "\n", format);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		//chatArea.append(message + "\n");
		
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
	
	public int getType() {
		return type;
	}
	
	private void createRightClickMenu(MouseEvent e) {
	
		
		
	}
	
	public JMenu createSubMenu(String [] names, String title) {
		JMenu subMenu = new JMenu(title);
		for(int i = 0; i < names.length; i++) {
			subMenu.add(new JMenuItem(names[i]));
			
		}
		return subMenu;
		
	}
	
	class popupListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command == "Kick") {
			//cm.getChannel(identifier).kick(userContainer.getElementAt(users.getSelectedIndex()).toString(), "LOL");
		}
	}
	
	}
	
	public JMenuItem createMenuItem(String name) {
		return new JMenuItem(name);
	}
	
	public UsersContainer getUserContainer() {
		return userContainer;
	}
	
}
