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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class OptionsPersonal extends JPanel implements ActionListener {
	//private static ResourceBundle messages;
	private JTextField fullName, email, nickname, altnick;
	JButton connect, ok, cancel, help;
	private JCheckBox invisible;
	private GridBagLayout layout = new GridBagLayout();
	private GridBagConstraints gbc = new GridBagConstraints();

	private BorderLayout bl;
	private JLabel selectedServer;
	
	File 	filePrefs,  	// Prefs file for ConnectionOptions preferences 
	fileServers; 	// List of known servers

	/**
	 * Constructor for the class, handle all GUI layout and fill inn initial
	 * values
	 * 
	 */
	


	public OptionsPersonal(String selectedServer) {
		filePrefs = new File("connetionoptionsprefs.ini");
		fileServers = new File("servers.ini");

		bl = new BorderLayout();
		setLayout(layout);
		JPanel networksPanel = new JPanel();
		networksPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		networksPanel.add(new JLabel(sIRC.i18n.getStr("connectionOptions.label.networks")));

		if(!(selectedServer == null)){
			gbc.anchor = GridBagConstraints.NORTH;
			add(1,8, 1, 1, this.selectedServer = new JLabel(""));
			//this.selectedServer = selectedServer;
		}
		gbc.anchor = GridBagConstraints.CENTER;
		// Create the panel with the four buttons on the right
		
		connect = createButton("connect", "", "", "connectionOptions.button.connect.buttonText");
		add(2,8, connect);
		// Add labels to text fields
		gbc.anchor = GridBagConstraints.EAST;
		add(1, 2, new JLabel(sIRC.i18n.getStr("connectionOptions.label.fullName")));
		add(1, 3,new JLabel(sIRC.i18n.getStr("connectionOptions.label.email")));
		add(1,4,new JLabel(sIRC.i18n.getStr("connectionOptions.label.nickname")));
		add(1,5,new JLabel(sIRC.i18n.getStr("connectionOptions.label.altNick")));
		// Add textfields
		gbc.anchor = GridBagConstraints.WEST;
		add(2, 2, fullName = new JTextField("", 20));
		add(2, 3, email = new JTextField("", 20));
		add(2, 4, nickname = new JTextField("", 15));
		add(2, 5, altnick = new JTextField("", 15));

		// Add ok, cancel, help buttons
		JPanel okCancelHelpPanel = new JPanel();
		okCancelHelpPanel.setLayout(new GridLayout(1, 3));
		
		ok = createButton("ok", "", "connectionOptions.button.ok.tooltip", "button.ok.buttonText");
		okCancelHelpPanel.add(ok);
		
		cancel = createButton("cancel", "", "connectionOptions.button.cancel.tooltip", "button.cancel.buttonText");
		okCancelHelpPanel.add(cancel);
		gbc.anchor = GridBagConstraints.SOUTH;
		gbc.insets = new Insets (10,10,10,10);
		add(1, 9, 3, 1, okCancelHelpPanel);

		// Set all tooltip texts
		fullName.setToolTipText(sIRC.i18n.getStr("connectionOptions.textfield.fullName.tooltip"));
		email.setToolTipText(sIRC.i18n.getStr("connectionOptions.textfield.email.tooltip"));
		nickname.setToolTipText(sIRC.i18n.getStr("connectionOptions.textfield.nickname.tooltip"));
		altnick.setToolTipText(sIRC.i18n.getStr("connectionOptions.textfield.altNick.tooltip"));
		this.load();
		setVisible(true);
	}

	/**
	 * Used to place components/panels in a single cell, take the x, y and
	 * component to be placed as a parameter
	 * 
	 * @param x
	 *            the column for this component
	 * @param y
	 *            the row for this component
	 * @param c
	 *            the component to add to the layout
	 */
	private void add(int x, int y, Component c) {
		add(x, y, 1, 1, c);
	}

	/**
	 * Used to add a panel/component to the layout, just take the x, y, width
	 * and height as well as the component to be placed as a parameter
	 * 
	 * @param x
	 *            the column for this component
	 * @param y
	 *            the row for this component
	 * @param width
	 *            the number of columns this component will spawn
	 * @param height
	 *            the number of rows this component will spawn
	 * @param c
	 *            the component to add to the layout
	 */
	private void add(int x, int y, int width, int height, Component c) {
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		layout.setConstraints(c, gbc);
		add(c);
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


	@Override
	public void actionPerformed(ActionEvent ae) {
		System.out.println(ae.getActionCommand());
		if(ae.getActionCommand().equals("connect")) {
			sIRC.conManagement.connect(getNickname(), this.selectedServer.getText());
			this.save();
			sIRC.options.hideWindow();
			
		} else if(ae.getActionCommand().equals("ok")) {
			this.save();
			sIRC.options.hideWindow();
		} else if(ae.getActionCommand().equals("cancel")) {
			sIRC.options.hideWindow();
			this.load();
		}
		
		
	}
	public void save() {
		// Try to update values
		try {			
			FileOutputStream fos = new FileOutputStream(filePrefs);
			Properties pri = new Properties();
			
			pri.store(fos, "");
			pri.setProperty("fullname", fullName.getText());
			pri.setProperty("email", email.getText());
			pri.setProperty("nickname", nickname.getText());
			pri.setProperty("altnick", altnick.getText());
			pri.setProperty("server", selectedServer.getText());
			pri.store(fos, "");
			
			fos.close();
			
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}
	public void load() {
		try {
			FileInputStream fis = new FileInputStream(filePrefs);
			Properties pro = new Properties();
			
			pro.load(fis);
			// Try to display them
			fullName.setText(pro.getProperty("fullname"));
			email.setText(pro.getProperty("email"));
			nickname.setText(pro.getProperty("nickname"));
			altnick.setText(pro.getProperty("altnick"));
			selectedServer.setText(pro.getProperty("server"));
			
			fis.close();
			
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	/**
	 * @return the selectedServer
	 */
	public String getSelectedServer() {
		return selectedServer.getText();
	}

	/**
	 * @param selectedServer the selectedServer to set
	 */
	public void setSelectedServer(String selectedServer) {
		this.selectedServer.setText(selectedServer);
	}
	private JButton createButton(String cmd, String icon, String tooltip, String name) {
		JButton button = new JButton(sIRC.i18n.getStr(name));
		button.setActionCommand(cmd);
		button.addActionListener(this);

		if (icon.length() > 1) {  // Set icon if we got one
			button.setIcon(new ImageIcon(getClass().getResource(
					"Images/Icons/" + icon)));
		}
		if (tooltip.length() > 2) { // Set tooltip if the string tooltip
									// contains something useful
			button.setToolTipText(sIRC.i18n.getStr(tooltip));
		}
		
		return button;
	}

}