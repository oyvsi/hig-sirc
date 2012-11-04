package no.hig.sss.sirc;

import java.awt.Dimension;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JFrame;


public class sIRC extends JFrame {
	private static ResourceBundle messages;
	private static Locale currentLocale;
	
	public sIRC() {
		GUI gui = new GUI(messages);
		setExtendedState(MAXIMIZED_BOTH);
		setJMenuBar(gui.menu());		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		currentLocale = Locale.getDefault();
		messages = ResourceBundle.getBundle("i18n/I18N", currentLocale);
		
		sIRC irc = new sIRC();
		irc.pack();
		irc.setVisible(true);
		irc.setDefaultCloseOperation(EXIT_ON_CLOSE);

	}
}
