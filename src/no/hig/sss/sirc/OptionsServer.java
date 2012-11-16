package no.hig.sss.sirc;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.List;
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
import javax.swing.JScrollPane;
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
	private ConnectionOptionsPrefs cop;
	private ArrayList<OptionsServerPrefs> osp;
	
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
	    
	    treeModel = new DefaultTreeModel(tRoot);
	    tree = new JTree(treeModel);
	    
	    addServers(tRoot);
	    tree.expandPath(tree.getPathForRow(0)); // expands path 1
	    tree.setRootVisible(false); // Hides root node
	    
	    tree.addTreeSelectionListener(this);
		
	    
	    
	    JScrollPane sp = new JScrollPane(tree);
	    sp.setPreferredSize(new Dimension(200,200));
	    //sp.setMaximumSize(new Dimension(150,200));
	    add (1,1,1,1, sp);
		
	    gbc.anchor = GridBagConstraints.CENTER;
		// Create the panel with the four buttons on the right
		JPanel alterServerButtons = new JPanel ();
		alterServerButtons.setLayout (new GridLayout (4,1));
		alterServerButtons.add (add = new JButton (messages.getString("connectionOptions.button.add.buttonText")));
		alterServerButtons.add (change= new JButton (messages.getString("connectionOptions.button.change.buttonText")));
		alterServerButtons.add (delete = new JButton (messages.getString("connectionOptions.button.delete.buttonText")));
		alterServerButtons.add (sort = new JButton (messages.getString("connectionOptions.button.sort.buttonText")));
		// Add the buttons on the right
		add (2, 0, 1, 3, alterServerButtons);
		
		// Add ok, cancel, help buttons
		JPanel okCancelHelpPanel = new JPanel ();
		okCancelHelpPanel.setLayout (new GridLayout(1, 3));
		okCancelHelpPanel.add (ok = new JButton (messages.getString("button.ok.buttonText")));
		okCancelHelpPanel.add (cancel = new JButton (messages.getString("button.cancel.buttonText")));
		okCancelHelpPanel.add (help = new JButton (messages.getString("button.help.buttonText")));
		gbc.anchor = GridBagConstraints.CENTER;
		add (0, 5, 3, 1, okCancelHelpPanel);
		
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
	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                tree.getLastSelectedPathComponent();
		if (node == null) return;
        if (node.isLeaf()) {
    		switch (node.toString()) {
    		case "Personal":
    			break;
    			
    		case "Server":
    			break;

    		case "Text":
    			break;
    		
        	case "Color":
        		break;
    		}
    	System.out.print(node.toString());
        }
	}
	
	/** Takes string that looks like 6661-6669GROUP, or 6665-6668,7000GROUP
	 * 
	 * @param raw
	 * @return array of ints
	 */
	private int[] toPort(String raw) {
		ArrayList<Integer> al = new ArrayList<Integer>();
		String[] temp;
		String[] temp2;
		temp = raw.split("G");
		
		temp = temp[0].split(",");
		for(String temp3 : temp) {
			if(temp3.contains("-")) {
				temp2 = temp3.split("-");
				for(int i = Integer.valueOf(temp2[0]); i >= Integer.valueOf(temp2[1]); i++) {
					al.add(i);
				}
			} else {
				al.add(Integer.valueOf(temp3));
			}
		}
	    int[] ports = new int[al.size()];
	    for(int i = 0; i < al.size(); i++) {
            ports[i] = al.get(i).intValue();
        }
	    for(int port : ports) {
	    	System.out.print(port);
	    }
		return ports;
		
	}
	
	private void addServers(DefaultMutableTreeNode t) {
		String[] networks = cop.getNetworks();
		String[] tempN;
		String[] tempS;
		
		osp = new ArrayList<OptionsServerPrefs>();
		OptionsServerPrefs tempOsp;
		String[] servers = cop.getServers();
		
		
		int[] port;
		
		for(String server : servers) {
			if(!server.isEmpty()) {
				tempS = server.split(":");
				port = toPort(tempS[2]);
				tempOsp = new OptionsServerPrefs(tempS[1], tempS[1],tempS[3], port);
				osp.add(tempOsp);
				tempOsp = osp.get(0);
				for(int portsss : tempOsp.getPort()) {
					System.out.print(portsss);
				}
			}
		}
		
		for(String network : networks) {
			if(!network.isEmpty()){
				tempN = network.split("=");
				DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode(tempN[1]);
				treeModel.insertNodeInto(dmtn, t, 0);
				
				for(int i = 0; i < osp.size(); i++ ) {
					if(osp.get(i).getServerGroup().equals(tempN[1])) {
						dmtn.add(new DefaultMutableTreeNode(osp.get(i).getServerName()));
					}
				}
			}
		}
	}
}
