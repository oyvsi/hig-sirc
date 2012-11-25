package no.hig.sss.sirc;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jerklib.events.modes.ModeAdjustment.Action;

public class TabContainer extends JTabbedPane {
	private Map<String, TabComponent> tabContainer = new HashMap<String, TabComponent>();

	public TabContainer() {
		super();
		newTab("Console", TabComponent.CONSOLE);
		setMnemonicAt(0, KeyEvent.VK_1);
		//((TabComponent) getComponent(0)).inFocus(); // Does not work. Why?
	
		// We want to notify the tab it has been selected
		addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int index = getSelectedIndex();
				((TabComponent) getComponent(index)).inFocus();
			}
		});
	}	

	public void message(String message, String identifier, int type, int format) {
		int index = getTabIndex(identifier);
		if(index == -1) {
			newTab(identifier, type);
			index = getTabIndex(identifier);
			setSelectedIndex(index);
			switch(index) {
				case 1:  setMnemonicAt(index, KeyEvent.VK_2); break;
				case 2:  setMnemonicAt(index, KeyEvent.VK_3); break;
				case 3:  setMnemonicAt(index, KeyEvent.VK_4); break;
				case 4:  setMnemonicAt(index, KeyEvent.VK_5); break;
				case 5:  setMnemonicAt(index, KeyEvent.VK_6); break;
				case 6:  setMnemonicAt(index, KeyEvent.VK_7); break;
				case 7:  setMnemonicAt(index, KeyEvent.VK_8); break;
				case 8:  setMnemonicAt(index, KeyEvent.VK_9); break;
			}
		}
		tabContainer.get(identifier).addText(message, format);
		System.out.println("Got message: " + message);
	}
	
	public void consoleMsg(String msg) {
		tabContainer.get("Console").addText(msg, TabComponent.CONSOLE);
	}
	
	public int getTabIndex(String identifier) {
		return indexOfTab(identifier);
		
	}
	
	private String getTabIdentifier(int index) {
		return ((TabComponent) getComponent(index)).getIdentifier();
	}
	

	public void newTab(String identifier, int type) {
		TabComponent tab = new TabComponent(type, identifier);
		tabContainer.put(identifier, tab);
		addTab(identifier, tab);	
		int index = getTabIndex(identifier);
		setSelectedIndex(index);
	}

	public void closeTab(String identifier) {
		remove(getTabIndex(identifier));
		tabContainer.remove(identifier);
	}
	
	public void closeAllTabs() {	// Close everything but the console
		for(int i = getTabCount() - 1; i > 0; i--) {
			tabContainer.remove(getTabIdentifier(i));
			remove(i);
		}
	}

	public void setTopText(String identifier, String text) {
		tabContainer.get(identifier).setTopText(text);
	}
	

	public void userJoined(String identifier, String nick) {
		tabContainer.get(identifier).getUserContainer().addUser(nick);
	}
	
	public void userLeft(String identifier, String nick) {
		tabContainer.get(identifier).getUserContainer().removeUser(nick);
	}
	
	public void opMode(String channelName, String nick, Action action) {
		tabContainer.get(channelName).getUserContainer().opMode(nick, action);
	}
	
	public void voiceMode(String channelName, String nick, Action action) {
		tabContainer.get(channelName).getUserContainer().voiceMode(nick, action);
	}
	
	public void userQuit(String nick, String msg) {
		TabComponent tab;
		for(int i = 1; i < tabContainer.size(); i++) {
			tab = (TabComponent) getComponent(i);
			if(tab.getType() == TabComponent.CHANNEL) {
				if(tab.getUserContainer().userInChannel(nick)) {
					tab.getUserContainer().removeUser(nick);
					tab.addText(msg, TabComponent.INFO);
				}
			}
			else if(tab.getType() == TabComponent.PM) {
				tab.addText(msg, TabComponent.INFO);
			}
		}
	}
	
	public void nickChange(String oldNick, String newNick, String msg) {
		TabComponent tab;	
		for(int i = 1; i < tabContainer.size(); i++) {
			tab = (TabComponent) getComponent(i);

			if(tab.getType() == TabComponent.CHANNEL) {
				if(tab.getUserContainer().userInChannel(oldNick)) {
					tab.getUserContainer().nickChange(oldNick, newNick);
					tab.addText(msg, TabComponent.INFO);
				}
			}
			else if(tab.getType() == TabComponent.PM) {
				tab.setIdentifier(newNick);
				tab.setTopText(newNick);
				setTitleAt(i, newNick);
				tab.addText(msg, TabComponent.INFO);
				
				tabContainer.remove(oldNick);
				tabContainer.put(newNick, tab);
			}
		}
		
	}

	public int getType(String identifier) {
		int index = getTabIndex(identifier);
		if(index > -1)	// Found tab
			return tabContainer.get(identifier).getType();
		else
			return -1;

	}
	
	public boolean containsUser(String identifier) {
		return tabContainer.containsKey(identifier);
	}
}