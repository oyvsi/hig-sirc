package no.hig.sss.sirc;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import jerklib.Channel;
import jerklib.ConnectionManager;
import jerklib.Profile;
import jerklib.Session;
import jerklib.events.*;
import jerklib.events.ErrorEvent.ErrorType;
import jerklib.events.IRCEvent.Type;
import jerklib.events.modes.ModeAdjustment;
import jerklib.events.modes.ModeAdjustment.Action;
import jerklib.events.modes.ModeEvent.ModeType;
import jerklib.events.modes.ModeEvent;
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
			sIRC.tabContainer.consoleMsg(sIRC.i18n.getStr("connectionManagement.connecting"));
		}
		else {
			sIRC.tabContainer.consoleMsg(sIRC.i18n.getStr("error.alreadyConnected"));
		}
	}
	
	public void disConnect(String quitMsg) {
		if(isConnected) {
			session.close(quitMsg);
			isConnected = false;
			sIRC.tabContainer.closeAllTabs();
		}
	}
	
	public void ListChannels() {
		System.out.println("Fant " + session.getChannels().size() + " kanaler");
		
	}
	
	public void receiveEvent(IRCEvent e) {
		if (e.getType() == Type.CONNECT_COMPLETE) {
			String server = session.getServerInformation().getServerName();
			sIRC.tabContainer.consoleMsg(sIRC.i18n.getStr("connectionMangement.connected") + " " + server);
			isConnected = true;
		} 
		else if (e.getType() == Type.CHANNEL_MESSAGE) {	
			MessageEvent me = (MessageEvent) e;
			String message = buildSay(me.getNick(), me.getMessage());
			sIRC.tabContainer.message(message, me.getChannel().getName(), TabComponent.CHANNEL, TabComponent.CHANNEL);			
		}
		
		else if (e.getType() == Type.PRIVATE_MESSAGE) {
			MessageEvent me = (MessageEvent) e;
			String message = buildSay(me.getNick(), me.getMessage());
			sIRC.tabContainer.message(message, me.getNick(), TabComponent.PM, TabComponent.PM);
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
			String userName = pe.getUserName();
			String nickName = pe.getWho();
			String actionMsg = time + "-!- " + userName + '@' + pe.getHostName() 
							   + "  " + sIRC.i18n.getStr("channel.userPart") + "  " + pe.getPartMessage();
			sIRC.tabContainer.message(actionMsg, channelName, TabComponent.CHANNEL, TabComponent.INFO);
			sIRC.tabContainer.userLeft(channelName, nickName);
		}
		
		else if (e.getType() == Type.MODE_EVENT) {
			
			ModeEvent me = (ModeEvent) e;
			
			List<ModeAdjustment> modeList = me.getModeAdjustments();
			if(me.getModeType() == ModeType.CHANNEL) {
				String channelName = me.getChannel().getName();
				me.getModeAdjustments().iterator().next();
				Iterator<ModeAdjustment> modeIter = modeList.iterator();
				while(modeIter.hasNext()) {
					ModeAdjustment ma = modeIter.next();
					char mode = ma.getMode();
					Action action = ma.getAction();
					if(mode == 'o') {
							System.out.println("OP");
							sIRC.tabContainer.opMode(channelName, ma.getArgument(), action);
					}
					if(mode == 'v') {
							System.out.println("VOICE");
							sIRC.tabContainer.voiceMode(channelName, ma.getArgument(), action);
						
				
					}
				
				}
				
			}
		}
		
		else if(e.getType() == Type.QUIT) {
			QuitEvent qe = (QuitEvent) e;
			// Got to check channels user is in
			System.out.println(qe.toString());
		}

		else if (e.getType() == Type.TOPIC) {	// Sent on topic changes and channel joins (if topic is set)
			String topicMsg;
			TopicEvent te = (TopicEvent) e;
			String channelName = te.getChannel().getName();
			String topic = te.getTopic();
			String[] topicSetBy = te.getSetBy().split("~");

			sIRC.tabContainer.setTopText(channelName, topic);
			
			Date now = new Date();
			long timeDiff = now.getTime() - te.getSetWhen().getTime();
			if(timeDiff > 3*1000) {	// We assume a join won't take longer than 3 seconds, and this is the existing topic 
				topicMsg = timeFormat.format(now) + " -!- " + sIRC.i18n.getStr("topic.topicFor") 
				+ " " + channelName + ": " +  topic;
			}
			else {	// We're already in channel. So the topic is changed
				topicMsg = timeFormat.format(te.getSetWhen()) + " -!- " + topicSetBy[0] + " " + 
						   sIRC.i18n.getStr("topic.changed") + " " + channelName + " " + 
						   sIRC.i18n.getStr("topic.to") + " " + topic;
			}
			
			sIRC.tabContainer.message(topicMsg, channelName, TabComponent.CHANNEL, TabComponent.INFO);			
		}
		else if (e.getType() == Type.JOIN_COMPLETE) { 
			JoinCompleteEvent je = (JoinCompleteEvent) e;
			String topic = je.getChannel().getTopic();
			String channelName = je.getChannel().getName();
			sIRC.tabContainer.newTab(channelName, TabComponent.CHANNEL);
			if(topic.equals(""))	// No topic, we won't get a topic event
				topic = " ";
			sIRC.tabContainer.setTopText(channelName, topic);
		}
		

		else if (e.getType() == Type.JOIN) {
			JoinEvent je = (JoinEvent) e;
			String nick = je.getNick();
			String channelName = je.getChannelName();
			sIRC.tabContainer.userJoined(channelName, nick);
			
			
		}
		
		//else if (e.getType() == Type.CTCP_EVENT) {

		else if (e.getType() == Type.CTCP_EVENT) {
			CtcpEvent ce = (CtcpEvent) e;
			String ctcp = ce.getCtcpString();
			String identifier = ce.getNick();
			if(ce.getChannel() != null)
				identifier = ce.getChannel().getName();
			
			if(ctcp.startsWith("ACTION")) {
				String actionMsg = ctcp.substring("ACTION".length() + 1);
				String printMsg = buildSay("* " + ce.getNick(), actionMsg);
				int type = sIRC.tabContainer.getType(identifier);
				
				if(type == TabComponent.CHANNEL)
					sIRC.tabContainer.message(printMsg, identifier, type, TabComponent.INFO);
				else if(type == TabComponent.PM)
					sIRC.tabContainer.message(printMsg, identifier, type, TabComponent.INFO);
			}
			

		//	KickEvent ke = (KickEvent) e;
		}
	
		else if(e.getType() == Type.CONNECTION_LOST) {
			isConnected = false;
			sIRC.tabContainer.consoleMsg(sIRC.i18n.getStr("connectionManagement.disconnected"));
		}
		
		else {
			sIRC.tabContainer.consoleMsg(e.getRawEventData());
		}
	}
	
	public void channelMsg(String channelName, String msg) {
		if(isConnected) {
			String message = buildSay(session.getNick(), msg);
			sIRC.tabContainer.message(message, channelName, TabComponent.CHANNEL, TabComponent.CHANNEL);
			session.getChannel(channelName).say(msg);
		}
	}
	
	public void actionMsg(String identifier, String msg, int type) {
		if(isConnected && (type == TabComponent.PM || type == TabComponent.CHANNEL)) {
			String printMsg = buildSay("* " + session.getNick(), msg);
			session.action(identifier, "ACTION " + msg);	
			sIRC.tabContainer.message(printMsg, identifier, type, TabComponent.INFO);
		}
	}
	
	public void privMsg(String toNick, String msg) {
		if(isConnected) {
			System.out.println("Got PM. Going to " + toNick + " Message was " + msg);
			String message = buildSay(session.getNick(), msg);
			sIRC.tabContainer.message(message, toNick, TabComponent.PM, TabComponent.PM);
			session.sayPrivate(toNick, msg);
		}
	}
	
	public void newServer(String hostName) {
		manager.requestConnection(hostName);
	}
	
	public void joinChannel(String channelName) {
		System.out.println("Asked to join " + channelName);
		if(isConnected) {
			if(session.isChannelToken(channelName))
				session.join(channelName);
			else
				sIRC.tabContainer.consoleMsg(sIRC.i18n.getStr("error.invalidChannelName") + " " + channelName);
		} else {
			sIRC.tabContainer.consoleMsg(sIRC.i18n.getStr("error.joinDisconnected"));
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
		return timeFormat.format(timeStamp) + "  " + nick + "  " + msg;
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
