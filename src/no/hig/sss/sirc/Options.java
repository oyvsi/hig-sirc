package no.hig.sss.sirc;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import java.util.*;

/**
 * This class makes the JPanel for for options, including the splitpane, and the navigation tree on the left.
 * 
 * @author Oyvind Sigerstad, Nils Slaaen, Bjorn-Erik Strand
 * 
 */
public class Options extends JPanel implements TreeSelectionListener {
	private static final long serialVersionUID = 1L;
	private JSplitPane splitPane;
	private OptionsPersonal op;
	private JFrame jf;
	private JTree tree;
	private DefaultTreeModel treeModel;

	public OptionsServer os;
	
	// Size for option window
	private final int WHEIGHT = 300;
	private final int WWIDTH = 720;
	
	private final String[] helpPaths = {
			"help/conn_help.html", 
			"help/usage_help.html", 
			"help/command_help.html", 
			"help/style_help.html",
			"help/server_help.html",
			"help/rightclick_help.html",
			"help/about_help.html",
			};
	
	// Used to identify which help file to use
	public static final int CONNECTIONHELP = 0;
	public static final int USAGEHELP = 1;
	public static final int COMMANDHELP = 2;
	public static final int STYLEHELP = 3;
	public static final int SERVERHELP = 4;
	public static final int POPUPHELP = 5;
	public static final int ABOUTHELP = 6;
	
	// Used to identify which text options to use
	public static TextOptions channelFormat;
	public static TextOptions pmFormat;
	public static TextOptions infoFormat;
	public static TextOptions consoleFormat;

	/**
	 * Class constructor, creates JTree, Tree model, puts nodes in tree,
	 * creates split pane for other views
	 */
	public Options() {
		// Makes all the nodes for the JTree
		DefaultMutableTreeNode tRoot = new DefaultMutableTreeNode(sIRC.i18n.getStr("tRoot"));
		DefaultMutableTreeNode tConn = new DefaultMutableTreeNode(sIRC.i18n.getStr("tConn"));
		DefaultMutableTreeNode tStyle = new DefaultMutableTreeNode(sIRC.i18n.getStr("tStyle"));
		DefaultMutableTreeNode tPersonal = new DefaultMutableTreeNode(sIRC.i18n.getStr("tPersonal"));
		DefaultMutableTreeNode tServer = new DefaultMutableTreeNode(sIRC.i18n.getStr("tServer"));
		DefaultMutableTreeNode tCmFormat = new DefaultMutableTreeNode(sIRC.i18n.getStr("tCmFormat"));
		DefaultMutableTreeNode tPmFormat = new DefaultMutableTreeNode(sIRC.i18n.getStr("tPmFormat"));
		DefaultMutableTreeNode tInfoFormat = new DefaultMutableTreeNode(sIRC.i18n.getStr("tInfoFormat"));
		DefaultMutableTreeNode tConFormat = new DefaultMutableTreeNode(sIRC.i18n.getStr("tConFormat"));

		DefaultMutableTreeNode tHelp = new DefaultMutableTreeNode(sIRC.i18n.getStr("tHelp"));
		DefaultMutableTreeNode tHelpConn = new DefaultMutableTreeNode(sIRC.i18n.getStr("tHelpConn"));
		DefaultMutableTreeNode tHelpUsage = new DefaultMutableTreeNode(sIRC.i18n.getStr("tHelpUsage"));
		DefaultMutableTreeNode tHelpCommands = new DefaultMutableTreeNode(sIRC.i18n.getStr("tHelpCommands"));
		DefaultMutableTreeNode tHelpStyle = new DefaultMutableTreeNode(sIRC.i18n.getStr("tHelpStyle"));
		DefaultMutableTreeNode tHelpServer = new DefaultMutableTreeNode(sIRC.i18n.getStr("tHelpServer"));
		DefaultMutableTreeNode tHelpPopup = new DefaultMutableTreeNode(sIRC.i18n.getStr("tHelpPopup"));
		DefaultMutableTreeNode tHelpAbout = new DefaultMutableTreeNode(sIRC.i18n.getStr("tHelpAbout"));

		// makes tree model around root and makes tree with tree model
		treeModel = new DefaultTreeModel(tRoot);
		tree = new JTree(treeModel);

		// Adds node to root node
		treeModel.insertNodeInto(tHelp, tRoot, 0);
		treeModel.insertNodeInto(tStyle, tRoot, 0);
		treeModel.insertNodeInto(tConn, tRoot, 0);

		// First level expand not needed when expands whole tree
		//tree.expandPath(tree.getPathForRow(0)); // expands first level
		tree.setRootVisible(false); // Hides root node

		// Adds option nodes to their parents
		tConn.add(tPersonal);
		tConn.add(tServer);
		tStyle.add(tCmFormat);
		tStyle.add(tPmFormat);
		tStyle.add(tInfoFormat);
		tStyle.add(tConFormat);
		tHelp.add(tHelpConn);
		tHelp.add(tHelpUsage);
		tHelp.add(tHelpCommands);
		tHelp.add(tHelpStyle);
		tHelp.add(tHelpServer);
		tHelp.add(tHelpPopup);
		tHelp.add(tHelpAbout);

		// Expands all node in the tree
		expandAll(tree, new TreePath(tRoot));

		// Set selection listener for node target changes
		tree.addTreeSelectionListener(this);

		// Create a split pane with the two scroll panes in it.
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(150);

		tree.setMinimumSize(new Dimension(150, 300));
		splitPane.setLeftComponent(tree);

		splitPane.setPreferredSize(new Dimension(WWIDTH, WHEIGHT));
		splitPane.setMinimumSize(new Dimension(WWIDTH, WHEIGHT));

		setViewPersonal(null);

		os = new OptionsServer();

		infoFormat = new TextOptions("Info");
		infoFormat.setBorder(null);
		infoFormat.setPreferredSize(new Dimension(800, 600));

		pmFormat = new TextOptions("PM");
		pmFormat.setBorder(null);
		pmFormat.setPreferredSize(new Dimension(800, 600));

		channelFormat = new TextOptions("Channel");
		channelFormat.setBorder(null);
		channelFormat.setPreferredSize(new Dimension(800, 600));

		consoleFormat = new TextOptions("Console");
		consoleFormat.setBorder(null);
		consoleFormat.setPreferredSize(new Dimension(800, 600));

		loadOptions();
	}
	
