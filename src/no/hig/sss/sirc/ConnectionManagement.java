package no.hig.sss.sirc;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
			sIRC.tabContainer.consoleMsg(sIRC.i18n.getStr("connectionManagement.disconnected"));
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
		
		else if(e.getType() == Type.ERROR) { 
			ErrorEvent ee = (ErrorEvent) e;
			if(ee.getErrorType() == ErrorType.NUMERIC_ERROR) {
				NumericErrorEvent ne = (NumericErrorEvent) ee;
				
				if(ne.getNumeric() == 401) { // No such nick or channel
					// Messages from server look like: ":dreamhack.se.quakenet.org 401 oyvsiSirc oyvsi99999 :No such nick"
					Pattern nickRegex = Pattern.compile("^.+401\\s(\\S+)\\s(\\S+).+$");
					Matcher nickMatch = nickRegex.matcher(ne.getRawEventData());
					if(nickMatch.matches()) {
						String msg = buildInfoPrefix() + sIRC.i18n.getStr("error.noSuchNick");
						sIRC.tabContainer.message(msg, nickMatch.group(2), TabComponent.PM, TabComponent.INFO);
					}
				}
			}	
		}	
		else if (e.getType() == Type.PART) {
			PartEvent pe = (PartEvent) e;
			String channelName = pe.getChannelName();
			String userName = pe.getUserName();
			String nickName = pe.getWho();
			
			if(nickName != session.getNick()) {
				String actionMsg = buildInfoPrefix() + nickName + " [" + userName + '@' + pe.getHostName() 
								   + "]  " + sIRC.i18n.getStr("channel.userPart") + "  " + pe.getPartMessage();
				sIRC.tabContainer.message(actionMsg, channelName, TabComponent.CHANNEL, TabComponent.INFO);
				sIRC.tabContainer.userLeft(channelName, nickName);
			}
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
			String nickName = qe.getNick();
			String msg = buildInfoPrefix() + " " + nickName + " [" + qe.getUserName() + 
					     "@" + qe.getHostName() + "] " + sIRC.i18n.getStr("channel.userQuit") + 
					     " [" + qe.getQuitMessage() + "]";
			
			sIRC.tabContainer.userQuit(nickName, msg);
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

			String message = buildInfoPrefix() + 
							 nick + " [" + je.getUserName() + "@" + je.getHostName() + "] " + 
							 sIRC.i18n.getStr("channel.userJoin") + " " + channelName;
			sIRC.tabContainer.message(message, channelName, TabComponent.CHANNEL, TabComponent.INFO);
		}
		

		else if (e.getType() == Type.WHOIS_EVENT) {
			WhoisEvent we = (WhoisEvent) e;
			String nick = we.getNick();
			String hostName = we.getHost();
			String realName = we.getRealName();
			String userName = we.getUser();
			String message = nick;
			
			
		}
		else if (e.getType() == Type.NICK_CHANGE) {
			NickChangeEvent nce = (NickChangeEvent) e;
			String oldNick = nce.getOldNick();
			String newNick = nce.getNewNick();

			String msg;
			if(newNick.equals(session.getNick())) 	// We changed our own nick
				msg = buildInfoPrefix() + " " + sIRC.i18n.getStr("channel.selfNickChange") + " " + newNick;
			else
				msg = buildInfoPrefix() + " " + oldNick 
					  + sIRC.i18n.getStr("channel.nickChange") + " " + newNick;	
	
			sIRC.tabContainer.nickChange(oldNick, newNick, msg);

		}

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
	
		else if(e.getType() == Type.KICK_EVENT) {
			System.out.println("KICK");
			KickEvent ke = (KickEvent) e;
			sIRC.tabContainer.userLeft(ke.getChannel().getName(), ke.getWho());
			
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
	
	private String buildInfoPrefix() {
		Date date = new Date();
		return timeFormat.format(date) + " -!- ";
	}
	
	public List<String> getUsers(String channelName) {
		return session.getChannel(channelName).getNicks();
	}
	
	public void changeNick(String newNick) {
		if(isConnected)
			session.changeNick(newNick);
		System.out.println("Current nick: " + sIRC.options.getNick());
		sIRC.options.setNick(newNick);
		System.out.println("New nick: " + sIRC.options.getNick());
		
	}
	
	public List<String> getUsersMode(String channelName, Action action, char mode) {
		return session.getChannel(channelName).getNicksForMode(action, mode);
	}
	
	public List<String> getChannelsWithUser(Session session, String nick) {
		Iterator<Channel >channelIter = session.getChannels().iterator();
		List<String> channels = new ArrayList<String>();
		while(channelIter.hasNext()) {
			Channel channel = channelIter.next();
			List<String> tmpnicks = channel.getNicks();
			if(tmpnicks.contains(nick)) {
				channels.add(channel.getName());
			}
		}
		return channels;
	}
	
	public Channel getChannel(String identifier) {
		return session.getChannel(identifier);
	}
	
	public Session getSession() {
		return session;
	}
	
	/*public boolean checkModeForUser(String channelName, Action action, char mode, String nick) {
		return session.getChannel(channelName).checkModeForUser(action, mode, nick);
	}*/

	

}
