package no.hig.sss.sirc;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * This class handles the dialog for getting the connection options for an IRC session.
 * 
 * @author oeivindk
 *
 */
@SuppressWarnings("serial")
public class ConnectionOptions extends JPanel {
	private static ResourceBundle messages;
	
	private JTextField fullName, email, nickname, altnick;
	JButton add, change, delete, sort, connect, ok, cancel, help;
	private JComboBox<String> networks, servers;
	private JCheckBox invisible;
	private GridBagLayout layout = new GridBagLayout();
	private GridBagConstraints gbc = new GridBagConstraints();
	private String dummyNetworks[] = { "Velg et nettverk", "Nettverk 1" ,"Nettverk 2" };
	private String dummyServers[] = { "Random EU Undernet server", "Server med virkelig langt navn" };
	private ConnectionOptionsPrefs cop;
	/**
	 * Constructor for the class, handle all GUI layout and fill inn initial values
	 * 
	 */
	
	public ConnectionOptions () {
		
		cop = new ConnectionOptionsPrefs();
		
		setLayout (layout);
		JPanel networksPanel = new JPanel ();
		networksPanel.setLayout (new FlowLayout(FlowLayout.LEFT, 0, 0));
		networksPanel.add (new JLabel (messages.getString ("connectionOptions.label.networks")));
		networksPanel.add (networks = new JComboBox<String>(dummyNetworks));
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(2, 2, 2, 2);
		// Add the choose network panel
		add (1, 1, 2, 1, networksPanel);
		// Add the choose server drop down box
		add (1, 2, 2, 1, servers = new JComboBox<String>(dummyServers));
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
		add (2, 3, connect = new JButton (messages.getString("connectionOptions.button.connect.buttonText")));
		
		// Add labels to text fields
		gbc.anchor = GridBagConstraints.EAST;
		add (1, 4, new JLabel(messages.getString("connectionOptions.label.fullName")));
		add (1, 5, new JLabel(messages.getString("connectionOptions.label.email")));
		add (1, 6, new JLabel(messages.getString("connectionOptions.label.nickname")));
		add (1, 7, new JLabel(messages.getString("connectionOptions.label.altNick")));
		// Add textfields
		gbc.anchor = GridBagConstraints.WEST;
		add (2, 4, fullName = new JTextField (cop.getFullName(),20));
		add (2, 5, email= new JTextField (cop.getEmail(), 20));
		add (2, 6, nickname = new JTextField (cop.getNickname(), 15));
		add (2, 7, altnick = new JTextField (cop.getAltnick(), 15));
		
		
		// Add invisible checkbox
		add (2,8, invisible = new JCheckBox (messages.getString("connectionOptions.checkbox.invisible.label")));
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
		fullName.setToolTipText(messages.getString("connectionOptions.textfield.fullName.tooltip"));
		email.setToolTipText(messages.getString("connectionOptions.textfield.email.tooltip"));
		nickname.setToolTipText(messages.getString("connectionOptions.textfield.nickname.tooltip"));
		altnick.setToolTipText(messages.getString("connectionOptions.textfield.altNick.tooltip"));
		invisible.setToolTipText(messages.getString("connectionOptions.checkbox.invisible.tooltip"));
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
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName.getText();
	}



	/**
	 * @return the email
	 */
	public String getEmail() {
		return email.getText();
	}



	/**
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname.getText();
	}



	/**
	 * @return the altnick
	 */
	public String getAltnick() {
		return altnick.getText();
	}



	/**
	 * @param messages the messages to set
	 */
	public static void setMessages(ResourceBundle messages) {
		ConnectionOptions.messages = messages;
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
}
