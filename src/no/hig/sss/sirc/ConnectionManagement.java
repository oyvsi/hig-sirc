package no.hig.sss.sirc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import jerklib.ConnectionManager;
import jerklib.Profile;
import jerklib.Session;
import jerklib.events.*;
import jerklib.events.ErrorEvent.ErrorType;
import jerklib.events.IRCEvent.Type;
import jerklib.events.modes.ModeAdjustment.Action;
import jerklib.listeners.IRCEventListener;

public class ConnectionManagement implements IRCEventListener {
	private ConnectionManager manager;
	private Session session;
	private Profile profile;
	private Boolean isConnected;
	private SimpleDateFormat timeFormat;

	
	public ConnectionManagement() {
		timeFormat = new SimpleDateFormat("HH:mm");
		isConnected = false;
	}
	
	public void connect(String nick, String server) {
		if(isConnected == false) {
			profile = new Profile(nick);
			manager = new ConnectionManager(profile);
			session = manager.requestConnection(server);
			session.addIRCEventListener(this);
			sIRC.tabContainer.message("Connecting...", "Console", TabComponent.CONSOLE);
		}
		else {
			sIRC.tabContainer.message("You are allready connected. Disconnect first", "Console", TabComponent.CONSOLE);
		}
	}
	
	public void disConnect(String quitMsg) {
		if(isConnected) {
			session.close(quitMsg);
			isConnected = false;
			sIRC.tabContainer.closeAllTabs();
			sIRC.tabContainer.message("Disconnected from the server!", "Console", TabComponent.CONSOLE);
		}
	}
	
	public void ListChannels() {
		System.out.println("Fant " + session.getChannels().size() + " kanaler");
		
	}
	
	public void receiveEvent(IRCEvent e) {
		if (e.getType() == Type.CONNECT_COMPLETE) {
			System.out.println("Connection Complete!");
			sIRC.tabContainer.message("Connection complete!", "Console", TabComponent.CONSOLE);
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
		
		// TODO
		/*else if(e.getType() == Type.ERROR) { 
			ErrorEvent ee = (ErrorEvent) e;
			if(ee.getErrorType() == ErrorType.NUMERIC_ERROR) {
				NumericErrorEvent ne = (NumericErrorEvent) ee;
				if(ne.getNumeric() == 401) // No such nick or channel
					//sIRC.tabContainer.closeTab()	// NOES! need identifier
					
			}
		}*/
		else if (e.getType() == Type.PART) {
			PartEvent pe = (PartEvent) e;
			String channelName = pe.getChannelName();
			Date date = new Date();
			String time = timeFormat.format(date);
			// username != nick
			String actionMsg = time + "-!- " + pe.getUserName() + " " + pe.getHostName() + "  has left  " + pe.getPartMessage();
			sIRC.tabContainer.message(actionMsg, channelName, TabComponent.CHANNEL);
		}
		
		else if(e.getType() == Type.QUIT) {
			QuitEvent qe = (QuitEvent) e;
			System.out.println(qe.toString());
		}

		else if (e.getType() == Type.TOPIC) {
			TopicEvent te = (TopicEvent) e;
			String channelName = te.getChannel().getName();
			String topic = te.getTopic();
			if(topic.equals(""))
				topic = " ";
			sIRC.tabContainer.setTopText(channelName, topic);
			
			String actionMsg = timeFormat.format(te.getSetWhen()) + "-!- " + te.getSetBy() + 
							   " changed the topic of " + channelName + " to " + topic;
			sIRC.tabContainer.message(actionMsg, channelName, TabComponent.CHANNEL);
			
		}
		else if (e.getType() == Type.JOIN_COMPLETE) { 
			JoinCompleteEvent je = (JoinCompleteEvent) e;
			sIRC.tabContainer.message("", je.getChannel().getName(), TabComponent.CHANNEL);
			sIRC.tabContainer.setTopText(je.getChannel().getName(), je.getChannel().getTopic());
		}
		
		//else if (e.getType() == Type.CTCP_EVENT) {
		//	KickEvent ke = (KickEvent) e;
		//}
		else if(e.getType() == Type.CONNECTION_LOST) {
			isConnected = false;
		}
		
		else {
			sIRC.tabContainer.message(e.getRawEventData(), "Console", TabComponent.CONSOLE);
		}
	}
	
	public void channelMsg(String channelName, String msg, int type) {
		if(isConnected) {
			String message = buildSay(session.getNick(), msg);
			sIRC.tabContainer.message(message, channelName, type);
			session.getChannel(channelName).say(msg);
		}
	}
	
	public void privMsg(String toNick, String msg, int type) {
		if(isConnected) {
			System.out.println("Got PM. Going to " + toNick + " Message was " + msg);
			String message = buildSay(session.getNick(), msg);
			sIRC.tabContainer.message(message, toNick, type);
			session.sayPrivate(toNick, msg);
		}
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
	
	public void closeChat(String identifier, int type, String partMsg) {
		if(type == TabComponent.CHANNEL) {
			session.getChannel(identifier).part(partMsg);
			sIRC.tabContainer.closeTab(identifier);
		}
		else if(type == TabComponent.PM) {
			sIRC.tabContainer.closeTab(identifier);
		}
	}
	
	private String buildSay(String nick, String msg) {
		Date timeStamp = new Date();
		return timeFormat.format(timeStamp) + "  " + nick + " " + msg;
	}
	
	public List<String> getUsers(String channelName) {
		return session.getChannel(channelName).getNicks();
		
	}
	
	public List<String> getUsersMode(String channelName, Action action, char mode) {
		return session.getChannel(channelName).getNicksForMode(action, mode);
	}
	
	/*public boolean checkModeForUser(String channelName, Action action, char mode, String nick) {
		return session.getChannel(channelName).checkModeForUser(action, mode, nick);
	}*/


}
