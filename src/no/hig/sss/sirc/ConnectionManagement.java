package no.hig.sss.sirc;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import jerklib.ConnectionManager;
import jerklib.Profile;
import jerklib.Session;
import jerklib.events.*;
import jerklib.events.IRCEvent.Type;
import jerklib.events.impl.MessageEventImpl;
import jerklib.listeners.IRCEventListener;


public class ConnectionManagement implements IRCEventListener {
	private ConnectionManager manager;
	private Session session;
	private Profile profile;
	private Boolean isConnected;
	
	public ConnectionManagement() {
		isConnected = false;
	}
	
	public void connect(String nick, String server) {
		profile = new Profile(nick);
		manager = new ConnectionManager(profile);
		session = manager.requestConnection(server);
		session.addIRCEventListener(this);
	}
	
	public void ListChannels() {
		System.out.println("Fant " + session.getChannels().size() + " kanaler");
		
	}
	
	
	public void receiveEvent(IRCEvent e) {
		if (e.getType() == Type.CONNECT_COMPLETE) {
			System.out.println("Connection Complete!");
			isConnected = true;
		}
		else if (e.getType() == Type.CHANNEL_MESSAGE) {	
			MessageEvent me = (MessageEvent) e;
			String message = buildSay(me.getNick(), me.getMessage());
			sIRC.tabContainer.message(message, me.getChannel().getName(), TabComponent.CHANNEL);			
		}
		
		else if (e.getType() == Type.PRIVATE_MESSAGE) {
			MessageEvent me = (MessageEvent) e;
			String message = buildSay(me.getNick(), me.getMessage());
			sIRC.tabContainer.message(message, me.getNick(), TabComponent.PM);
		}
		
		else if (e.getType() == Type.JOIN_COMPLETE) { 
			JoinCompleteEvent je = (JoinCompleteEvent) e;
			sIRC.tabContainer.message("", je.getChannel().getName(), TabComponent.CHANNEL);
		}
		
		else if (e.getType() == Type.CTCP_EVENT) {
			KickEvent ke = (KickEvent) e;
		}
		
		else {
			sIRC.tabContainer.message(e.getRawEventData(), "Console", TabComponent.CONSOLE);
		}
	}
	
	public void channelMsg(String channelName, String msg, int type) {
		String message = buildSay(session.getNick(), msg);
		sIRC.tabContainer.message(message, channelName, type);
		session.getChannel(channelName).say(msg);

	}
	
	public void privMsg(String toNick, String msg, int type) {
		String message = buildSay(session.getNick(), msg);
		sIRC.tabContainer.message(message, toNick, type);
		session.sayPrivate(toNick, msg);
	}
	
	public void newServer(String hostName) {
		manager.requestConnection(hostName);
	}
	
	public void joinChannel(String channelName) {
		System.out.println("Asked to join " + channelName);
		if(isConnected) {
			System.out.println("Connected, trying");
			session.join(channelName);
		} else {
			sIRC.tabContainer.message("Can't join channel when not connected.", "Console", TabComponent.CONSOLE);
		}
	}
	
	private String buildSay(String nick, String msg) {
		Date timeStamp = new Date();
		SimpleDateFormat time = new SimpleDateFormat("HH:mm");
		return time.format(timeStamp) + "  " + nick + " " + msg;
	}
	
	public List<String> getUsers(String channelName) {
		return session.getChannel(channelName).getNicks();
	}
		
}
