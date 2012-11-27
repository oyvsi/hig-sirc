package no.hig.sss.sirc;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jerklib.events.modes.ModeAdjustment.Action;

/**
 * This class holds all the tabs and pass messages to them.
 * 
 * @author Oyvind Sigerstad, Nils Slaaen, Bjorn-Erik Strand
 *
 */

public class TabContainer extends JTabbedPane {
	private static final long serialVersionUID = 1L;
	private Map<String, TabComponent> tabContainer; // Holds all the tabs
	private boolean isAway;	// Keeps track of away status

	/**
	 * Constructor
	 */
	public TabContainer() {
		super();
		isAway = false;
		tabContainer = new HashMap<String, TabComponent>();
	
		newTab("Console", TabComponent.CONSOLE); // Create console tab
		setMnemonics();
		//setMnemonicAt(0, KeyEvent.VK_1); // Set shortcut
	
		// Notify when tab is selected to set input area in focus.
		addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int index = getSelectedIndex();
				((TabComponent) getComponent(index)).inFocus();
			}
		});
	}	

	
	/**
	 * Function to pass messages to tab. Creates new tab if identifier is unknown.
	 * @param message - The message to send
	 * @param identifier - The name of channel or user
	 * @param type - The type of tab. Defined in TabComponent
	 * @param format - The text style to use for the message. Defined in TabComponent
	 */
	
	public void message(String message, String identifier, int type, int format) {
		int index = getTabIndex(identifier);
		if(index == -1) {	// Unknown tab. Create a new
			newTab(identifier, type);
		}
		tabContainer.get(identifier).addText(message, format);	// Pass along the message
		// if user is set away. Send her a reminder.
		if(isAway && type == TabComponent.PM)
			tabContainer.get(identifier).addText(sIRC.i18n.getStr("pm.isAwayReminder"), TabComponent.INFO);
	}
	
	/**
	 * Sends message to the consoleTab
	 * 
	 * @param msg - The text to append
	 */
	public void consoleMsg(String msg) {
		tabContainer.get("Console").addText(msg, TabComponent.CONSOLE);
	}
	/**
	 * Get index of tab
	 * 
	 * @param identifier - The name of channel or nick
	 * @return - the index of tab. -1 if it does not exist
	 */
	public int getTabIndex(String identifier) {
		return indexOfTab(identifier);
	}

	
	/**
	 * Creates a new tab
	 * 
	 * @param identifier - The name of channel or nick
	 * @param type - The type of tab. Defined in TabComponent
	 */
	public void newTab(String identifier, int type) {
		TabComponent tab = new TabComponent(type, identifier);
		tabContainer.put(identifier, tab);
		addTab(identifier, tab);	
		int index = getTabIndex(identifier);
		setSelectedIndex(index);
		setMnemonics();	// Has to be updated when /wc between windows
		
		setSelectedIndex(index);
	}
	/**
	 * Sets Mnemonics for all tabs
	 */
	public void setMnemonics() {
		for(int i = 0; i < getTabCount(); i++) {
			switch(i) {	 // Set up shortcuts for the first 8 tabs.
				case 0:	setMnemonicAt(i, KeyEvent.VK_1); break;
				case 1: setMnemonicAt(i, KeyEvent.VK_2); break;
				case 2: setMnemonicAt(i, KeyEvent.VK_3); break;
				case 3: setMnemonicAt(i, KeyEvent.VK_4); break;
				case 4: setMnemonicAt(i, KeyEvent.VK_5); break;
				case 5: setMnemonicAt(i, KeyEvent.VK_6); break;
				case 6: setMnemonicAt(i, KeyEvent.VK_7); break;
				case 7: setMnemonicAt(i, KeyEvent.VK_8); break;
				case 8: setMnemonicAt(i, KeyEvent.VK_9); break;
			}
		}
	}
	/**
	 * Close a tab
	 * 
	 * @param identifier - The name of channel or nick
	 */
	public void closeTab(String identifier) {
		remove(getTabIndex(identifier));
		tabContainer.remove(identifier);
		setMnemonics();	// Has to be updated when /wc between windows
	}
	/**
	 * Close all tabs, except console
	 */
	public void closeAllTabs() {	
		for(int i = getTabCount() - 1; i > 0; i--) {
			tabContainer.remove(getTabIdentifier(i));
			remove(i);
		}
	}
	
