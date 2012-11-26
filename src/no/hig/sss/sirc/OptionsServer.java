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
 * This class handles the dialog for getting the connection options for an IRC
 * session.
 * 
 * @author oeivindk
 * 
 */
@SuppressWarnings("serial")
public class OptionsServer extends JPanel implements TreeSelectionListener, ActionListener {

	JButton add, change, delete, ok, cancel, help, addServer, back;
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
	
	// for edit and add server

	/**
	 * Constructor for the class, handle all GUI layout and fill inn initial
	 * values
	 * 
	 */

	public OptionsServer() {

		bl = new BorderLayout();
		this.loadServers();
		setLayout(bl);

		DefaultMutableTreeNode tRoot = new DefaultMutableTreeNode("root");

		treeModel = new DefaultTreeModel(tRoot);
		tree = new JTree(treeModel);

		addServers(tRoot);
		tree.expandPath(tree.getPathForRow(0)); // expands path 1
		tree.setRootVisible(false); // Hides root node

		tree.addTreeSelectionListener(this);

		sp = new JScrollPane(tree);
		sp.setPreferredSize(new Dimension(150, 200));
		add(new JLabel("Server settings"), BorderLayout.NORTH);

		add(sp, BorderLayout.CENTER);
		// Create the panel with the four buttons on the right
		action = new JPanel();
		action.setLayout(new GridLayout(3, 1));
		add = Helpers.createButton("button.add.buttonText", "button.add.tooltip", "add", this);
		change = Helpers.createButton("connectionOptions.button.change.buttonText", "connectionOptions.button.change.tooltip", "change", this);
		delete = Helpers.createButton("connectionOptions.button.delete.buttonText", "connectionOptions.button.delete.tooltip", "delete", this);

		action.add(add);
		action.add(change);
		action.add(delete);
		
		// Add the buttons on the right
		action.setSize(new Dimension(200, 60));
		add(action, BorderLayout.EAST);
		// Add ok, cancel, help buttons
		JPanel navigate = new JPanel();
		navigate.setLayout(new GridLayout(1, 2));
		
		ok = Helpers.createButton("button.ok.buttonText", "connectionOptions.button.ok.tooltip", "ok", this);
		cancel = Helpers.createButton("button.cancel.buttonText", "connectionOptions.button.cancel.tooltip", "cancel", this);
		help = Helpers.createButton("button.help.buttonText", "connectionOptions.button.help.tooltip", "help", this);
		
		navigate.add(ok);
		navigate.add(cancel);
		navigate.add(help);
		add(navigate, BorderLayout.SOUTH);
		
		setVisible(true);
	}
	
