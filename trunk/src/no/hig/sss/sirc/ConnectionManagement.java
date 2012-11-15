package no.hig.sss.sirc;


import java.text.SimpleDateFormat;
import java.util.Date;

import jerklib.ConnectionManager;
import jerklib.Profile;
import jerklib.Session;
import jerklib.events.*;
import jerklib.events.IRCEvent.Type;
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
			//e.getSession().join("#teamhenkars.sirc");
		}
		else if (e.getType() == Type.CHANNEL_MESSAGE) {			
			MessageEvent me = (MessageEvent) e;
			Date timeStamp = new Date();
			SimpleDateFormat time = new SimpleDateFormat("HH:mm");
			String message = time.format(timeStamp) + " " + me.getNick() + " " + me.getMessage();
			sIRC.tabContainer.message(message, me.getChannel().getName(), TabComponent.CHANNEL);
			
			System.out.println(me.getChannel());
		}
		
		else if (e.getType() == Type.PRIVATE_MESSAGE) {
			MessageEvent me = (MessageEvent) e;
			Date timeStamp = new Date();
			SimpleDateFormat time = new SimpleDateFormat("HH:mm");
			String message = time.format(timeStamp) + " " + me.getNick() + " " + me.getMessage();
			sIRC.tabContainer.message(message, me.getNick(), TabComponent.PM);
		}
		
		else if (e.getType() == Type.JOIN_COMPLETE)
		{
		}
		
		else if (e.getType() == Type.CTCP_EVENT) {
			KickEvent ke = (KickEvent) e;
		}
		
		else {
			sIRC.tabContainer.message(e.getRawEventData(), "Console", TabComponent.CONSOLE);
			//System.out.println(e.getType() + " " + e.getRawEventData());
		}
	}
	
	public void channelMsg(String identifier, String msg, int type) {
		System.out.println("ID " + identifier + "Msg: " + msg);
		sIRC.tabContainer.message(msg, identifier, type);
		session.getChannel(identifier).say(msg);

	}
	
	public void privMsg(String nick, String msg) {
		session.sayPrivate(nick, msg);
	}
	
	public void newServer(String hostName) {
		manager.requestConnection(hostName);
	}
	
	public void joinChannel(String channelName) {
		System.out.println("Asked to join " + channelName);
		if(isConnected) {
			System.out.println("Connected, trying");
			session.join(channelName);
			sIRC.tabContainer.message("", channelName, TabComponent.CHANNEL);
		} else {
			sIRC.tabContainer.message("Can't join channel when not connected.", "Console", TabComponent.CONSOLE);
		}
	}
	
		
}
