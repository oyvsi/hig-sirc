package no.hig.sss.sirc;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.DefaultListModel;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import jerklib.ConnectionManager;


public class sIRC extends JFrame {
	private static ResourceBundle messages;
	private static Locale currentLocale;
	public static ConnectionManagement conManagement;
	private JPanel outerPanel;
	JDesktopPane jdp;
	
	public sIRC() {
		//conManagement = new ConnectionManagement();
		//jdp = new JDesktopPane();
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Console", createTab("Console"));
        tabbedPane.addTab("golv", createTab("golv"));
        tabbedPane.addTab("nilssl", createTab("nilssl"));
        System.out.println(tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()));
        
        //JPanel tmp = (JPanel) tabbedPane.getTabComponentAt(1);
        //System.out.println(tmp);
        
        
        add(tabbedPane);
		
		
		GUI gui = new GUI(messages);
		
		
		
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                  screenSize.width  - inset*2,
                  screenSize.height - inset*2);
		//createOptions();
		//setContentPane(jdp);
		setJMenuBar(gui.menu());
		
		//jdp.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		
	}
	
	
	public JPanel createTab(String nick) {
		JPanel panel = new JPanel(new BorderLayout());
		
		DefaultListModel listModel = new DefaultListModel();
		listModel.addElement("Oyvsi");
		JList users = new JList(listModel);
		
		
		JTextArea ja = new JTextArea(50,50);
		JScrollPane userArea = new JScrollPane(users);
		userArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		JScrollPane textArea = new JScrollPane(ja);
		textArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
        JTextField jtf = new JTextField();
        panel.add(textArea, BorderLayout.NORTH);
        panel.add(userArea, BorderLayout.WEST);
        panel.add(jtf, BorderLayout.SOUTH);
        return panel;
		
		
	}
	
	public void createOptions() {
		JInternalFrame frame = new JInternalFrame("Channel",true,true,true,true);

		
		frame.setSize(300,300);
		 
        //Set the window's location.
        frame.setLocation(0, 0);
        frame.setVisible(true); //necessary as of 1.3
        jdp.add(frame);
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		currentLocale = Locale.getDefault();
		messages = ResourceBundle.getBundle("i18n/I18N", currentLocale);
		
		sIRC irc = new sIRC();
		//irc.pack();
		irc.setVisible(true);
		irc.setDefaultCloseOperation(EXIT_ON_CLOSE);

	}
}
