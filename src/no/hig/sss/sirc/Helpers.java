package no.hig.sss.sirc;

import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public final class Helpers {
	
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

	public static JButton createButton(String name, String tt, String ac, ActionListener al) {
		JButton b = new JButton(sIRC.i18n.getStr(name));
		b.addActionListener(al);
		b.setActionCommand(ac);
		
		if (tt.length() > 2) { // Set tooltip if the string tt contains something useful
			b.setToolTipText(sIRC.i18n.getStr(tt));
		}
		return b;
	}

}