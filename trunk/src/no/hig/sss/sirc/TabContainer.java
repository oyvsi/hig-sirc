package no.hig.sss.sirc;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class TabContainer {
	JTabbedPane tabs;
	TabComponent tab;
	Map<String, TabComponent> tabContainer = new HashMap<String, TabComponent>();
	public TabContainer() {
		tabs = new JTabbedPane();
		
		
	}	
	
	public void addTab(String identifier) {
		//tab = new TabComponent(identifier);
		tabContainer.put(identifier, tab);
		tabs.addTab(identifier, tab);
		
	}
	
	
	
	

}