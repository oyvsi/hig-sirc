package no.hig.sss.sirc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

/**
 * Creates our menu bars
 * 
 * @author Oyvind Sigerstad, Nils Slaaen, Bjorn-Erik Strand
 *
 */
class GUI implements ActionListener, MenuListener {
	private JFrame jf;
	private JMenuItem serverAway;	
	private JMenuItem serverConnect;
	private JMenuItem serverDisconnect;
	private JMenuItem serverJoinChannel;
	private JMenuItem serverChannelList;
	
	/**
	 * Constructor. Stores frame for later use.
	 * @param jf the parrent frame
	 */
	public GUI(JFrame jf) {
		this.jf = jf;
	}

	/**
	 * Creates the menubar and returns a menubar object
	 * 
	 * @return JMenuBar
	 */
	public JMenuBar menu() {
		// Create menubar
		JMenuBar menuBar = new JMenuBar();
		// Create our menus
		JMenu fileMenu = new JMenu(sIRC.i18n.getStr("menuBar.File"));
		JMenu toolsMenu = new JMenu(sIRC.i18n.getStr("menuBar.Tools"));
		JMenu helpMenu = new JMenu(sIRC.i18n.getStr("menuBar.Help"));
		JMenu serverMenu = new JMenu(sIRC.i18n.getStr("menuBar.Server"));
		
		// Tooltip for menu items
		fileMenu.setToolTipText(sIRC.i18n.getStr("tooltip.File"));
		toolsMenu.setToolTipText(sIRC.i18n.getStr("tooltip.Tools"));
		helpMenu.setToolTipText(sIRC.i18n.getStr("tooltip.Help"));
		serverMenu.setToolTipText(sIRC.i18n.getStr("tooltip.Server"));
		// Listener for when the user clicks on server menu
		serverMenu.addMenuListener(this);

		// Items for File menu
		JMenuItem fileExit = Helpers.createMenuItem("fileMenu.Exit", "exit", "tooltip.Exit", fileMenu, this);
		// Items for Edit menu 
		Helpers.createMenuItem("editMenu.Options", "options", "tooltip.Options", toolsMenu, this);
		// Items for Help
		Helpers.createMenuItem("helpMenu.Help", "help", "tooltip.Help", helpMenu, this);
		Helpers.createMenuItem("helpMenu.About", "about", "tooltip.About", helpMenu, this);
		
		// Items for Server menu
		serverConnect = Helpers.createMenuItem("serverMenu.Connect", "connect", "tooltip.Connect", serverMenu, this);
		serverDisconnect = Helpers.createMenuItem("serverMenu.Disconnect", "disconnect", "tooltip.Disconnect", serverMenu, this);
		serverJoinChannel = Helpers.createMenuItem("serverMenu.JoinAChannel", "joinAChannel", "tooltip.JoinAChannel", serverMenu, this);
		serverChannelList = Helpers.createMenuItem("serverMenu.ListOfChannels", "listOfChannels", "tooltip.ListOfChannels", serverMenu, this);
		serverAway = Helpers.createCheckBoxMenuItem("serverMenu.MarkedAway", "markedAway", "tooltip.MarkedAway", serverMenu, this, false);
		
		// Mnemonics
		fileExit.setMnemonic(KeyEvent.VK_Q);
		fileExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
		ActionEvent.ALT_MASK));
		
		// Adding main menu
		menuBar.add(fileMenu);
		menuBar.add(toolsMenu);
		menuBar.add(helpMenu);
		menuBar.add(serverMenu);
	
		return menuBar;
	}


	/**
	 * Handles when user clicks on out menu items
	 */
	public void actionPerformed(ActionEvent e) {
		String event = e.getActionCommand();
		
		if (event.equals("exit"))
			System.exit(0);
		
		else if (event.equals("options"))
			sIRC.options.showWindow();
		
		else if (event.equals("help")) {
			sIRC.options.showWindow();
			sIRC.options.setViewHelp(Options.CONNECTIONHELP);
		} 
		
		else if (event.equals("disconnect"))
			sIRC.conManagement.disConnect(null);
		
		else if (event.equals("connect"))
			sIRC.conManagement.connect(sIRC.options.getNick(), sIRC.options.getServer());
	
		
		else if (event.equals("joinAChannel")) {  // Ask for channel from user
			String channel = (String) JOptionPane.showInputDialog(jf, sIRC.i18n.getStr("enterChannel"), "Join channel", JOptionPane.QUESTION_MESSAGE);			
			if(channel != "" && channel != null)
				sIRC.conManagement.joinChannel(channel);  // Join
		} 
		
		else if (event.equals("markedAway")) {
			if(serverAway.isSelected()) {	// Ask for a reason
				String awayMsg = (String) JOptionPane.showInputDialog(jf, sIRC.i18n.getStr("setAwayMsg"), "Set away message", 						
					     JOptionPane.QUESTION_MESSAGE);
				sIRC.conManagement.away(awayMsg);
			} 
			else {
				sIRC.conManagement.away(null);
			}
		} 
		
		else if(event.equals("about")) {
			sIRC.options.showWindow();
			sIRC.options.setViewHelp(Options.ABOUTHELP);	
		} 
		
		else if(event.equals("listOfChannels")) {
			sIRC.conManagement.ListChannels();
			sIRC.tabContainer.setSelectedIndex(0);
		}
		
		else {  // Unknown command
			JOptionPane.showMessageDialog(null, sIRC.i18n.getStr("notFound.Text"));
		}
	}

	/**
	 * Handles when user clicks on server
	 */
	public void menuSelected(MenuEvent e) {
		if(sIRC.conManagement.isConnected()) { // Enable features that are only applicable when connected
			serverDisconnect.setEnabled(true);
			serverAway.setEnabled(true);
			serverJoinChannel.setEnabled(true);
			serverChannelList.setEnabled(true);
			
			serverAway.setSelected(sIRC.conManagement.getSession().isAway());	// Are we away?
		} 
		
		else {  // Disable them
			serverDisconnect.setEnabled(false);
			serverAway.setEnabled(false);
			serverJoinChannel.setEnabled(false);
			serverChannelList.setEnabled(false);
		}
	}

	/**
	 * Not implemented
	 */
	public void menuDeselected(MenuEvent e) {}

	/**
	 * Not implemented
	 */
	public void menuCanceled(MenuEvent e) {}
}