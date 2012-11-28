package no.hig.sss.sirc;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.ImageIcon;
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
		try {
			setupSettings();
		} catch (IOException e) {
			System.out.println("Error setting up config files");
			e.printStackTrace();
		}

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
		//options.createAndShowGUI(this);
	}
	
	/**
	 * Main creates the JFrame and sets it to visible, with exit on close
	 * @param args
	 */
	public static void main(String[] args) {
		sIRC irc = new sIRC();
		
		
		irc.setVisible(true);
		irc.setDefaultCloseOperation(EXIT_ON_CLOSE);
		options.createAndShowGUI();
	}
	
	/**
	 * Makes sure that we have our config files set up and sets program icon.
	 * 
	 * @throws IOException
	 */
	public void setupSettings() throws IOException {		
		String home = System.getProperty("user.home");
	
		// Set image icon
		URL myurl = this.getClass().getResource("Resources/icon.png");
		ImageIcon img = new ImageIcon(this.getToolkit().getImage(myurl));
		setIconImage(img.getImage());
		
		File settingsDir = new File(home, "sirc");
		File configDest = new File(settingsDir, "config.ini");		
		File serversDest = new File(settingsDir, "servers.ini");
		
		if(settingsDir.exists() == false) {	// Make sure we have %User%sIRC/
	        if(!settingsDir.mkdir())
	            throw new IllegalStateException(settingsDir.toString());
		}
		
		if(configDest.exists() == false) // There is no config-file in %User%sIRC/. Copy from jar
			Helpers.copyFile(getClass().getResourceAsStream("Resources/config.ini"), home + "/sIRC/config.ini");
		
		if(serversDest.exists() == false) // There is no servers-file in %User%sIRC/. Copy from jar
			Helpers.copyFile(getClass().getResourceAsStream("Resources/servers.ini"), home + "/sIRC/servers.ini");
	}
}
