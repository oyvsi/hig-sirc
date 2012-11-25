package no.hig.sss.sirc;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

public class UserList extends JList {
	
	String channel;
	JPopupMenu popupMenu;
	ConnectionManagement cm = sIRC.conManagement;
	
	public UserList(String identifier, UsersContainer userContainer) {
		super(userContainer);
		channel = identifier;
		popupMenu = new JPopupMenu();
		this.add(popupMenu);
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(SwingUtilities.isRightMouseButton(e)) {
					setSelectedIndex(locationToIndex(e.getPoint()) );
					showMenu(e);
				}
				}
		});
		
		JMenu control = new JMenu("Control");
		JMenu ctcp = new JMenu("CTCP");
		JMenu dcc = new JMenu("DCC");
		
		JMenuItem info = new JMenuItem("Info");
		JMenuItem whois = new JMenuItem("Whois");
		JMenuItem query = new JMenuItem("Query");
		JMenuItem slap = new JMenuItem("Slap");
		
		JMenuItem ignore = new JMenuItem("Ignore");
		JMenuItem unignore = new JMenuItem("Unignore");
		JMenuItem op = new JMenuItem("Op");
		JMenuItem deop = new JMenuItem("Deop");
		JMenuItem voice = new JMenuItem("Voice");
		JMenuItem devoice = new JMenuItem("Devoice");
		JMenuItem kick = new JMenuItem("Kick");
		JMenuItem ban = new JMenuItem("Ban");
		
		JMenuItem send = new JMenuItem("Send");
		JMenuItem chat = new JMenuItem("Chat");
		
		JMenuItem ping = new JMenuItem("Ping");
		JMenuItem time = new JMenuItem("Time");
		JMenuItem version = new JMenuItem("Version");
		
		info.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cm.getChannel(channel).op(parseNick(getSelectedValue().toString()));
			}
		});
		
		whois.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cm.getSession().whois(parseNick(getSelectedValue().toString()));
			}
		});
		query.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		
		ignore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		
		unignore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		
		op.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cm.getChannel(channel).op(parseNick(getSelectedValue().toString()));
			}
		});
		
		deop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cm.getChannel(channel).deop(parseNick(getSelectedValue().toString()));
			}
		});
		
		voice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cm.getChannel(channel).voice(parseNick(getSelectedValue().toString()));
			}
		});
		
		devoice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cm.getChannel(channel).deVoice(parseNick(getSelectedValue().toString()));
			}
		});
		
		kick.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cm.getChannel(channel).kick(parseNick(getSelectedValue().toString()), "lol");
			}
		});
		
		ban.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		
		// Control
		control.add(ignore);
		control.add(unignore);
		control.add(deop);
		control.add(op);
		control.add(voice);
		control.add(kick);
		control.add(ban);
		
		// CTCP
		ctcp.add(ping);
		ctcp.add(time);
		ctcp.add(version);
		
		popupMenu.add(control);
		popupMenu.add(ctcp);
	}
	
	public void showMenu(MouseEvent e) {
		popupMenu.show(e.getComponent(), e.getX(), e.getY());
	}
	
	public String parseNick(String nick) {
		if(nick.startsWith("@") || nick.startsWith("+")) {
			return nick.substring(1);
		} else 
		return nick;
	}
}
