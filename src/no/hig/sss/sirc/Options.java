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

import java.util.*;

//SplitPaneDemo itself is not a visible component.
public class Options extends JPanel implements TreeSelectionListener {
	private JSplitPane splitPane;
	OptionsPersonal op;
	OptionsServer os;
	JFrame jf;
	JTree tree;
	DefaultTreeModel treeModel;
	
	public static TextOptions channelFormat;
	public static TextOptions pmFormat;
	public static TextOptions infoFormat;
	public static TextOptions consoleFormat;
	
	
	final int WHEIGHT = 300;
	final int WWIDTH = 700;
	
	public Options() {
				
		DefaultMutableTreeNode tRoot = new DefaultMutableTreeNode("root");
		DefaultMutableTreeNode tConn = new DefaultMutableTreeNode("Connection");
	    DefaultMutableTreeNode tStyle = new DefaultMutableTreeNode("Style");
	    DefaultMutableTreeNode tPersonal = new DefaultMutableTreeNode("Personal");
	    DefaultMutableTreeNode tServer = new DefaultMutableTreeNode("Server");
	    DefaultMutableTreeNode tCmFormat = new DefaultMutableTreeNode("Channel messages");
	    DefaultMutableTreeNode tPmFormat = new DefaultMutableTreeNode("Private messages");
	    DefaultMutableTreeNode tInfoFormat = new DefaultMutableTreeNode("Info messages");
	    DefaultMutableTreeNode tConFormat = new DefaultMutableTreeNode("Console messages");

	    treeModel = new DefaultTreeModel(tRoot);    
	    tree = new JTree(treeModel);
	    
	    treeModel.insertNodeInto(tStyle, tRoot, 0);
	    treeModel.insertNodeInto(tConn, tRoot, 0);
	    tree.expandPath(tree.getPathForRow(0)); // expands first level
	    tree.setRootVisible(false); // Hides root node
	    
	    tConn.add(tPersonal);
	    tConn.add(tServer);
	    tStyle.add(tCmFormat);
	    tStyle.add(tPmFormat);
	    tStyle.add(tInfoFormat);
	    tStyle.add(tConFormat);
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
		if(os != null){
			if(os.getSelectedServer() != null) { // Don't want new server if its nothing
				op.setSelectedServer(os.getSelectedServer());	// Set server
			}
		} else {
			op = new OptionsPersonal("irc.quakenet.org");	// Std server
		}
		splitPane.setRightComponent(op);
	}
	
	void setViewServer() {
		os.loadServers();
		splitPane.setRightComponent(os);
	}
	void setViewServerEdit() {
		JPanel jp = new JPanel(); 
		BorderLayout bl = new BorderLayout();
		jp.setLayout(bl);
		
		jp.setLayout(new GridLayout(1,2));
		jp.add(new JLabel("Edit Server"));
		jp.add(new JLabel("Name"));
		jp.add(new JButton("test"));
		//jp.add(new JButton("Save"), );
		jp.add(new JButton("Cancel"));
		os.remove(os.tree);
		os.add(jp, BorderLayout.CENTER);
		
	}
	
	private void setFormatView(int view) {
		if(view == TabComponent.CHANNEL)
			splitPane.setRightComponent(channelFormat);
		else if(view == TabComponent.PM)
			splitPane.setRightComponent(pmFormat);
		else if(view == TabComponent.CONSOLE)
			splitPane.setRightComponent(consoleFormat);
		else if(view == TabComponent.INFO)
			splitPane.setRightComponent(infoFormat);			
	}
	
	public JSplitPane getSplitPane() {
		return splitPane;
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	public void createAndShowGUI() {
		// Create and set up the window.
		jf = new JFrame("sIrc Options");
		jf.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		//sIRC.options  = new Options();
		
		jf.getContentPane().add(sIRC.options.getSplitPane());
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int X = (screen.width / 2) - (WWIDTH / 2); // Center horizontally.
		int Y = (screen.height / 2) - (WHEIGHT / 2); // Center vertically.

		jf.setBounds(X,Y , WWIDTH,WHEIGHT);
		// Display the window.
		jf.pack();
		jf.setResizable(false);
		jf.setVisible(true);
		jf.setAlwaysOnTop(true);
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                tree.getLastSelectedPathComponent();
		if (node == null) return;
        if (node.isLeaf()) {
    		switch (node.toString()) {
    		case "Personal":
    			setViewPersonal(null);
    			break;
    			
    		case "Server":
    			setViewServer();
    			break;

    		case "Channel messages":
    			setFormatView(TabComponent.CHANNEL);
    			break;
    		
        	case "Private messages":
        		setFormatView(TabComponent.PM);
        		break;
        	case "Info messages":
        		setFormatView(TabComponent.INFO);
        		break;
        	case "Console messages":
        		setFormatView(TabComponent.CONSOLE);
    		}
    		System.out.print(node.toString());
        }
	}
	public void hideWindow(Boolean saveOptions) {
		jf.setVisible(false);
		if(saveOptions)
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
		Properties pro = new Properties();
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
}