	/**
	 * Test function for the class.
	 * 
	 * @param tse
	 */
	@Override
	public void valueChanged(TreeSelectionEvent tse) {

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();
		if (node == null)
			return;
		if (node.isLeaf()) {
			setSelectedServer(node.toString(), node.getParent().toString());
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
		}
	}
	private String portsToString(int[] ports) {
		String tempPorts = "";
		for(int port : ports) {
			tempPorts = tempPorts + port + ",";
		}
		return(tempPorts.substring(0, tempPorts.length()-1));
	}
	
	
	/**
	 * Takes string that looks like 6661-6669GROUP, or 6665-6668,7000GROUP
	 * 
	 * @param raw
	 * @return array of ints
	 */
	private int[] toPort(String raw) {
		ArrayList<Integer> al = new ArrayList<Integer>();
		String[] temp;
		String[] temp2;
		//raw = raw.substring(0, (raw.length()-5));
		temp = raw.split("G");

		temp = temp[0].split(",");
		for (String temp3 : temp) {
			//System.out.println(temp3);
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

	private void addServers(DefaultMutableTreeNode t) {
		Collections.reverse(osp);		// Safe?
		//Collections.reverse(networks);	// Can only be used on list
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

	@Override
	public void actionPerformed(ActionEvent ae) {
		System.out.println(ae.getActionCommand());
		if (ae.getActionCommand().equals("add")) {
			nToRemove = null;
			ospToRemove = null;
			addServer(null, null);
		} else if (ae.getActionCommand().equals("addServer")) {
			saveServer();
			sIRC.options.os = new OptionsServer();
			sIRC.options.setViewServer();

		} else if (ae.getActionCommand().equals("delete")) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
					.getLastSelectedPathComponent();
			System.out.println(node.toString());
			for (int i = osp.size() - 1; i >= 0; --i) {
				if (osp.get(i).getServerName().equals(node.toString())) {
					
					
					if(node.getParent() != null) {
						DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
						model.removeNodeFromParent(node);
						osp.remove(i);
					
					}
				}
			}
		} else if (ae.getActionCommand().equals("change")) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
					.getLastSelectedPathComponent();
			for (int i = osp.size() - 1; i >= 0; --i) {
				if (osp.get(i).getServerName().equals(node.toString())) {
					OptionsServerPrefs osp = this.osp.get(i);
					addServer(osp, node);
					break;
					
				}
			}
		} else if (ae.getActionCommand().equals("ok")) {
			sIRC.options.hideWindow(true); // Hides window and save prefs
			sIRC.options.os.saveServers();
		//	sIRC.options.op.save(); // Saves prefs to ini
		} else if (ae.getActionCommand().equals("cancel")) {
			sIRC.options.hideWindow(false); // Hides window, don't save prefs
		//	sIRC.options.op.load(); // Loads prefs again
		} else if (ae.getActionCommand().equals("back")) {
			sIRC.options.os = new OptionsServer();
			sIRC.options.setViewServer();
		} else if (ae.getActionCommand().equals("help")) {
			sIRC.options.setViewHelp(Options.SERVERHELP);
		}
		
		
	}

	public void saveServers() {
		fileServers = new File("servers2.ini");
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
			i = 0;
			for (OptionsServerPrefs osp : this.osp) {
				i++;
				
				int [] ports = osp.getPort();
				String tempPorts = "";
				for(int port : ports) {
					tempPorts = tempPorts + port + ",";
				}
				tempPorts = tempPorts.substring(0, tempPorts.length()-1);
				
				bw.write("n" + i + "=" + osp.getServerName() + "SERVER:"
						+ osp.getServerUrl() + ":" + tempPorts + 
						"GROUP:" + osp.getServerGroup());
				bw.newLine();
				
				//System.out.println("n" + i + "=" + osp.getServerName() + "SERVER:"
				//		+ osp.getServerUrl() + ":" + tempPorts + 
				//		"GROUP:" + osp.getServerGroup() + "\n");
			}
			bw.close();
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void loadServers() {
		fileServers = new File("servers2.ini");

		ArrayList<String> tempServers = new ArrayList<String>();
		ArrayList<String> tempNetworks = new ArrayList<String>();

		String line;
		String[] split;
		try {
			FileInputStream fis;
			fis = new FileInputStream(fileServers);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis,
					Charset.forName("UTF-8")));
			while (((line = br.readLine()) != null)) {
				if (line.contains("[networks]"))
					break;

			}

			while (((line = br.readLine()) != null)) {
				if (line.contains("[servers]"))
					break;
				if (line.contains("=")) {
					split = line.split("=");
					tempNetworks.add(split[1]);
				}
			}
			while (((line = br.readLine()) != null)) {
				tempServers.add(line);

			}
			fis.close();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}

		networks = new String[tempNetworks.size()];
		networks = tempNetworks.toArray(networks);

		String[] serv = new String[tempServers.size()];
		serv = tempServers.toArray(serv);

		String[] tempS;

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

	public void addServer(OptionsServerPrefs osp, DefaultMutableTreeNode n) {
		remove(sp);
		remove(action);
		if(server != null) {
			remove(server);
		}
		
		server = new JPanel();
		server.setLayout(new GridLayout(5, 2));
		server.setSize(new Dimension(100, 100));
		server.add(new JLabel("Server Name"));
		server.add(editServerName = new JTextField(osp != null ? osp
				.getServerName() : ""));
		server.add(new JLabel("Url"));
		server.add(editServerUrl = new JTextField(osp != null ? osp
				.getServerUrl() : ""));
		server.add(new JLabel("Ports"));
		server.add(editServerPorts = new JTextField(osp != null ? 
				portsToString(osp.getPort()) : ""));
		server.add(new JLabel("Group"));
		server.add(editServerGroup = new JTextField(osp != null ? osp
				.getServerGroup() : ""));
		
		addServer = Helpers.createButton("button.add.buttonText", "button.add.tooltip", "addServer", this);
		back = Helpers.createButton("button.back.buttonText", "button.back.tooltip", "back", this);
		
		server.add(addServer);
		server.add(back);

		nToRemove = n;
		ospToRemove = osp;
		
		add(server, BorderLayout.CENTER);
		revalidate();
		repaint();
	}

	public void saveServer() {
		OptionsServerPrefs osp = new OptionsServerPrefs(
				editServerName.getText(), editServerUrl.getText(),
				editServerGroup.getText(), toPort(editServerPorts.getText()));
		this.osp.add(osp);
		
		if((nToRemove != null) && (ospToRemove != null)) {
			treeModel.removeNodeFromParent(nToRemove);
			this.osp.remove(ospToRemove);
			nToRemove = null;
			ospToRemove = null;
		}
		this.saveServers();
	}

	public void changeServer(OptionsServerPrefs osp) {

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
}
