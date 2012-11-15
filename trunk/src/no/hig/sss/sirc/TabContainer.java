package no.hig.sss.sirc;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class TabContainer extends JTabbedPane {
	TabComponent tab;
	Map<String, TabComponent> tabContainer = new HashMap<String, TabComponent>();

	public TabContainer() {
		super();
		newTab("Console", TabComponent.CONSOLE);
	}	
	
	

	public void message(String message, String identifier, int type) {
		int index = getTabIndex(identifier);
		if(index == -1) {
			newTab(identifier, type);
			index = getTabIndex(identifier);
			setSelectedIndex(index);
			switch(index) {
				case 0:  setMnemonicAt(index, KeyEvent.VK_1); break;
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
		tabContainer.get(identifier).addText(message);
		System.out.println("Got message: " + message);
	}
	
	public int getTabIndex(String identifier) {
		return indexOfTab(identifier);
	}

	private void newTab(String identifier, int type) {
		tab = new TabComponent(type, identifier);
		tabContainer.put(identifier, tab);
		addTab(identifier, tab);	
	}
}