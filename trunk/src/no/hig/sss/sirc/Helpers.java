package no.hig.sss.sirc;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

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
	
	public static void copyFile(InputStream input, String destination) throws IOException {
		 
		OutputStream os = new FileOutputStream(destination);							// The output file
		byte[] buffer = new byte[4096];													// Set the size of the buffer
		int length;																		// Read size
		while ((length = input.read(buffer)) > 0) 
			os.write(buffer, 0, length);

		os.close();
		input.close();
		
	}

}