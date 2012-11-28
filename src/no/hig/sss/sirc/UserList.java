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

/**
 * UserList is used for the visualization of users
 * 
 *
 */
public class UserList extends JList<String> implements MouseListener {
	private static final long serialVersionUID = 1L;
	private TabContainer tabContainer = sIRC.tabContainer;
	private String channelName;
	private JPopupMenu popupMenu;
	private ConnectionManagement cm = sIRC.conManagement;
	
	
	/**
	 * Constructor for UserList, creates popup menu and adds action listeners
	 * @param identifier
	 * @param userContainer
	 */
	public UserList(String identifier, UserModel userModel) {
		super(userModel);
		channelName = identifier;
		popupMenu = createPopupMenu();
		add(popupMenu);
		addMouseListener(this);
	}
	
	private JPopupMenu createPopupMenu() {
		JPopupMenu tmpPopupMenu = new JPopupMenu();
		JMenu control = new JMenu("Control");
		JMenu ctcp = new JMenu("CTCP");
	
		JMenuItem whois = new JMenuItem("Whois");
		JMenuItem query = new JMenuItem("Query");
		JMenuItem slap = new JMenuItem("Slap");
	
		JMenuItem op = new JMenuItem("Op");
		JMenuItem deop = new JMenuItem("Deop");
		JMenuItem voice = new JMenuItem("Voice");
		JMenuItem devoice = new JMenuItem("Devoice");
		JMenuItem kick = new JMenuItem("Kick");
		
		JMenuItem ping = new JMenuItem("Ping");
		JMenuItem time = new JMenuItem("Time");
		JMenuItem version = new JMenuItem("Version");
		
		whois.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cm.getSession().whois(parseNick(getSelectedValue().toString()));
			}
		});
		query.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		
		slap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		
		op.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cm.getChannel(channelName).op(parseNick(getSelectedValue().toString()));
			}
		});
		
		deop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cm.getChannel(channelName).deop(parseNick(getSelectedValue().toString()));
			}
		});
		
		voice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cm.getChannel(channelName).voice(parseNick(getSelectedValue().toString()));
			}
		});
		
		devoice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cm.getChannel(channelName).deVoice(parseNick(getSelectedValue().toString()));
			}
		});
		
		kick.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String reason = JOptionPane.showInputDialog("Reason");
				Channel channel = cm.getChannel(channelName);
				String nick = parseNick(getSelectedValue().toString());
				if(reason.isEmpty()) {
					channel.kick(nick, "No reason");
				} else {
					channel.kick(nick, reason);
				}
				
				
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
		
		// Control items
		control.add(op);
		control.add(deop);
		control.add(voice);
		control.add(devoice);
		control.add(kick);
		
		// CTCP items
		ctcp.add(ping);
		ctcp.add(time);
		ctcp.add(version);
		
		// Add menu items to popup menu
		tmpPopupMenu.add(whois);
		tmpPopupMenu.add(control);
		tmpPopupMenu.add(ctcp);;
		
		return tmpPopupMenu;
	}
	
	
	public void handleCtcp(String type) {
		String nick = parseNick(getSelectedValue().toString());
		cm.getSession().ctcp(nick, type);
		String localMessage = "-> [" + nick + "]" + type;
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
	 * 
	 * 
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

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
