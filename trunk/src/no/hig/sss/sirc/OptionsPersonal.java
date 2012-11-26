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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class OptionsPersonal extends JPanel implements ActionListener {
	//private static ResourceBundle messages;
	private JTextField fullName, email, nickname, altnick;
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
		filePrefs = new File("connetionoptionsprefs.ini");
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
		add(1, 2, new JLabel(sIRC.i18n.getStr("connectionOptions.label.fullName")));
		add(1, 3,new JLabel(sIRC.i18n.getStr("connectionOptions.label.email")));
		add(1,4,new JLabel(sIRC.i18n.getStr("connectionOptions.label.nickname")));
		add(1,5,new JLabel(sIRC.i18n.getStr("connectionOptions.label.altNick")));
		// Add textfields
		gbc.anchor = GridBagConstraints.WEST;
		if(!(selectedServer == null)){
			add(2,1, 2, 1, this.selectedServer = new JTextField("", 20));
		}
		add(2, 2,2,1, fullName = new JTextField("", 20));
		add(2, 3,2,1, email = new JTextField("", 20));
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
		fullName.setToolTipText(sIRC.i18n.getStr("connectionOptions.textfield.fullName.tooltip"));
		email.setToolTipText(sIRC.i18n.getStr("connectionOptions.textfield.email.tooltip"));
		nickname.setToolTipText(sIRC.i18n.getStr("connectionOptions.textfield.nickname.tooltip"));
		altnick.setToolTipText(sIRC.i18n.getStr("connectionOptions.textfield.altNick.tooltip"));
		
		add(new JLabel("Personal"), BorderLayout.NORTH);
		add(gridLayout, BorderLayout.CENTER);
		add(navigate, BorderLayout.SOUTH);
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
		gridLayout.add(c);
	}
	
	/**
	 * 
	 * @param nick The nickname to change to
	 */
	public void setNick(String nick) {
		nickname.setText(nick);
	
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
			sIRC.options.hideWindow(true);
			
		} else if(ae.getActionCommand().equals("ok")) {
			sIRC.options.hideWindow(true);
		} else if(ae.getActionCommand().equals("cancel")) {
			sIRC.options.hideWindow(false);
		} else if(ae.getActionCommand().equals("help")) {
			sIRC.options.setViewHelp(0);
		}
		
		
	}
	public Properties save() {
		Properties pro = new Properties();
		pro.setProperty("fullname", fullName.getText());
		pro.setProperty("email", email.getText());
		pro.setProperty("nickname", nickname.getText());
		pro.setProperty("altnick", altnick.getText());
		pro.setProperty("server", selectedServer.getText());
		
		return pro;
	}
	
	public void load(Properties pro) {		
		fullName.setText(pro.getProperty("fullname"));
		email.setText(pro.getProperty("email"));
		nickname.setText(pro.getProperty("nickname"));
		altnick.setText(pro.getProperty("altnick"));
		selectedServer.setText(pro.getProperty("server"));
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
}