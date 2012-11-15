package no.hig.sss.sirc;

import java.awt.Component;
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
		}
		//setSelectedIndex(index);
		tabContainer.get(identifier).addText(message);
		System.out.println("Got message: " + message);
	}
	
	public int getTabIndex(String identifier) {
		return indexOfTab(identifier);
	}

	private void newTab(String identifier, int type) {
		tab = new TabComponent(type);
		tabContainer.put(identifier, tab);
		addTab(identifier, tab);	
	}
}