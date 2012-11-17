package no.hig.sss.sirc;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.List;
import java.awt.event.ActionEvent;
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
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * This class handles the dialog for getting the connection options for an IRC session.
 * 
 * @author oeivindk
 *
 */
@SuppressWarnings("serial")
public class OptionsServer extends JPanel implements TreeSelectionListener, ActionListener{
	private static ResourceBundle messages;
	
	JButton add, change, delete, ok, cancel, help;
	private ConnectionOptionsPrefs cop;
	private ArrayList<OptionsServerPrefs> osp;
	
	private BorderLayout bl;
	
	JTree tree;
	DefaultTreeModel treeModel;
	
	/**
	 * Constructor for the class, handle all GUI layout and fill inn initial values
	 * 
	 */
	
	public OptionsServer () {
		
		bl = new BorderLayout();
		cop = new ConnectionOptionsPrefs();
		
		cop.load();
		
		
		
		
		setLayout(bl);
		
		DefaultMutableTreeNode tRoot = new DefaultMutableTreeNode("root");
	    
	    treeModel = new DefaultTreeModel(tRoot);
	    tree = new JTree(treeModel);
	    
	    addServers(tRoot);
	    tree.expandPath(tree.getPathForRow(0)); // expands path 1
	    tree.setRootVisible(false); // Hides root node
	    
	    tree.addTreeSelectionListener(this);
	    
	    JScrollPane sp = new JScrollPane(tree);
	    sp.setPreferredSize(new Dimension(150,200));
	    add(new JLabel("Server settings"), BorderLayout.NORTH);
	    
	    add(sp, BorderLayout.CENTER);
		// Create the panel with the four buttons on the right
		JPanel alterServerButtons = new JPanel ();
		alterServerButtons.setLayout (new GridLayout (3,1));
		alterServerButtons.add (add = new JButton (messages.getString("connectionOptions.button.add.buttonText")));
		add.addActionListener(this);

		alterServerButtons.add (change= new JButton (messages.getString("connectionOptions.button.change.buttonText")));
		change.addActionListener(this);
		alterServerButtons.add (delete = new JButton (messages.getString("connectionOptions.button.delete.buttonText")));
		delete.addActionListener(this);
		// Add the buttons on the right
		alterServerButtons.setSize(new Dimension(200,60));
		add(alterServerButtons, BorderLayout.EAST);
		// Add ok, cancel, help buttons
		JPanel okCancelHelpPanel = new JPanel ();
		okCancelHelpPanel.setLayout (new GridLayout(1, 2));
		okCancelHelpPanel.add (ok = new JButton (messages.getString("button.ok.buttonText")));
		okCancelHelpPanel.add (cancel = new JButton (messages.getString("button.cancel.buttonText")));
		okCancelHelpPanel.add (help = new JButton (messages.getString("button.help.buttonText")));

		add(okCancelHelpPanel, BorderLayout.SOUTH);
		// Set all tooltip texts
		add.setToolTipText(messages.getString("connectionOptions.button.add.tooltip"));
		change.setToolTipText(messages.getString("connectionOptions.button.change.tooltip"));
		delete.setToolTipText(messages.getString("connectionOptions.button.delete.tooltip"));
		ok.setToolTipText(messages.getString("connectionOptions.button.ok.tooltip"));
		cancel.setToolTipText(messages.getString("connectionOptions.button.cancel.tooltip"));
		help.setToolTipText(messages.getString("connectionOptions.button.help.tooltip"));
		setVisible(true);
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
    	//System.out.print(node.toString());
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
	    	//System.out.print(port);
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

	@Override
	public void actionPerformed(ActionEvent ae) {
		System.out.println(ae.getActionCommand());
		if (ae.getActionCommand().equals(messages.getString("connectionOptions.button.add.buttonText"))) {
			
		} else if(ae.getActionCommand().equals(messages.getString("connectionOptions.button.change.buttonText"))) {
			 DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent(); 
			 System.out.println(node.toString());
			 
			 OptionsServerPrefs my = null;
			 for(OptionsServerPrefs opfs : osp){
				 if(node.toString().equals(opfs.getServerName())) {
					 my = opfs;
					 
				 }
			 }
			 if(my != null)
				 my.setServerName("hei!");
		} else if(ae.getActionCommand().equals(messages.getString("connectionOptions.button.delete.buttonText"))) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent(); 
			System.out.println(node.toString());
			for (int i = osp.size() - 1; i >= 0; --i) {
				if (osp.get(i).getServerName().equals(node.toString())) {
					osp.remove(i);
					DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
					model.removeNodeFromParent(node);
				}
			}
			
		}
	}	
}