	/**
	 * Sets the view in options to personal
	 * @param selectedServer
	 */
	public void setViewPersonal(String selectedServer) {
		if (os != null) {
			if (os.getSelectedServer() != null) // Don't want to grab new server if its nothing
				op.setSelectedServer(os.getSelectedServer()); // Set server
		} else {
				op = new OptionsPersonal("irc.quakenet.org"); // Std server
		}
		splitPane.setRightComponent(op);
	}

	/**
	 * Sets the view in options to server also loads servers.
	 */
	public void setViewServer() {
		os.loadServers();
		splitPane.setRightComponent(os);
	}

	/**
	 * Sets the view in options to help based on which help section based on parm
	 * @param view
	 */
	public void setViewHelp(int view) {
		splitPane.setRightComponent(new SIRCHelp(helpPaths[view]));
		// Selects the right help node in the tree, sends the path to the node.
		if(helpPaths[view].contains("style"))
			selectNode(tree, new TreePath(treeModel.getRoot()), sIRC.i18n.getStr("cStyle"));
		else if(helpPaths[view].contains("conn"))
			selectNode(tree, new TreePath(treeModel.getRoot()), sIRC.i18n.getStr("cConn"));
		else if(helpPaths[view].contains("server"))
			selectNode(tree, new TreePath(treeModel.getRoot()), sIRC.i18n.getStr("cServer"));
		else if(helpPaths[view].contains("about"))
			selectNode(tree, new TreePath(treeModel.getRoot()), sIRC.i18n.getStr("cAbout"));
	}

	/**
	 * Getter for split pane
	 * @return
	 */
	public JSplitPane getSplitPane() {
		return splitPane;
	}

	/**
	 * Create the GUI for Options and shows it.
	 */
	public void createAndShowGUI() {
		// Create and set up the window.
		jf = new JFrame("sIRC Options");
		jf.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
		// Icon for JFrame
		URL myurl = this.getClass().getResource("Resources/icon.png");
		ImageIcon img = new ImageIcon(this.getToolkit().getImage(myurl));
		jf.setIconImage(img.getImage());
		
		
		jf.getContentPane().add(getSplitPane());
		
		// Set start position for options
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int X = (screen.width / 2) - (WWIDTH / 2); // Center horizontally.
		int Y = (screen.height / 2) - (WHEIGHT / 2); // Center vertically.

		jf.setBounds(X, Y, WWIDTH, WHEIGHT);
		
		// Display the window.
		jf.pack();
		jf.setResizable(false);
		jf.setVisible(true);
	}

