package no.hig.sss.sirc;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import java.util.*;

//SplitPaneDemo itself is not a visible component.
public class Options extends JPanel implements TreeSelectionListener {
	private JSplitPane splitPane;
	JPanel op;
	OptionsServer os;
	JFrame jf;
	JTree tree;
	DefaultTreeModel treeModel;
	
	final int WHEIGHT = 300;
	final int WWIDTH = 700;
	
	public Options() {

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
	    

		// Create a split pane with the two scroll panes in it.
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(150);

		tree.setMinimumSize(new Dimension(150, 300));
		splitPane.setLeftComponent(tree);
		
		
		splitPane.setPreferredSize(new Dimension(WWIDTH, WHEIGHT));
		splitPane.setMinimumSize(new Dimension(WWIDTH, WHEIGHT));
		
		setViewPersonal(null);
		
	}
	void setViewPersonal(String selectedServer) {
		OptionsPersonal.setMessages(ResourceBundle.getBundle("i18n/I18N"));
		if(os != null){
			op = new OptionsPersonal(os.getSelectedServer());
		} else {
			op = new OptionsPersonal("irc.quakenet.org");
		}
		splitPane.setRightComponent(op);
	}
	
	void setViewServer() {
		OptionsServer.setMessages(ResourceBundle.getBundle("i18n/I18N"));
		os = new OptionsServer();
		splitPane.setRightComponent(os);
	}
	void setViewServerEdit() {
		JPanel jp = new JPanel(); 
		BorderLayout bl = new BorderLayout();
		
		bl.addLayoutComponent(new JLabel("Edit Server"), BorderLayout.NORTH);
		bl.addLayoutComponent(new JLabel("Name"), BorderLayout.WEST);
		jp.setLayout(bl);
		jp.setVisible(true);
		splitPane.setRightComponent(jp);
		System.out.println("weee");
	}

	void setViewColor() {
		JPanel to = new TextOptions();
		to.setBorder(null);
		to.setPreferredSize(new Dimension(800, 600));
		splitPane.setRightComponent(to);

	}
	void setViewText() {
		JPanel to = new TextOptions();
		to.setBorder(null);
		to.setPreferredSize(new Dimension(800, 600));
		splitPane.setRightComponent(to);
		

	}

	void setViewFontOptions() {

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
		Options sp = new Options();
		jf.getContentPane().add(sp.getSplitPane());
		
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int X = (screen.width / 2) - (WWIDTH / 2); // Center horizontally.
		int Y = (screen.height / 2) - (WHEIGHT / 2); // Center vertically.

		jf.setBounds(X,Y , WWIDTH,WHEIGHT);
		// Display the window.
		jf.pack();
		jf.setVisible(true);
		jf.setAlwaysOnTop(true);
	}
	/*
	public static void main(String[] args) {
		//Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Options op = new Options();
				op.createAndShowGUI();
			}
		});
	}
	*/
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

    		case "Text":
    			setViewText();
    			break;
    		
        	case "Color":
        		setViewColor();
        		break;
    		}
      System.out.print(node.toString());
      }
		
	}
	public void hideWindow() {
		jf.setVisible(false);
		
	}
	public void showWindow() {
		jf.setVisible(true);
	}
}