package no.hig.sss.sirc;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
		tabContainer.get("Console").addText(msg, TabComponent.INFO);
	}
	
	private int getTabIndex(String identifier) {
		return indexOfTab(identifier);
		
	}
	
	private String getTabIdentifier(int index) {
		return ((TabComponent) getComponent(index)).getIdentifier();
	}
	

	public void newTab(String identifier, int type) {
		TabComponent tab = new TabComponent(type, identifier);
		tabContainer.put(identifier, tab);
		addTab(identifier, tab);	
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
	
}