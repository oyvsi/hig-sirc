package no.hig.sss.sirc;


import jerklib.Channel;
import jerklib.ConnectionManager;
import jerklib.Profile;
import jerklib.Session;
import jerklib.events.*;
import jerklib.events.IRCEvent.Type;
import jerklib.listeners.IRCEventListener;


public class ConnectionManagement implements IRCEventListener

{
	private ChatManager chatManager;
	private static  ConnectionManager manager;
	private Channel channel;
	public Session session;
	Profile profile;
	
	
	public ConnectionManagement() 
	{
		profile = new Profile("golvSIRC");
		manager = new ConnectionManager(profile);
		session = manager.requestConnection("irc.homelien.no");
		session.addIRCEventListener(this);
		chatManager = new ChatManager(this);
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
			e.getSession().join("#teamhenkars.siirc");
			chatManager.createChat("#teamhenkars.sirc",  "CHANNEL", profile.getActualNick());
			chatManager.createChat("#teamhenkars.siirc", "CHANNEL", profile.getActualNick());
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			ListChannels();
		}
		else if (e.getType() == Type.CHANNEL_MESSAGE)
		{
			MessageEvent me = (MessageEvent) e;
			chatManager.relayRemoteChannelMessage(me.getNick(), me.getMessage(), me.getChannel().getName());
			System.out.println(me.getChannel());
		}
		
		else if (e.getType() == Type.PRIVATE_MESSAGE)
		{
			MessageEvent me = (MessageEvent) e;
			if(!(chatManager.getMap().containsKey(me.getNick()))) {
				chatManager.createChat(me.getNick(), "PRIVATE", profile.getActualNick());
			}
			chatManager.relayRemotePrivateMessage(me.getNick(), me.getMessage());
			System.out.println(me.getChannel());
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
			System.out.println(e.getType() + " " + e.getRawEventData());
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
	