	/**
	 * Handler for tree selection, calls appropriate setView functions
	 * @param e
	 */
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if ((node == null) || !(node.isLeaf()))
			return;
		switch (node.toString()) {
			case "Personal":			setViewPersonal(null); break;
			case "Server": 				setViewServer(); break;
			case "Channel messages": 	setViewFormat(TabComponent.CHANNEL); break;
			case "Private messages": 	setViewFormat(TabComponent.PM); break;
			case "Info messages": 		setViewFormat(TabComponent.INFO); break;
			case "Console messages": 	setViewFormat(TabComponent.CONSOLE); break;
			case "Connection Help": 	setViewHelp(CONNECTIONHELP); break;
			case "Usage Help": 			setViewHelp(USAGEHELP); break;
			case "Commands": 			setViewHelp(COMMANDHELP); break;
			case "Style": 				setViewHelp(STYLEHELP); break;
			case "Server Help": 		setViewHelp(SERVERHELP); break;
			case "Popup Help":		    setViewHelp(POPUPHELP); break;
			case "About": 				setViewHelp(ABOUTHELP); break;
		}
	}

	/**
	 * Hides options window, and saves options based on param
	 * @param saveOptions
	 */
	public void hideWindow(Boolean saveOptions) {
		jf.setVisible(false);
		if (saveOptions)
			saveOptions();
		else
			loadOptions();
	}

	/**
	 * Sets options to visible
	 */
	public void showWindow() {
		loadOptions();
		jf.setVisible(true);
	}
	
	/**
	 * Getter for selected server
	 * @return selected server
	 */
	public String getServer() {
		return op.getSelectedServer();
	}
	
	/**
	 * Getter for alt nick
	 * @return altnick
	 */
	public String getAltNick() {
		return op.getAltnick();
	}
	
	/**
	 * Getter for user name 
	 * @return userName
	 */
	public String getUserName() {
		return op.getUserName();
	}
	
	/**
	 * Getter for nickname
	 * @return nickName
	 */
	public String getNick() {
		return op.getNickname();
	}

	/**
	 * Setter for nickname
	 * @param nick
	 */
	public void setNick(String nick) {
		op.setNick(nick);
		saveOptions();
	}

	/**
	 * Saves options to prefs file
	 */
	private void saveOptions() {
		File file = Helpers.getFile("config.ini");
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			op.save().store(fos, "Personal Settings");
			channelFormat.save().store(fos, "Channel Messages");
			pmFormat.save().store(fos, "Private Messages");
			infoFormat.save().store(fos, "Info Messages");
			consoleFormat.save().store(fos, "Console Messages");

		} catch (FileNotFoundException e) {
			System.out.println("The config file could not be opened");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error saving to config file");
			e.printStackTrace();
		}
	}

	/**
	 * Loads options from prefs file
	 */
	private void loadOptions() {
		File file = Helpers.getFile("config.ini");
		FileInputStream fis;
		Properties pro;
		try {
			fis = new FileInputStream(file);
			pro = new Properties();
			pro.load(fis);
			op.load(pro);
			channelFormat.load(pro);
			pmFormat.load(pro);
			infoFormat.load(pro);
			consoleFormat.load(pro);
			fis.close();
		} catch (FileNotFoundException e) {
			System.out.println("The config file could not be opened");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error loading config file");
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets the view in options to Format based on what to edit based on parm
	 * @param view
	 */
	private void setViewFormat(int view) {
		if (view == TabComponent.CHANNEL)
			splitPane.setRightComponent(channelFormat);
		else if (view == TabComponent.PM)
			splitPane.setRightComponent(pmFormat);
		else if (view == TabComponent.CONSOLE)
			splitPane.setRightComponent(consoleFormat);
		else if (view == TabComponent.INFO)
			splitPane.setRightComponent(infoFormat);
	}

	/**
	 * Recursive function to expand all tree nodes under specific parent of tree
	 * @param tree
	 * @param parent
	 */
	private void expandAll(JTree tree, TreePath parent) {
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandAll(tree, path);
				if(path.toString().equals("[root, Connection, Personal]")) {
					tree.setSelectionPath(path);
				}
			}
		}
		tree.expandPath(parent);
		// tree.collapsePath(parent);	// If we want collapse instead
	}

	/**
	 * Recursive function to select spesific tree node based on path to treenode to select
	 * @param tree
	 * @param parent
	 * @param exp
	 */
	private void selectNode(JTree tree, TreePath parent, String exp) {
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				selectNode(tree, path, exp);
				if(path.toString().equals(exp)) {
					tree.setSelectionPath(path);
				}
			}
		}
	}
}