/**
 * Sets the top text in a tab
 * 
 * @param identifier - The name of channel or nick
 * @param text - The text to put in top-field
 */
	public void setTopText(String identifier, String text) {
		tabContainer.get(identifier).setTopText(text);
	}
	
/**
 * Notifies a tab that a user has joined
 * 
 * @param identifier - The name of channel or nick
 * @param nick - The nick of the user who joined
 */
	public void userJoined(String identifier, String nick) {
		tabContainer.get(identifier).getUserModel().addUser(nick);
	}
	
	/**
	 * Notifies a tab that a user has left
	 * 	
	 * @param identifier - The name of channel or nick
	 * @param nick - The nick of the user who left
	 */
	public void userLeft(String identifier, String nick) {
		tabContainer.get(identifier).getUserModel().removeUser(nick);
	}
	
	/**
	 * Notifies a channel-tab that a op has been set or unset for a user
	 * 
	 * @param channelName - The name of the channel
	 * @param nick - The nick of the user
	 * @param action - Action object for the op-change
	 */
	public void opMode(String channelName, String nick, Action action) {
		tabContainer.get(channelName).getUserModel().opMode(nick, action);
	}
	
	/**
	 * Notifies a channel-tab that a voice has been set or unset for a user
	 * 
	 * @param channelName - The name of the channel
	 * @param nick - The nick of the user
	 * @param action - Action object for the voice-change
	 */
	public void voiceMode(String channelName, String nick, Action action) {
		tabContainer.get(channelName).getUserModel().voiceMode(nick, action);
	}
	
	/**
	 * Notifies all tabs concerned that a user has quit
	 * 
	 * @param nick - The nick of the user who quit
	 * @param msg - The quit message
	 */
	public void userQuit(String nick, String msg) {
		TabComponent tab;
		for(int i = 1; i < tabContainer.size(); i++) {
			tab = (TabComponent) getComponent(i);
			
			if(tab.getType() == TabComponent.CHANNEL) {
				if(tab.getUserModel().userInChannel(nick)) {
					tab.getUserModel().removeUser(nick);
					tab.addText(msg, TabComponent.INFO);
				}
			}
			else if(tab.getType() == TabComponent.PM) {
				tab.addText(msg, TabComponent.INFO);
			}
		}
	}
	/**
	 * Notifies channels that a user has changed nick.
	 * If we have a PM with the user, we change the tab to reflect the new nick.
	 * 
	 * @param oldNick - The old nick
	 * @param newNick - The new nick
	 * @param msg - The info-message to send to the tab
	 */
	public void nickChange(String oldNick, String newNick, String msg) {
		TabComponent tab;	
		for(int i = 1; i < tabContainer.size(); i++) {	// loop all tabs
			tab = (TabComponent) getComponent(i);

			if(tab.getType() == TabComponent.CHANNEL) {
				if(tab.getUserModel().userInChannel(oldNick)) { // Check if user is in channel
					tab.getUserModel().nickChange(oldNick, newNick); // Notify the userlist
					tab.addText(msg, TabComponent.INFO); // Pass the change nick message
				}
			}
			else if(tab.getType() == TabComponent.PM) {
				tab.setIdentifier(newNick);	// Notify the tab of new name
				tab.setTopText(newNick);  // Change top text
				setTitleAt(i, newNick);  // Change the title of the tab
				tab.addText(msg, TabComponent.INFO); // Pass change nick message
				
				tabContainer.remove(oldNick);
				tabContainer.put(newNick, tab);
			}
		}
		
	}
	/**
	 * Get the type of tab as defined in TabComponent
	 * 
	 * @param identifier name of channel or nick
	 * @return the type of tab
	 */
	public int getType(String identifier) {
		int index = getTabIndex(identifier);
		if(index > -1)	// Found tab
			return tabContainer.get(identifier).getType();
		else
			return -1;

	}
	
	/**
	 * Sets if the user is away or not
	 * 
	 * @param isAway
	 */
	public void away(boolean isAway) {
		this.isAway = isAway;
	}
	
	/**
	 * Get identifier of tab
	 * 
	 * @param index - The index of the tab
	 * @return - The identifier of the tab
	 */
	private String getTabIdentifier(int index) {
		return ((TabComponent) getComponent(index)).getIdentifier();
	}
}