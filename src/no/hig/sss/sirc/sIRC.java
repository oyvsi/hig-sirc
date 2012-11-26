package no.hig.sss.sirc;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;


/**
 * Initializes the application and holds references
 * to ConnectionManagement, TabContainer, Options and I18n  
 * @author Oyvind Sigerstad, Nils Slaaen, Bjorn-Erik Strand
 *
 */
public class sIRC extends JFrame {
    private static final long serialVersionUID = 1L;
	public static ConnectionManagement conManagement;
	public static TabContainer tabContainer;
	public static Options options;
	public static I18n i18n;
	
	/**
	 * Constructor 
	 */
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
	 * Main creates the JFrame and sets it to visible, with exit on close
	 * @param args
	 */
	public static void main(String[] args) {
		sIRC irc = new sIRC();
		irc.setVisible(true);
		irc.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
}
