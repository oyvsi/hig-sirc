package no.hig.sss.sirc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JButton;
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
 * This class makes the JPanel for choosing the different servers and handelig file read 
 * and load from the servers.ini.
 * 
 * @author Oyvind Sigerstad, Nils Slaaen, Bjorn-Erik Strand
 * 
 */
@SuppressWarnings("serial")
public class OptionsServer extends JPanel implements TreeSelectionListener, ActionListener {

	private JButton add, change, delete, ok, cancel, help, addServer, back;
	private ArrayList<OptionsServerPrefs> osp;
	private String[] networks;
	private BorderLayout bl;
	private JScrollPane sp;
	private JPanel action, server;
	private String selectedServer;
	private JTree tree;
	private DefaultTreeModel treeModel;
	private File fileServers;
	private JTextField editServerName, editServerUrl, editServerGroup, editServerPorts; 
	
	// To remember nodes while editing an existing server
	private DefaultMutableTreeNode nToRemove = null;
	private OptionsServerPrefs ospToRemove = null;
	
	/**
	 * Class constructor
	 * Makes the initial layout for serveroptions, including buttons and tree as a JPanel with borderlayout
	 */
	public OptionsServer() {
		bl = new BorderLayout();
		this.loadServers();	// Load servers
		setLayout(bl);	// Sets layout

		// Initial treenode for the servers tree.
		DefaultMutableTreeNode tRoot = new DefaultMutableTreeNode("root");
		
		// Makes new model based around root node
		treeModel = new DefaultTreeModel(tRoot);
		// Makes new tree based on the treemodel
		tree = new JTree(treeModel);

		// Adds all the servers with parent tRoot
		addServers(tRoot);
		tree.expandPath(tree.getPathForRow(0)); // expands first level under root
		tree.setRootVisible(false); // Hides root node

		// sets listener so we can select servers
		tree.addTreeSelectionListener(this);

		// makes tree scrollable
		sp = new JScrollPane(tree);
		sp.setPreferredSize(new Dimension(150, 200));
		add(new JLabel("Server settings"), BorderLayout.NORTH);

		add(sp, BorderLayout.CENTER);
		// Create the panel with the three buttons on the right
		action = new JPanel();
		action.setLayout(new GridLayout(3, 1));
		
		add = Helpers.createButton("button.add.buttonText", "button.add.tooltip", "add", this);
		change = Helpers.createButton("connectionOptions.button.change.buttonText", "connectionOptions.button.change.tooltip", "change", this);
		delete = Helpers.createButton("connectionOptions.button.delete.buttonText", "connectionOptions.button.delete.tooltip", "delete", this);

		// adds buttons to the action panel
		action.add(add);
		action.add(change);
		action.add(delete);
		
		// Add the panel
		action.setSize(new Dimension(200, 60));
		add(action, BorderLayout.EAST);
		
		// Add ok, cancel, help buttons
		JPanel navigate = new JPanel();
		navigate.setLayout(new GridLayout(1, 3));
		
		ok = Helpers.createButton("button.ok.buttonText", "connectionOptions.button.ok.tooltip", "ok", this);
		cancel = Helpers.createButton("button.cancel.buttonText", "connectionOptions.button.cancel.tooltip", "cancel", this);
		help = Helpers.createButton("button.help.buttonText", "connectionOptions.button.help.tooltip", "help", this);
		
		// Add the buttons
		navigate.add(ok);
		navigate.add(cancel);
		navigate.add(help);
		add(navigate, BorderLayout.SOUTH);
		
		setVisible(true);
	}
	
