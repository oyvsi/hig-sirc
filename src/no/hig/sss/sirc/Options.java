package no.hig.sss.sirc;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import java.util.*;

//SplitPaneDemo itself is not a visible component.
public class Options extends JPanel implements TreeSelectionListener {
	private static final long serialVersionUID = 1L;
	private JSplitPane splitPane;
	OptionsPersonal op;
	OptionsServer os;
	private JFrame jf;
	private JTree tree;
	private DefaultTreeModel treeModel;

	
	private final String[] helpPaths = {
			"File:help/conn_help.html", 
			"File:help/usage_help.html", 
			"File:help/command_help.html", 
			"File:help/style_help.html",
			"File:help/server_help.html",
			"File:help/about_help.html" };
	
	public static final int CONNECTIONHELP = 0;
	public static final int USAGEHELP = 1;
	public static final int COMMANDHELP = 2;
	public static final int STYLEHELP = 3;
	public static final int SERVERHELP = 4;
	public static final int ABOUTHELP = 5;
	
	public static TextOptions channelFormat;
	public static TextOptions pmFormat;
	public static TextOptions infoFormat;
	public static TextOptions consoleFormat;
	
	// Size for option window
	private final int WHEIGHT = 300;
	private final int WWIDTH = 720;

	public Options() {
		// Makes all the nodes for the JTree
		DefaultMutableTreeNode tRoot = new DefaultMutableTreeNode("root");
		DefaultMutableTreeNode tConn = new DefaultMutableTreeNode("Connection");
		DefaultMutableTreeNode tStyle = new DefaultMutableTreeNode("Style");
		DefaultMutableTreeNode tPersonal = new DefaultMutableTreeNode("Personal");
		DefaultMutableTreeNode tServer = new DefaultMutableTreeNode("Server");
		DefaultMutableTreeNode tCmFormat = new DefaultMutableTreeNode("Channel messages");
		DefaultMutableTreeNode tPmFormat = new DefaultMutableTreeNode("Private messages");
		DefaultMutableTreeNode tInfoFormat = new DefaultMutableTreeNode("Info messages");
		DefaultMutableTreeNode tConFormat = new DefaultMutableTreeNode("Console messages");

		DefaultMutableTreeNode tHelp = new DefaultMutableTreeNode("Help");
		DefaultMutableTreeNode tHelpConn = new DefaultMutableTreeNode("Connection Help");
		DefaultMutableTreeNode tHelpUsage = new DefaultMutableTreeNode("Usage Help");
		DefaultMutableTreeNode tHelpCommands = new DefaultMutableTreeNode("Commands");
		DefaultMutableTreeNode tHelpStyle = new DefaultMutableTreeNode("Style");
		DefaultMutableTreeNode tHelpServer = new DefaultMutableTreeNode("Server Help");
		DefaultMutableTreeNode tHelpAbout = new DefaultMutableTreeNode("About");

		// makes treemodel around root and makes tree with treemodel
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
	
	void setViewPersonal(String selectedServer) {
		if (os != null) {
			if (os.getSelectedServer() != null) // Don't want to grab new server if its nothing
				op.setSelectedServer(os.getSelectedServer()); // Set server
		} else {
				op = new OptionsPersonal("irc.quakenet.org"); // Std server
		}
		splitPane.setRightComponent(op);
	}

	void setViewServer() {
		os.loadServers();
		splitPane.setRightComponent(os);
	}

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

	void setViewHelp(int view) {
		splitPane.setRightComponent(new SIRCHelp(helpPaths[view]));
		// Selects the right help node in the tree, sends the path to the node.
		if(helpPaths[view].contains("style"))
			selectNode(tree, new TreePath(treeModel.getRoot()), "[root, Help, Style]");
		else if(helpPaths[view].contains("conn"))
			selectNode(tree, new TreePath(treeModel.getRoot()), "[root, Help, Connection Help]");
		else if(helpPaths[view].contains("server"))
			selectNode(tree, new TreePath(treeModel.getRoot()), "[root, Help, Server Help]");
		else if(helpPaths[view].contains("about"))
			selectNode(tree, new TreePath(treeModel.getRoot()), "[root, Help, About]");
	}

	public JSplitPane getSplitPane() {
		return splitPane;
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	public void createAndShowGUI(JFrame jff) {
		// Create and set up the window.
		jf = new JFrame("sIRC Options");
		jf.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
		// Icon for JFrame
		ImageIcon img = new ImageIcon("icon.png");
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
		
		jf.setLocationRelativeTo(jff);
		//jf.setAlwaysOnTop(false);
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if (node == null)
			return;
		if (node.isLeaf()) {
			switch (node.toString()) {
			case "Personal":
				setViewPersonal(null);
				break;

			case "Server":
				setViewServer();
				break;

			case "Channel messages":
				setViewFormat(TabComponent.CHANNEL);
				break;

			case "Private messages":
				setViewFormat(TabComponent.PM);
				break;
			case "Info messages":
				setViewFormat(TabComponent.INFO);
				break;
			case "Console messages":
				setViewFormat(TabComponent.CONSOLE);
				break;
			case "Connection Help":
				setViewHelp(CONNECTIONHELP);
				break;
			case "Usage Help":
				setViewHelp(USAGEHELP);
				break;
			case "Commands":
				setViewHelp(COMMANDHELP);
				break;
			case "Style":
				setViewHelp(STYLEHELP);
				break;
			case "Server Help":
				setViewHelp(SERVERHELP);
				break;
			case "About":
				setViewHelp(ABOUTHELP);
				break;
			}
		}
	}

	public void hideWindow(Boolean saveOptions) {
		jf.setVisible(false);
		if (saveOptions)
			saveOptions();
		else
			loadOptions();
	}

	public void showWindow() {
		loadOptions();
		jf.setVisible(true);
	}

	private void saveOptions() {
		File file = new File("config.ini");
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			op.save().store(fos, "Personal Settings");
			channelFormat.save().store(fos, "Channel Messages");
			pmFormat.save().store(fos, "Private Messages");
			infoFormat.save().store(fos, "Info Messages");
			consoleFormat.save().store(fos, "Console Messages");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void loadOptions() {
		File file = new File("config.ini");
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getNick() {
		return op.getNickname();
	}

	public void setNick(String nick) {
		op.setNick(nick);
		saveOptions();
	}

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
		// tree.collapsePath(parent);
	}

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

	public String getServer() {
		return op.getSelectedServer();
	}
	
	public String getAltNick() {
		return op.getAltnick();
	}
	
	public String getUserName() {
		return op.getUserName();
	}
}