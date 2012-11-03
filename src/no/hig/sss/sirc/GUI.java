package no.hig.sss.sirc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.event.MenuListener;

class GUI {
	private ResourceBundle messages;
	private ActionListener menuListener;
	
	public GUI(ResourceBundle messages) {
		this.messages = messages;
		menuListener = new menuListener();
	}
	
	/**
	 * Creates the menubar and returns a menubar object
	 * 
	 * @return JMenuBar
	 */
	public JMenuBar menu() {
		// Create menu
		JMenuBar menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu(messages.getString("sIRC.menuBar.File"));
		JMenu toolsMenu = new JMenu(messages.getString("sIRC.menuBar.Tools"));
		JMenu helpMenu = new JMenu(messages.getString("sIRC.menuBar.Help"));

		// Tooltip for menu items
		fileMenu.setToolTipText(messages.getString("sIRC.tooltip.File"));
		toolsMenu.setToolTipText(messages.getString("sIRC.tooltip.Tools"));
		helpMenu.setToolTipText(messages.getString("sIRC.tooltip.Help"));

		/*
		// Set listeners for menu
		fileMenu.setMnemonic(KeyEvent.VK_F);
		editMenu.setMnemonic(KeyEvent.VK_E);
		helpMenu.setMnemonic(KeyEvent.VK_H); 
		*/

		// Items for File
		JMenuItem fileExit = createMenuItem("sIRC.fileMenu.Exit", "exit", "", "sIRC.tooltip.Exit");


		// Items for Edit menu bar
		JMenuItem editOptions = createMenuItem("sIRC.editMenu.Options", "options", "", "sIRC.tooltip.Options");
	
		// Items for help
		JMenuItem helpHelp = createMenuItem("sIRC.helpMenu.Help", "help", "", "sIRC.tooltip.Help");
		JMenuItem helpAbout = createMenuItem("sIRC.helpMenu.About", "about", "", "sIRC.tooltip.About");

		// Adding buttons to menu
		fileMenu.add(fileExit);
		
		toolsMenu.add(editOptions);

		helpMenu.add(helpHelp);
		helpMenu.add(helpAbout);

		// Adding main menu
		menuBar.add(fileMenu);
		menuBar.add(toolsMenu);
		menuBar.add(helpMenu); 

		return menuBar;
	}
	
	/**
	 * Creates our toolbar and returns the bar.
	 * 
	 * @return JToolbar
	 */
/*	private JToolBar toolBar() {
		JToolBar toolBar = new JToolBar("ToolTest", JToolBar.HORIZONTAL);

		// Create buttons for toolbar
		JButton newButton = createButton("new", "new.gif", "GBLEditor.tooltip.Create");
		JButton loadButton = createButton("load", "opendoc.gif", "GBLEditor.tooltip.Load");
		JButton saveButton = createButton("save", "save.gif", "GBLEditor.tooltip.Save");
		JButton exportButton = createButton("export", "exportjava.png", "GBLEditor.tooltip.Export");
		JButton addRowButton = createButton("addRow", "newrow.gif", "GBLEditor.tooltip.NewRow");
		JButton moveUpButton = createButton("moveUp", "moverowup.gif", "GBLEditor.tooltip.MoveUp");
		JButton moveDownButton = createButton("moveDown", "moverowdown.gif", "GBLEditor.tooltip.MoveDown");

		// Add the buttons
		toolBar.add(newButton);
		toolBar.add(loadButton);
		toolBar.add(saveButton);
		toolBar.add(exportButton);

		toolBar.addSeparator();

		toolBar.add(addRowButton);
		toolBar.add(moveUpButton);
		toolBar.add(moveDownButton);

		return toolBar;
	} */
	
	
	/* Begin helper functions */ 
	
	/**
	 * Creates JButton
	 * 
	 * @param cmd Action command
	 * @param icon Icon name
	 * 
	 * @return button JButton
	 */
/*	private JButton createButton(String cmd, String icon, String tooltip) {
		JButton button = new JButton();
		button.addActionListener(menuListener);
		button.setActionCommand(cmd);

		if (icon.length() > 1) {  // Set icon if we got one
			button.setIcon(new ImageIcon(getClass().getResource(
					"Images/Icons/" + icon)));
		}
		if (tooltip.length() > 2) { // Set tooltip if the string tooltip
									// contains something useful
			button.setToolTipText(messages.getString(tooltip));
		}
		
		return button;
	}*/
	
	/**
	 * Creates menu item
	 * 
	 * @param name Menu item name
	 * @param cmd Action command
	 * @param icon Icon name
	 * @param tooltip Tooltip for menu item
	 * 
	 * @return item JMenuItem
	 */
	private JMenuItem createMenuItem(String name, String cmd, String icon, String tooltip) {
		JMenuItem item = new JMenuItem(messages.getString(name));
		item.addActionListener(menuListener);

		if (icon.length() > 1) { // Set icon if we got one
			item.setIcon(new ImageIcon(getClass().getResource("Images/Icons/" + icon)));
		}
		item.setActionCommand(cmd);
		if (tooltip.length() > 2) { // Set tooltip if the string tooltip
									// contains something useful
			item.setToolTipText(messages.getString(tooltip));
		}

		return item;
	}
	
	class menuListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String event = e.getActionCommand();
			if (event == "exit")
				System.exit(0);
			else
				JOptionPane.showMessageDialog(null, messages.getString("sIRC.notFound.Text"));
		}

	}
}