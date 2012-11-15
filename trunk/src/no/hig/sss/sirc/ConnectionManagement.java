package no.hig.sss.sirc;


import java.text.SimpleDateFormat;
import java.util.Date;

import jerklib.Channel;
import jerklib.ConnectionManager;
import jerklib.Profile;
import jerklib.Session;
import jerklib.events.*;
import jerklib.events.IRCEvent.Type;
import jerklib.listeners.IRCEventListener;


public class ConnectionManagement implements IRCEventListener

{
	private TabContainer tabContainer;
	private static ConnectionManager manager;
	private Channel channel;
	public Session session;
	Profile profile;
	
	
	public ConnectionManagement() 
	{
		profile = new Profile("golvSIRC");
		manager = new ConnectionManager(profile);
		session = manager.requestConnection("se.quakenet.org");
		session.addIRCEventListener(this);
		tabContainer = sIRC.tabContainer;
	}
	
	public void ListChannels() {
		System.out.println("Fant " + session.getChannels().size() + " kanaler");
		
	}
	
	
	public void receiveEvent(IRCEvent e)
	{
		
		
		if (e.getType() == Type.CONNECT_COMPLETE)
		{
			System.out.println("Connection Complete!");
			e.getSession().join("#teamhenkars.sirc");

		}
		else if (e.getType() == Type.CHANNEL_MESSAGE)
		{
			
			MessageEvent me = (MessageEvent) e;
			Date timeStamp = new Date();
			SimpleDateFormat time = new SimpleDateFormat("HH:mm");
			String message = time.format(timeStamp) + " " + me.getNick() + " " + me.getMessage();
			tabContainer.message(message, me.getChannel().getName(), TabComponent.CHANNEL);
			
			System.out.println(me.getChannel());
		}
		
		else if (e.getType() == Type.PRIVATE_MESSAGE)
		{
			MessageEvent me = (MessageEvent) e;
			Date timeStamp = new Date();
			SimpleDateFormat time = new SimpleDateFormat("HH:mm");
			String message = time.format(timeStamp) + " " + me.getNick() + " " + me.getMessage();
			tabContainer.message(message, me.getNick(), TabComponent.PM);
		}
		
		else if (e.getType() == Type.JOIN_COMPLETE)
		{
		}
		
		else if (e.getType() == Type.CTCP_EVENT)
		{
			KickEvent ke = (KickEvent) e;
			
		}
		
		else
		{
			tabContainer.message(e.getRawEventData(), "Console", TabComponent.CONSOLE);
			//System.out.println(e.getType() + " " + e.getRawEventData());
		}
	}
	
	public void channelMsg(String channelName, String msg) {
		session.getChannel(channelName).say(msg);
	}
	
	public void privMsg(String nick, String msg) {
		session.sayPrivate(nick, msg);
	}
	
	public void newServer(String hostName) {
		manager.requestConnection(hostName);
	}
	
	public void newChannel(String channelName) {
		if(session.isConnected()) session.join(channelName);
	}
		
}
	

