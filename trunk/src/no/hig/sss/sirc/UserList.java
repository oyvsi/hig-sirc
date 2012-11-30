package no.hig.sss.sirc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import jerklib.Channel;
import jerklib.events.modes.ModeAdjustment.Action;

/**
 * UserList is used for the visualization of users strings and
 * holds the popup menu with actions on a given user
 * 
 * @author Oyvind Sigerstad, Nils Slaaen, Bjorn-Erik Strand
 *
 */
public class UserList extends JList<String> implements MouseListener {
	private static final long serialVersionUID = 1L;
	private TabContainer tabContainer = sIRC.tabContainer;
	private String channelName;
	private JPopupMenu popupMenu;
	private ConnectionManagement cm = sIRC.conManagement;
	private UserModel userModel;
	private ActionListener controlListener;
	
	/**
	 * Constructor for UserList, creates popup menu and adds action listeners
	 * @param identifier
	 * @param userContainer
	 */
	public UserList(String identifier, UserModel userModel) {
		super(userModel);
		this.userModel = userModel;
		this.setFixedCellWidth(100);
		channelName = identifier;
		popupMenu = createPopupMenu();
		add(popupMenu);
		addMouseListener(this);
		controlListener = new ControlListener();
	}
	/** 
	 * Sets up the popup menu with submenus, menuitems and adds
	 * listeners to each menuitem
	 * @return tmpPopupMenu - The popup menu
	 */
	private JPopupMenu createPopupMenu() {
		JPopupMenu tmpPopupMenu = new JPopupMenu();
		
		// Main menus
		JMenu control = new JMenu("Control");
		JMenu ctcp = new JMenu("CTCP");
		
		JMenuItem whois = new JMenuItem("Whois");
		
		// Control menu items
		JMenuItem op = new JMenuItem("Op");
		JMenuItem deop = new JMenuItem("Deop");
		JMenuItem voice = new JMenuItem("Voice");
		JMenuItem devoice = new JMenuItem("Devoice");
		JMenuItem kick = new JMenuItem("Kick");
		
		// CTCP menu items
		JMenuItem ping = new JMenuItem("Ping");
		JMenuItem time = new JMenuItem("Time");
		JMenuItem version = new JMenuItem("Version");
		
		// Listeners
		
		kick.addActionListener(new ControlListener());
		op.addActionListener(new ControlListener());
		deop.addActionListener(new ControlListener());
		voice.addActionListener(new ControlListener());
		devoice.addActionListener(new ControlListener());
		
		whois.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cm.getSession().whois(parseNick(getSelectedValue().toString()));
			}
		});
		
		ping.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleCtcp("PING");
			}
		});
		
		time.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleCtcp("TIME");
			}
		});
		
		version.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleCtcp("VERSION");
			}
		});
		
		// Add control items
		control.add(op);
		control.add(deop);
		control.add(voice);
		control.add(devoice);
		control.add(kick);
		
		// ADD CTCP items
		ctcp.add(ping);
		ctcp.add(time);
		ctcp.add(version);
		
		// Add menus to popup menu
		tmpPopupMenu.add(whois);
		tmpPopupMenu.add(control);
		tmpPopupMenu.add(ctcp);;
		
		return tmpPopupMenu;
	}
	
	
	/**
	 * Inner class to send events if we are OP, else notify the user
	 * in the given channel
	 * @author Oyvind Sigerstad, Nils Slaaen, Bjorn-Erik Strand
	 *
	 */
	class ControlListener implements ActionListener {

		/**
		 * Handles events Kick, Op, Deop, Voice, and Devoice based on us having OP
		 * @param ae - The event
		 */
		public void actionPerformed(ActionEvent ae) {
			String ourNick = cm.getNick();
			String selectedNick = parseNick(getSelectedValue().toString());
			// If OP, execute given control event
			if(userModel.getOpList().contains(ourNick)) {
				
				if(ae.getActionCommand().equals("Kick")) {
					String reason = JOptionPane.showInputDialog("Reason:");
					Channel channel = cm.getChannel(channelName);
					
					if(reason.isEmpty()) channel.kick(selectedNick, "No reason");
					else channel.kick(selectedNick, reason);
				} 
	
				else if (ae.getActionCommand().equals("Op")) {
					cm.getChannel(channelName).op(parseNick(getSelectedValue().toString()));
				} 
				
				else if (ae.getActionCommand().equals("Deop")) {
					cm.getChannel(channelName).deop(parseNick(getSelectedValue().toString()));
				} 
				
				else if (ae.getActionCommand().equals("Voice")) {
					cm.getChannel(channelName).voice(parseNick(getSelectedValue().toString()));
				} 
				
				else if (ae.getActionCommand().equals("Devoice")) {
					cm.getChannel(channelName).deVoice(parseNick(getSelectedValue().toString()));
				}
			
			}  else {
				sIRC.tabContainer.message("-> Insufficient privileges for that action", channelName, TabComponent.CHANNEL, TabComponent.INFO);
			}
		}
		
	}
	
	/**
	 * Sends the ctcp event of either TIME, PING or VERSION
 	 * @param type - the type to send
	 */
	public void handleCtcp(String type) {
		String nick = parseNick(getSelectedValue().toString());
		cm.getSession().ctcp(nick, type);
		String localMessage = "-> [ " + nick + " ] " + type;
		sIRC.tabContainer.message(localMessage, channelName, TabComponent.CHANNEL, TabComponent.INFO);
	}

	/** 
	 * Displays the popup menu with actions on a user 
	 * @param me Mousevent - The Mousevent
	 * 
	 */
	public void showMenu(MouseEvent me) {
		popupMenu.show(this, me.getX(), me.getY());
	}
	
	/**
	 * Parses a nick and removes operator prefix if it exists
	 * @param nick - Nick to be parsed
	 * @return nick  - Nick without prefix
	 */
	public String parseNick(String nick) {
		if(nick.startsWith("@") || nick.startsWith("+")) {
			return nick.substring(1);
		} else 
		return nick;
	}

	/**
	 * Handles two left clicks on a user (open new or existing pm tab) or 
	 * right click on a user for popup menu with actions
	 */
	public void mouseClicked(MouseEvent me) {
		
		if(me.getClickCount() == 2) {
			String identifier = parseNick(getSelectedValue().toString());
			if(tabContainer.getTabIndex(identifier) != -1) {
				tabContainer.setSelectedIndex(tabContainer.getTabIndex(identifier));
			} else  {
				tabContainer.newTab(identifier, TabComponent.PM);
			}
		}
		
		if(SwingUtilities.isRightMouseButton(me)) {
			setSelectedIndex(locationToIndex(me.getPoint()) );
			showMenu(me);
		}
	}
	
	/**
	 * Method not implemented
	 */
	@Override
	public void mouseEntered(MouseEvent arg0) {}
	
	/**
	 * Method not implemented
	 */
	@Override
	public void mouseExited(MouseEvent arg0) {}

	/**
	 * Method not implemented
	 */
	@Override
	public void mousePressed(MouseEvent arg0) {}

	/**
	 * Method not implemented
	 */
	@Override
	public void mouseReleased(MouseEvent arg0) {}

}
