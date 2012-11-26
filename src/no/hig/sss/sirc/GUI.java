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
	private JMenuItem marked_away;
	
	public GUI(JFrame jf) {
		this.jf = jf;
	}

	/**
	 * Creates the menubar and returns a menubar object
	 * 
	 * @return JMenuBar
	 */
	public JMenuBar menu() {
		// Create menu
		JMenuBar menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu(sIRC.i18n.getStr("menuBar.File"));
		JMenu toolsMenu = new JMenu(sIRC.i18n.getStr("menuBar.Tools"));
		JMenu helpMenu = new JMenu(sIRC.i18n.getStr("menuBar.Help"));
		JMenu serverMenu = new JMenu(sIRC.i18n.getStr("menuBar.Server"));
		
		// Tooltip for menu items
		fileMenu.setToolTipText(sIRC.i18n.getStr("tooltip.File"));
		toolsMenu.setToolTipText(sIRC.i18n.getStr("tooltip.Tools"));
		helpMenu.setToolTipText(sIRC.i18n.getStr("tooltip.Help"));
		serverMenu.setToolTipText(sIRC.i18n.getStr("tooltip.Server"));
		
		serverMenu.addMenuListener(this);

		// Items for File menu
		JMenuItem fileExit = Helpers.createMenuItem("fileMenu.Exit", "exit",
				"tooltip.Exit", fileMenu, this);
		// Items for Edit menu 
		Helpers.createMenuItem("editMenu.Options", "options", "tooltip.Options", toolsMenu, this);
		// Items for Help
		Helpers.createMenuItem("helpMenu.Help", "help", "tooltip.Help", helpMenu, this);
		Helpers.createMenuItem("helpMenu.About", "about", "tooltip.About", helpMenu, this);
		
		// Items for Server menu
		Helpers.createMenuItem("serverMenu.Disconnect", "disconnect", "tooltip.Disconnect", serverMenu, this);
		Helpers.createMenuItem("serverMenu.Connect", "connect", "tooltip.Connect", serverMenu, this);
		Helpers.createMenuItem("serverMenu.JoinAChannel", "joinAChannel", "tooltip.JoinAChannel", serverMenu, this);
		Helpers.createMenuItem("serverMenu.ListOfChannels", "listOfChannels", "tooltip.ListOfChannels", serverMenu, this);
		marked_away = Helpers.createCheckBoxMenuItem("serverMenu.MarkedAway", "markedAway", "tooltip.MarkedAway", serverMenu, this, false);
		
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


	@Override
	public void actionPerformed(ActionEvent e) {
		String event = e.getActionCommand();
		
		if (event.equals("exit"))
			System.exit(0);
		else if (event.equals("options"))
			sIRC.options.showWindow();
		else if (event.equals("help")) {
			sIRC.options.showWindow();
			sIRC.options.setViewHelp(Options.CONNECTIONHELP);
		} else if (event.equals("disconnect"))
			sIRC.conManagement.disConnect(null);
		else if (event.equals("connect")) {
			sIRC.conManagement.connect(sIRC.options.getNick(), sIRC.options.getServer());
		} 
		
		else if (event.equals("joinAChannel")) {
			String channel = (String) JOptionPane.showInputDialog(jf, sIRC.i18n.getStr("enterChannel"), "Join channel",
					         JOptionPane.QUESTION_MESSAGE);
			
			if (channel != "" && channel != null)
				sIRC.conManagement.joinChannel(channel);
		} else if (event.equals("markedAway")) {
			if(sIRC.conManagement.isConnected()) {
				if(marked_away.isSelected()) {
					String awayMsg = (String) JOptionPane.showInputDialog(jf, sIRC.i18n.getStr("setAwayMsg"), "Set away message", 						
						     JOptionPane.QUESTION_MESSAGE);
					sIRC.conManagement.away(awayMsg);
				} 
				else {
					sIRC.conManagement.away(null);
				}
			} else {
				JOptionPane.showMessageDialog(jf, sIRC.i18n.getStr("error.setAwayDisconnected"));
				marked_away.setSelected(false);
			}
		} else if (event.equals("about")) {
			sIRC.options.showWindow();
			sIRC.options.setViewHelp(Options.ABOUTHELP);
			
		} else {
			JOptionPane.showMessageDialog(null, sIRC.i18n.getStr("notFound.Text"));
		}
	}

	@Override
	public void menuSelected(MenuEvent e) {
		if(sIRC.conManagement.isConnected()) {
			marked_away.setSelected(sIRC.conManagement.getSession().isAway());
		}
	}

	@Override
	public void menuDeselected(MenuEvent e) {}
	@Override
	public void menuCanceled(MenuEvent e) {	}
}