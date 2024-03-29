package no.hig.sss.sirc;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * This class makes the JPanel for personal options
 * 
 * @author Oyvind Sigerstad, Nils Slaaen, Bjorn-Erik Strand
 * 
 */
public class OptionsPersonal extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JTextField username, nickname, altnick;
	private JButton connect, ok, cancel, help;
	private JPanel gridLayout = new JPanel();
	private GridBagLayout layout = new GridBagLayout();
	private GridBagConstraints gbc = new GridBagConstraints();

	private BorderLayout bl;
	private JTextField selectedServer;
	
	File filePrefs;  	// Prefs file for ConnectionOptions preferences 

	/**
	 * Constructor for the class, handle all GUI layout and fill inn initial
	 * values
	 * 
	 */
	public OptionsPersonal(String selectedServer) {
		bl = new BorderLayout();
		setLayout(bl);
		
		gridLayout.setLayout(layout);
		JPanel networksPanel = new JPanel();
		networksPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		networksPanel.add(new JLabel(sIRC.i18n.getStr("connectionOptions.label.networks")));
		gbc.anchor = GridBagConstraints.CENTER;
		// Create the panel with the four buttons on the right
		
		connect = Helpers.createButton("connectionOptions.button.connect.buttonText", "", "connect", this);
		add(1,6,2,1, connect);
		// Add labels to text fields
		
		gbc.anchor = GridBagConstraints.WEST;
		add(1,1, 2, 1, new JLabel("Server"));
		add(1, 2, new JLabel(sIRC.i18n.getStr("connectionOptions.label.username")));
		add(1,4,new JLabel(sIRC.i18n.getStr("connectionOptions.label.nickname")));
		add(1,5,new JLabel(sIRC.i18n.getStr("connectionOptions.label.altNick")));
		
		// Add textfields
		gbc.anchor = GridBagConstraints.WEST;
		if(!(selectedServer == null)){
			add(2,1, 2, 1, this.selectedServer = new JTextField("", 20));
		}
		add(2, 2,2,1, username = new JTextField("", 20));
		add(2, 4,2,1, nickname = new JTextField("", 15));
		add(2, 5,2,1, altnick = new JTextField("", 15));

		// Add ok, cancel, help buttons
		JPanel navigate = new JPanel();
		navigate.setLayout(new GridLayout(1, 3));
		
		ok = Helpers.createButton("button.ok.buttonText", "connectionOptions.button.ok.tooltip", "ok", this);
		cancel = Helpers.createButton("button.cancel.buttonText", "connectionOptions.button.cancel.tooltip","cancel", this);
		help = Helpers.createButton("button.help.buttonText", "connectionOptions.button.help.tooltip", "help", this);
		
		navigate.add(ok);
		navigate.add(cancel);
		navigate.add(help);
		
		gbc.anchor = GridBagConstraints.SOUTH;
		gbc.insets = new Insets (10,10,10,10);

		// Set all tooltip texts
		username.setToolTipText(sIRC.i18n.getStr("connectionOptions.textfield.username.tooltip"));
		nickname.setToolTipText(sIRC.i18n.getStr("connectionOptions.textfield.nickname.tooltip"));
		altnick.setToolTipText(sIRC.i18n.getStr("connectionOptions.textfield.altNick.tooltip"));
		
		add(new JLabel("Personal"), BorderLayout.NORTH);
		add(gridLayout, BorderLayout.CENTER);
		add(navigate, BorderLayout.SOUTH);
		setVisible(true);
	}

	/**
	 * Setter for nickname
	 * @param nick The nickname to change to
	 */
	public void setNick(String nick) {
		nickname.setText(nick);
	}

	/**
	 * Getter for userName
	 * @return the username
	 */
	public String getUserName() {
		return username.getText();
	}

	/**
	 * Getter for nickname
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname.getText();
	}

	/**
	 * Getter for altnick
	 * @return the altnick
	 */
	public String getAltnick() {
		return altnick.getText();
	}

	/**
	 * Handler for action commands
	 * Handles Connect, ok, cancel, help
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
		if(ae.getActionCommand().equals("connect")) {
			sIRC.conManagement.connect(getNickname(), this.selectedServer.getText());
			sIRC.options.hideWindow(true);	
		} 
		
		else if(ae.getActionCommand().equals("ok")) {
			sIRC.options.hideWindow(true);
		} 
		
		else if(ae.getActionCommand().equals("cancel")) {
			sIRC.options.hideWindow(false);
		} 
		
		else if(ae.getActionCommand().equals("help")) {
			sIRC.options.setViewHelp(Options.CONNECTIONHELP);
		}
	}
	
	/**
	 * Saves info in text areas to properties file
	 * textfields: username, nickname, altnick, selectedserver
	 * @return pro
	 */
	public Properties save() {
		Properties pro = new Properties();
		pro.setProperty("username", username.getText());
		pro.setProperty("nickname", nickname.getText());
		pro.setProperty("altnick", altnick.getText());
		pro.setProperty("server", selectedServer.getText());
		
		// Once I was newb, now I
		return pro;
	}
	
	/**
	 * Loads prefs from config file
	 * @param pro
	 */
	public void load(Properties pro) {		
		username.setText(pro.getProperty("username"));
		nickname.setText(pro.getProperty("nickname"));
		altnick.setText(pro.getProperty("altnick"));
		selectedServer.setText(pro.getProperty("server"));
	}
	
	/**
	 * Getter for selected server
	 * @return the selectedServer
	 */
	public String getSelectedServer() {
		return selectedServer.getText();
	}

	/**
	 * Setter for selected server
	 * @param selectedServer the selectedServer to set
	 */
	public void setSelectedServer(String selectedServer) {
		this.selectedServer.setText(selectedServer);
	}

	/**
	 * Used to place components/panels in a single cell, take the x, y and
	 * component to be placed as a parameter
	 * 
	 * @author Cyberkoll
	 * 
	 * @param x the column for this component
	 * @param y the row for this component
	 * @param c the component to add to the layout
	 */
	private void add(int x, int y, Component c) {
		add(x, y, 1, 1, c);
	}

	/**
	 * Used to add a panel/component to the layout, just take the x, y, width
	 * and height as well as the component to be placed as a parameter
	 * 
	 * @author Cyberkoll
	 * 
	 * @param x the column for this component
	 * @param y the row for this component
	 * @param width the number of columns this component will spawn
	 * @param height the number of rows this component will spawn
	 * @param c the component to add to the layout
	 */
	private void add(int x, int y, int width, int height, Component c) {
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		layout.setConstraints(c, gbc);
		gridLayout.add(c);
	}
}