	/**
	 * Listener for value changed in tree sets the selected server.
	 * 
	 * @param tse
	 */
	@Override
	public void valueChanged(TreeSelectionEvent tse) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();
		if ((node == null) || !(node.isLeaf()))
			return;
		// If node is leaf and node is not null, set selected server
		setSelectedServer(node.toString(), node.getParent().toString());
	}	

	/**
	 * Handles action events for class
	 * Events: add, addserver, delete, change, back, ok, cancel, help
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getActionCommand().equals("add")) {
			nToRemove = null;
			ospToRemove = null;
			addServer(null, null);
		} 
		
		else if (ae.getActionCommand().equals("addServer")) {
			saveServer();
			sIRC.options.os = new OptionsServer();
			sIRC.options.setViewServer();
		} 
		
		else if (ae.getActionCommand().equals("delete")) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			for (int i = osp.size() - 1; i >= 0; --i) {
				if (osp.get(i).getServerName().equals(node.toString())) {			
					if(node.getParent() != null) {
						DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
						model.removeNodeFromParent(node);
						osp.remove(i);
					}
				}
			}
		} 
		
		else if (ae.getActionCommand().equals("change")) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
					.getLastSelectedPathComponent();
			for (int i = osp.size() - 1; i >= 0; --i) {
				if (osp.get(i).getServerName().equals(node.toString())) {
					OptionsServerPrefs osp = this.osp.get(i);
					addServer(osp, node);
					break;		// Finds our node, gtfo
				}
			}
		} 
		
		else if (ae.getActionCommand().equals("ok")) {
			sIRC.options.hideWindow(true); // Hides window and save prefs
			sIRC.options.os.saveServers();
		} 
		
		else if (ae.getActionCommand().equals("cancel")) {
			sIRC.options.hideWindow(false); // Hides window, don't save prefs
		} 
		
		else if (ae.getActionCommand().equals("back")) {
			sIRC.options.os = new OptionsServer();
			sIRC.options.setViewServer();
		} 
		
		else if (ae.getActionCommand().equals("help")) {
			sIRC.options.setViewHelp(Options.SERVERHELP);
		}
	}
	
	/**
	 * Saves server in the list of servers to file specified.
	 */
	public void saveServers() {
		fileServers = Helpers.getFile("servers.ini");
		FileWriter fos;
		try {
			fos = new FileWriter(fileServers);
			BufferedWriter bw = new BufferedWriter(fos);

			bw.write("[networks]");
			bw.newLine();
			int i = 0;
			for (String network : networks) {
				i++;
				bw.write("n" + i + "=" + network);
				bw.newLine();
			}
			bw.newLine();
			bw.write("[servers]");
			bw.newLine();
			i = 0;		// Let us just use this while we still can
			for (OptionsServerPrefs osp : this.osp) {
				int [] ports = osp.getPort();
				String tempPorts = "";
				
				i++;
				for(int port : ports) {
					tempPorts = tempPorts + port + ",";
				}
				tempPorts = tempPorts.substring(0, tempPorts.length()-1);
				
				bw.write("n" + i + "=" + osp.getServerName() + "SERVER:"
						+ osp.getServerUrl() + ":" + tempPorts + 
						"GROUP:" + osp.getServerGroup());
				bw.newLine();
			}
			bw.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the selectedServer
	 */
	public String getSelectedServer() {
		return selectedServer;
	}

	/**
	 * @param selectedServer
	 *            the selectedServer to set
	 */
	public void setSelectedServer(String selectedServer, String group) {
		for(OptionsServerPrefs osp : this.osp) {
			if(osp.getServerName().equals(selectedServer) && osp.getServerGroup().equals(group)) {
				this.selectedServer = osp.getServerUrl();
			}
		}
	}

	/**
	 * Loads servers from file specified.
	 */
	public void loadServers() {
		fileServers = Helpers.getFile("servers.ini");

		ArrayList<String> tempServers = new ArrayList<String>();
		ArrayList<String> tempNetworks = new ArrayList<String>();

		String line;
		String[] split;
		try {
			FileInputStream fis;
			fis = new FileInputStream(fileServers);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
			while ((line = br.readLine()) != null) {
				if (line.contains("[networks]"))	// Must read line first.
					break;							// OH The infamous break.
			}

			// Start to read networks
			while ((line = br.readLine()) != null) {
				if (line.contains("[servers]"))		// Must read line first.
				break;								// OH The infamous break.
				if (line.contains("=")) {
					split = line.split("=");
					tempNetworks.add(split[1]);
				}
			}
			
			// Start to read servers
			while (((line = br.readLine()) != null)) {
				tempServers.add(line);
			}
			// End of file read, lets close the file
			fis.close();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}

		// Put our servers into the String array of networks
		networks = new String[tempNetworks.size()];
		networks = tempNetworks.toArray(networks);

		// but our servers into a temp array of Strings
		String[] serv = new String[tempServers.size()];
		serv = tempServers.toArray(serv);

		String[] tempS;
		
		// Make list of server objects.
		osp = new ArrayList<OptionsServerPrefs>();
		OptionsServerPrefs tempOsp;

		int[] port;

		for (String server : serv) {
			if (server.length() > 8) {
				if (server.contains((":"))) {
					tempS = server.split(":");
					port = toPort(tempS[2]);
					String edit = tempS[0].split("=")[1];
					if(edit.contains("SERVER")) {
						edit = edit.substring(0, (edit.length()-6)); // remove SERVER
					}
					tempOsp = new OptionsServerPrefs(edit, tempS[1], tempS[3], port);
					osp.add(tempOsp);
					tempOsp = osp.get(0);
				}
			}
		}
	}

	/**
	 * Makes a new view to edit or add a new server, adds listeners.
	 * @param osp
	 * @param n
	 */
	private void addServer(OptionsServerPrefs osp, DefaultMutableTreeNode n) {
		// Remove items from JPanel
		remove(sp);
		remove(action);
		if(server != null) {
			remove(server);
		}
		
		// Create layout for add/edit server
		server = new JPanel();
		server.setLayout(new GridLayout(5, 2));
		server.setSize(new Dimension(100, 100));
		server.add(new JLabel(sIRC.i18n.getStr("server.name")));
		server.add(editServerName = new JTextField(osp != null ? osp
				.getServerName() : ""));
		server.add(new JLabel(sIRC.i18n.getStr("server.url")));
		server.add(editServerUrl = new JTextField(osp != null ? osp
				.getServerUrl() : ""));
		server.add(new JLabel(sIRC.i18n.getStr("server.port")));
		server.add(editServerPorts = new JTextField(osp != null ? 
				portsToString(osp.getPort()) : ""));
		server.add(new JLabel(sIRC.i18n.getStr("server.group")));
		server.add(editServerGroup = new JTextField(osp != null ? osp
				.getServerGroup() : ""));
		
		addServer = Helpers.createButton("button.add.buttonText", "button.add.tooltip", "addServer", this);
		back = Helpers.createButton("button.back.buttonText", "button.back.tooltip", "back", this);
		
		server.add(addServer);
		server.add(back);

		// Set values to se if we are adding or removing
		nToRemove = n;
		ospToRemove = osp;
		
		add(server, BorderLayout.CENTER);
		revalidate();
		repaint();
	}

	/**
	 * Add server to list and tree, based on if server is being edited or a new one is made
	 */
	private void saveServer() {
		OptionsServerPrefs osp = new OptionsServerPrefs(
				editServerName.getText(), editServerUrl.getText(),
				editServerGroup.getText(), toPort(editServerPorts.getText()));
		this.osp.add(osp);
		
		if((nToRemove != null) && (ospToRemove != null)) {
			// Delete server we just edited
			treeModel.removeNodeFromParent(nToRemove);
			this.osp.remove(ospToRemove);
			// Make sure we remove references to "deleted objects"
			nToRemove = null;
			ospToRemove = null;
		}
		this.saveServers();	// save servers
	}
	
	/**
	 * Takes an Int array and makes comma separated string
	 * 
	 * @param ports
	 * @return ports as String
	 */
	private String portsToString(int[] ports) {
		String tempPorts = "";
		for(int port : ports) {
			tempPorts = tempPorts + port + ",";
		}
		// returns the string, and removes the comma at the end.
		return(tempPorts.substring(0, tempPorts.length()-1));
	}
	
	/**
	 * Takes string that looks like 6661-6669GROUP, or 6665-6668,7000GROUP
	 * Turns them into an array of Ints
	 * @param raw
	 * @return array of Ints
	 */
	private int[] toPort(String raw) {
		ArrayList<Integer> al = new ArrayList<Integer>();
		String[] temp;
		String[] temp2;
		temp = raw.split("G");

		temp = temp[0].split(",");
		for (String temp3 : temp) {
			if (temp3.contains("-")) {
				temp2 = temp3.split("-");
				for (int i = Integer.valueOf(temp2[0]); i <= Integer
						.valueOf(temp2[1]); i++) {
					al.add(i);
				}
			} else {
				al.add(Integer.valueOf(temp3));
			}
		}
		int[] ports = new int[al.size()];
		for (int i = 0; i < al.size(); i++) {
			ports[i] = al.get(i).intValue();
		}
		return ports;
	}

	/**
	 * Adds a server to list of servers and the root Tree node
	 * @param t
	 */
	private void addServers(DefaultMutableTreeNode t) {
		Collections.reverse(osp);		// Safe?, reverse list
		for(OptionsServerPrefs temposp : osp){
			boolean isNetwork = false;
			for(String network : this.networks) {
				if((network.equalsIgnoreCase(temposp.getServerGroup()))) {
					isNetwork = true;
				}
			}
			
			if(!isNetwork) {
				DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode(temposp.getServerGroup());
				treeModel.insertNodeInto(dmtn, t, 0);
				dmtn.add(new DefaultMutableTreeNode(temposp.getServerName()));
			}
		}
		
		for (String network : this.networks) {
			if (!network.isEmpty()) {
				DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode(network);
				treeModel.insertNodeInto(dmtn, t, 0);
				for (int i = 0; i < osp.size(); i++) {
					if (osp.get(i).getServerGroup().equals(network)) {
						dmtn.add(new DefaultMutableTreeNode(osp.get(i).getServerName()));
					}
				}
			}
		}
	}
}
