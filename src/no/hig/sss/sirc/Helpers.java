package no.hig.sss.sirc;

import java.awt.event.ActionListener;
import java.io.File;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * Helper class which holds methods for creating common JComponents
 * @author Oyvind Sigerstad, Nils Slaaen, Bjorn-Erik Strand
 *
 */

public final class Helpers {
	
	
	/**
	 * Creates a JMenuItem
	 * @param name Name of the new item
	 * @param ac The ActionCommand
	 * @param tt Text for tooltip
	 * @param menu he menu which this item is added to
	 * @param al Set this ActionListener
	 * @return mi The JMenuItem
	 */
	public static JMenuItem createMenuItem(String name, String ac, String tt,  JMenu menu, ActionListener al) {
		JMenuItem mi = new JMenuItem(sIRC.i18n.getStr(name));
		mi.setActionCommand(ac);
		mi.addActionListener(al);
		menu.add(mi);
		if (tt.length() > 2) { // Set tooltip if the string tooltip
			// contains something useful
			mi.setToolTipText(sIRC.i18n.getStr(tt));
		}
		return mi;
	}
	/**
	 * Creates a JCheckBoxMenuItem
	 * @param name Name of the new item
	 * @param ac The ActionCommand
	 * @param tt Text for tooltip
	 * @param menu The menu which this item is added to
	 * @param al Set this ActionListener
	 * @param state The default state to be set
	 * @return cbmi The JCheckBoxMenuItem
	 */

	public static JCheckBoxMenuItem createCheckBoxMenuItem(String name, String ac,String tt, JMenu menu, ActionListener al, boolean state) {
		JCheckBoxMenuItem cbmi = new JCheckBoxMenuItem(sIRC.i18n.getStr(name));
		cbmi.setActionCommand(ac);
		cbmi.addActionListener(al);
		cbmi.setState(state);
		menu.add(cbmi);
		if (tt.length() > 2) { // Set tooltip if the string tooltip
			// contains something useful
			cbmi.setToolTipText(sIRC.i18n.getStr(tt));
		}
		return cbmi;
	}
	
	
	/**
	 * Creates a JButton
	 * @param name Name of the new item
	 * @param tt Text for tooltip
	 * @param ac The ActionCommand
	 * @param al Set this ActionListener
	 * @return b The JButton
	 */
	
	public static JButton createButton(String name, String tt, String ac, ActionListener al) {
		JButton b = new JButton(sIRC.i18n.getStr(name));
		b.addActionListener(al);
		b.setActionCommand(ac);
		
		if (tt.length() > 2) { // Set tooltip if the string tt contains something useful
			b.setToolTipText(sIRC.i18n.getStr(tt));
		}
		return b;
	}
	
	public static File getFile(String name) {
		File file = new File(System.getProperty("user.home") + "/sIRC", name);
		return file;
	}
	
	public static void setupSettings(URL resourcesPath) throws IOException {
		
		String home = System.getProperty("user.home");
		File settingsDir = new File(home, "sirc");
		
		File configDest = new File(settingsDir, "config.ini");
		File configSource = new File(resourcesPath.getPath(), "config.ini");
		
		File serversDest = new File(settingsDir, "servers.ini");
		File serversSource = new File(resourcesPath.getPath(), "servers.ini");
		
		if(settingsDir.exists() == false) {	// Make sure we have %User%sIRC/
	        if(!settingsDir.mkdir())
	            throw new IllegalStateException(settingsDir.toString());
		}
		
		if(configDest.exists() == false) 
			copyFile(configSource, configDest);
		
		if(serversDest.exists() == false) 
			copyFile(serversSource, serversDest);
	}
	
		// Requires Java 7
	public static void copyFile(File from, File to) throws IOException {
	    Files.copy(from.toPath(), to.toPath());
		/*
		FileOutputStream output = new FileOutputStream(to);
		InputStream input = new FileInputStream(from);
		byte [] buffer = new byte[4096];
		int bytesRead = input.read(buffer);
		while (bytesRead != -1) {
		    output.write(buffer, 0, bytesRead);
		    bytesRead = input.read(buffer);
		}
		output.close();
		input.close();*/
		
	}

}