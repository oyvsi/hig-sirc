package no.hig.sss.sirc;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.DefaultListModel;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import jerklib.ConnectionManager;


public class sIRC extends JFrame {
	public static ConnectionManagement conManagement;
	public static TabContainer tabContainer;
	public static Options options;
	public static I18n i18n;
	
	public sIRC() {
		i18n = new I18n();
		conManagement = new ConnectionManagement(); 
		tabContainer = new TabContainer();
		options = new Options();
		
		GUI gui = new GUI(this);		       
        add(tabContainer);
				
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                  screenSize.width  - inset*2,
                  screenSize.height - inset*2);

		setJMenuBar(gui.menu());
		options.createAndShowGUI();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		sIRC irc = new sIRC();
		irc.setVisible(true);
		irc.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
}
