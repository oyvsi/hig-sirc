package no.hig.sss.sirc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.MenuListener;

class GUI implements ActionListener {
	// private ActionListener menuListener;
	private boolean setAway;
	private JFrame jf;
	
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

		// Items for File
		JMenuItem fileExit = Helpers.createMenuItem("fileMenu.Exit", "exit",
				"tooltip.Exit", fileMenu, this);
		// Items for Edit menu bar
		JMenuItem editOptions = Helpers.createMenuItem("editMenu.Options",
				"options", "tooltip.Options", toolsMenu, this);
		// Items for help
		JMenuItem helpHelp = Helpers.createMenuItem("helpMenu.Help", "help",
				"tooltip.Help", helpMenu, this);
		JMenuItem helpAbout = Helpers.createMenuItem("helpMenu.About", "about",
				"tooltip.About", helpMenu, this);
		// Mnemonics
		fileExit.setMnemonic(KeyEvent.VK_Q);
		fileExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				ActionEvent.ALT_MASK));

		JMenuItem disconnect = Helpers.createMenuItem("serverMenu.Disconnect", "disconnect", "tooltip.Disconnect", serverMenu, this);
		JMenuItem reconnect = Helpers.createMenuItem("serverMenu.Reconnect", "reconnect", "tooltip.Reconnect", serverMenu, this);
		JMenuItem join_a_channel = Helpers.createMenuItem("serverMenu.JoinAChannel", "joinAChannel", "tooltip.JoinAChannel", serverMenu, this);
		JMenuItem list_of_channels = Helpers.createMenuItem("serverMenu.ListOfChannels", "listOfChannels", "tooltip.ListOfChannels", serverMenu, this);
		JMenuItem marked_away = Helpers.createCheckBoxMenuItem("serverMenu.MarkedAway", "markedAway", "tooltip.MarkedAway",serverMenu, this, false);

		// Adding main menu
		menuBar.add(fileMenu);
		menuBar.add(toolsMenu);
		menuBar.add(helpMenu);
		menuBar.add(serverMenu);
		return menuBar;
	}

	/**
	 * Creates our toolbar and returns the bar.
	 * 
	 * @return JToolbar
	 */
	/*
	 * private JToolBar toolBar() { JToolBar toolBar = new JToolBar("ToolTest",
	 * JToolBar.HORIZONTAL);
	 * 
	 * // Create buttons for toolbar JButton newButton = createButton("new",
	 * "new.gif", "GBLEditor.tooltip.Create"); JButton loadButton =
	 * createButton("load", "opendoc.gif", "GBLEditor.tooltip.Load"); JButton
	 * saveButton = createButton("save", "save.gif", "GBLEditor.tooltip.Save");
	 * JButton exportButton = createButton("export", "exportjava.png",
	 * "GBLEditor.tooltip.Export"); JButton addRowButton =
	 * createButton("addRow", "newrow.gif", "GBLEditor.tooltip.NewRow"); JButton
	 * moveUpButton = createButton("moveUp", "moverowup.gif",
	 * "GBLEditor.tooltip.MoveUp"); JButton moveDownButton =
	 * createButton("moveDown", "moverowdown.gif",
	 * "GBLEditor.tooltip.MoveDown");
	 * 
	 * // Add the buttons toolBar.add(newButton); toolBar.add(loadButton);
	 * toolBar.add(saveButton); toolBar.add(exportButton);
	 * 
	 * toolBar.addSeparator();
	 * 
	 * toolBar.add(addRowButton); toolBar.add(moveUpButton);
	 * toolBar.add(moveDownButton);
	 * 
	 * return toolBar; }
	 */

	/* Begin helper functions */

	/**
	 * Creates JButton
	 * 
	 * @param cmd
	 *            Action command
	 * @param icon
	 *            Icon name
	 * 
	 * @return button JButton
	 */
	/*
	 * private JButton createButton(String cmd, String icon, String tooltip) {
	 * JButton button = new JButton(); button.addActionListener(menuListener);
	 * button.setActionCommand(cmd);
	 * 
	 * if (icon.length() > 1) { // Set icon if we got one button.setIcon(new
	 * ImageIcon(getClass().getResource( "Images/Icons/" + icon))); } if
	 * (tooltip.length() > 2) { // Set tooltip if the string tooltip // contains
	 * something useful button.setToolTipText(sIRC.i18n.getStr(tooltip)); }
	 * 
	 * return button; }
	 */

	/**
	 * Creates menu item
	 * 
	 * @param name
	 *            Menu item name
	 * @param cmd
	 *            Action command
	 * @param icon
	 *            Icon name
	 * @param tooltip
	 *            Tooltip for menu item
	 * 
	 * @return item JMenuItem
	 */

	@Override
	public void actionPerformed(ActionEvent e) {
		String event = e.getActionCommand();
		if (event.equals("exit"))
			System.exit(0);
		else if (event.equals("options"))
			sIRC.options.showWindow();
		else if (event.equals("help"))
			sIRC.options.setViewHelp(0);
		else if (event.equals("disconnect"))
			sIRC.conManagement.disConnect(null);
		else if (event.equals("reconnect")) {
			//sIRC.options.connect();
		} else if (event.equals("joinAChannel")) {
				String channel = (String) JOptionPane.showInputDialog(jf, sIRC.i18n.getStr("enterChannel"), "Join channel",
						JOptionPane.QUESTION_MESSAGE);
				if (channel != "" && channel != null) {
					sIRC.conManagement.joinChannel(channel);
				}
		} else if (event.equals("MarkedAway")) {
			sIRC.conManagement.away(null);
			//boolean setAway = marked_away.getState();
			//if (setAway && !connection.isAway()) {
			//	connection.setAway("Away");
			//} else if (!setAway && connection.isAway())
			//	connection.unSetAway();
		}

		else
			JOptionPane.showMessageDialog(null,
					sIRC.i18n.getStr("notFound.Text"));
	}
}