package no.hig.sss.sirc;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * This class handles the dialog for getting the connection options for an IRC session.
 * 
 * @author oeivindk
 *
 */
@SuppressWarnings("serial")
public class OptionsServer extends JPanel implements TreeSelectionListener {
	private static ResourceBundle messages;
	
	JButton add, change, delete, sort, ok, cancel, help;
	private JComboBox<String> networks, servers;
	private GridBagLayout layout = new GridBagLayout();
	private GridBagConstraints gbc = new GridBagConstraints();
	private String dummyNetworks[] = { "Velg et nettverk", "Nettverk 1" ,"Nettverk 2" };
	private String dummyServers[] = { "Random EU Undernet server", "Server med virkelig langt navn" };
	private ConnectionOptionsPrefs cop;
	
	JTree tree;
	DefaultTreeModel treeModel;
	
	/**
	 * Constructor for the class, handle all GUI layout and fill inn initial values
	 * 
	 */
	
	public OptionsServer () {
		
		cop = new ConnectionOptionsPrefs();
		
		cop.load();
		
		
		
		
		
		setLayout (layout);
		
		DefaultMutableTreeNode tRoot = new DefaultMutableTreeNode("root");
		DefaultMutableTreeNode tConn = new DefaultMutableTreeNode("Connection");
	    DefaultMutableTreeNode tStyle = new DefaultMutableTreeNode("Style");
	    DefaultMutableTreeNode tPersonal = new DefaultMutableTreeNode("Personal");
	    DefaultMutableTreeNode tServer = new DefaultMutableTreeNode("Server");
	    DefaultMutableTreeNode tColor = new DefaultMutableTreeNode("Color");
	    DefaultMutableTreeNode tText = new DefaultMutableTreeNode("Text");
	    
	    treeModel = new DefaultTreeModel(tRoot);
	    
	    tree = new JTree(treeModel);
	    
	    treeModel.insertNodeInto(tStyle, tRoot, 0);
	    treeModel.insertNodeInto(tConn, tRoot, 0);
	    tree.expandPath(tree.getPathForRow(0)); // expands path 1
	    tree.setRootVisible(false); // Hides root node
	    
	    
	    tConn.add(tPersonal);
	    tConn.add(tServer);
	    tStyle.add(tColor);
	    tStyle.add(tText);	    
	    
	    tree.addTreeSelectionListener(this);
		
	    addServers(tRoot);
	    
	    add (2,2,0,0, tree);
		
	    gbc.anchor = GridBagConstraints.CENTER;
		// Create the panel with the four buttons on the right
		JPanel alterServerButtons = new JPanel ();
		alterServerButtons.setLayout (new GridLayout (4,1));
		alterServerButtons.add (add = new JButton (messages.getString("connectionOptions.button.add.buttonText")));
		alterServerButtons.add (change= new JButton (messages.getString("connectionOptions.button.change.buttonText")));
		alterServerButtons.add (delete = new JButton (messages.getString("connectionOptions.button.delete.buttonText")));
		alterServerButtons.add (sort = new JButton (messages.getString("connectionOptions.button.sort.buttonText")));
		// Add the buttons on the right
		add (3, 1, 1, 3, alterServerButtons);
		
		// Add ok, cancel, help buttons
		JPanel okCancelHelpPanel = new JPanel ();
		okCancelHelpPanel.setLayout (new GridLayout(1, 3));
		okCancelHelpPanel.add (ok = new JButton (messages.getString("button.ok.buttonText")));
		okCancelHelpPanel.add (cancel = new JButton (messages.getString("button.cancel.buttonText")));
		okCancelHelpPanel.add (help = new JButton (messages.getString("button.help.buttonText")));
		gbc.anchor = GridBagConstraints.CENTER;
		add (1, 9, 3, 1, okCancelHelpPanel);
		
		// Set all tooltip texts
		add.setToolTipText(messages.getString("connectionOptions.button.add.tooltip"));
		change.setToolTipText(messages.getString("connectionOptions.button.change.tooltip"));
		delete.setToolTipText(messages.getString("connectionOptions.button.delete.tooltip"));
		sort.setToolTipText(messages.getString("connectionOptions.button.sort.tooltip"));
		ok.setToolTipText(messages.getString("connectionOptions.button.ok.tooltip"));
		cancel.setToolTipText(messages.getString("connectionOptions.button.cancel.tooltip"));
		help.setToolTipText(messages.getString("connectionOptions.button.help.tooltip"));
		
//		pack ();
		setVisible(true);
	}
	
	/**
	 * Used to place components/panels in a single cell, take the x, y and component to be placed as a parameter
	 * 
	 * @param x the column for this component
	 * @param y the row for this component
	 * @param c the component to add to the layout
	 */
	private void add (int x, int y, Component c) {
		add (x, y, 1, 1, c);
	}
	
	/**
	 * Used to add a panel/component to the layout, just take the x, y, width and height as well as the
	 * component to be placed as a parameter
	 * 
	 * @param x the column for this component
	 * @param y the row for this component
	 * @param width the number of columns this component will spawn
	 * @param height the number of rows this component will spawn
	 * @param c the component to add to the layout
	 */
	private void add (int x, int y, int width, int height, Component c) {
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		layout.setConstraints(c, gbc);
		add (c);
	}

	/**
	 * @param messages the messages to set
	 */
	public static void setMessages(ResourceBundle messages) {
		OptionsServer.messages = messages;
	}



	/**
	 * Test function for the class.
	 * 
	 * @param args
	 */
	
	public static void main(String[] args) {
		ConnectionOptions.setMessages (ResourceBundle.getBundle ("i18n/I18N"));
		ConnectionOptions co = new ConnectionOptions ();
	//	co.setDefaultCloseOperation(EXIT_ON_CLOSE);
	//	co.pack ();
		co.setVisible(true);
	}

	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	private void addServers(DefaultMutableTreeNode t) {
		String[] networks = cop.getNetworks();
		
		for(String network : networks) {
			
			DefaultMutableTreeNode i = new DefaultMutableTreeNode(network);
			treeModel.insertNodeInto(i, t, 0);
			i.add(new DefaultMutableTreeNode("Test1"));
		}
		DefaultMutableTreeNode first = new DefaultMutableTreeNode("Test1");
		treeModel.insertNodeInto(first, t, 0);
		first.add(new DefaultMutableTreeNode("Test1"));
	}
}
