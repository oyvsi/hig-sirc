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
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class OptionsPersonal extends JPanel implements ActionListener {
	private static ResourceBundle messages;
	private JTextField fullName, email, nickname, altnick;
	JButton connect, ok, cancel, help;
	private JCheckBox invisible;
	private GridBagLayout layout = new GridBagLayout();
	private GridBagConstraints gbc = new GridBagConstraints();
	private ConnectionOptionsPrefs cop;

	private BorderLayout bl;
	private String selectedServer;
	/**
	 * Constructor for the class, handle all GUI layout and fill inn initial
	 * values
	 * 
	 */

	public OptionsPersonal(String selectedServer) {

		cop = new ConnectionOptionsPrefs();

		cop.load();
		bl = new BorderLayout();
		setLayout(layout);
		JPanel networksPanel = new JPanel();
		networksPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		networksPanel.add(new JLabel(messages
				.getString("connectionOptions.label.networks")));

		if(!(selectedServer == null)){
			gbc.anchor = GridBagConstraints.NORTH;
			add(6,6, new JLabel(selectedServer));
			this.selectedServer = selectedServer;
		}
		gbc.anchor = GridBagConstraints.CENTER;
		// Create the panel with the four buttons on the right
		add(2,3,connect = new JButton(messages.getString("connectionOptions.button.connect.buttonText")));
		//add(networksPanel, BorderLayout.SOUTH);
		
		
		connect.setActionCommand("connect");
		connect.addActionListener(this);
		// Add labels to text fields
		gbc.anchor = GridBagConstraints.EAST;
		add(1, 4, new JLabel(messages.getString("connectionOptions.label.fullName")));
		add(1, 5,new JLabel(messages.getString("connectionOptions.label.email")));
		add(1,6,new JLabel(messages.getString("connectionOptions.label.nickname")));
		add(1,7,new JLabel(messages.getString("connectionOptions.label.altNick")));
		// Add textfields
		gbc.anchor = GridBagConstraints.WEST;
		add(2, 4, fullName = new JTextField(cop.getFullName(), 20));
		add(2, 5, email = new JTextField(cop.getEmail(), 20));
		add(2, 6, nickname = new JTextField(cop.getNickname(), 15));
		add(2, 7, altnick = new JTextField(cop.getAltnick(), 15));

		// Add invisible checkbox
		add(2,
				8,
				invisible = new JCheckBox(
						messages.getString("connectionOptions.checkbox.invisible.label")));
		// Add ok, cancel, help buttons
		JPanel okCancelHelpPanel = new JPanel();
		okCancelHelpPanel.setLayout(new GridLayout(1, 3));
		okCancelHelpPanel.add(ok = new JButton(messages
				.getString("button.ok.buttonText")));
		okCancelHelpPanel.add(cancel = new JButton(messages
				.getString("button.cancel.buttonText")));
		okCancelHelpPanel.add(help = new JButton(messages
				.getString("button.help.buttonText")));
		gbc.anchor = GridBagConstraints.CENTER;
		add(1, 9, 3, 1, okCancelHelpPanel);

		// Set all tooltip texts
		fullName.setToolTipText(messages
				.getString("connectionOptions.textfield.fullName.tooltip"));
		email.setToolTipText(messages
				.getString("connectionOptions.textfield.email.tooltip"));
		nickname.setToolTipText(messages
				.getString("connectionOptions.textfield.nickname.tooltip"));
		altnick.setToolTipText(messages
				.getString("connectionOptions.textfield.altNick.tooltip"));
		invisible.setToolTipText(messages
				.getString("connectionOptions.checkbox.invisible.tooltip"));
		ok.setToolTipText(messages
				.getString("connectionOptions.button.ok.tooltip"));
		cancel.setToolTipText(messages
				.getString("connectionOptions.button.cancel.tooltip"));
		help.setToolTipText(messages
				.getString("connectionOptions.button.help.tooltip"));

	
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

	/**
	 * @param messages
	 *            the messages to set
	 */
	public static void setMessages(ResourceBundle messages) {
		OptionsPersonal.messages = messages;
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		System.out.println(ae.getActionCommand());
		if(ae.getActionCommand().equals("connect")) {
			sIRC.conManagement.connect(getFullName(), this.selectedServer);
			cop.save();
			sIRC.options.hideWindow();
		}
		
		
	}

}