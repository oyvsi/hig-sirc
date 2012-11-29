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


/**
 * Handles all the communication between client and server
 * @author Oyvind Sigerstad, Nils Slaaen, Bjorn-Erik Strand
 *
 */
public class ConnectionManagement implements IRCEventListener {
	private ConnectionManager manager;
	private Session session;
	private Profile profile;
	private Boolean isConnected;
	private SimpleDateFormat timeFormat;

	/**
	 * Constructor 
	 * Sets the timeformat and if we are connected or not
	 */
	public ConnectionManagement() {
		timeFormat = new SimpleDateFormat("HH:mm");
		isConnected = false;
	}
	
	/**
	 * Connects to a given server with user specified information
	 * @param nick the nick to connect with
	 * @param server the server to connect to
	 */
	public void connect(String nick, String server) {
		if(isConnected) {
			sIRC.tabContainer.consoleMsg(sIRC.i18n.getStr("error.alreadyConnected"));
		}
		if(isConnected == false && validateNick(nick) && validateServer(server)) {
			String userName = sIRC.options.getUserName(); // Get username from options-panel
			
			if(userName.length() > 1) { // Is username defined in options-panel
				String altNick = sIRC.options.getAltNick();
				if(altNick.length() > 0 && validateNick(altNick)) // Alt nick is defined
					profile = new Profile(userName, nick, altNick, altNick + "_42");
				else
					profile = new Profile(userName, nick);
			} else {
				profile = new Profile(nick);
			}
			
			manager = new ConnectionManager(profile);  // jerklibs manager
			session = manager.requestConnection(server); // session for connection
			
			session.addIRCEventListener(this);  // this class will receive all irc events
			sIRC.tabContainer.consoleMsg(sIRC.i18n.getStr("connectionManagement.connecting"));
		}
	}
	
	/**
	 * Validates the user specified nick based on RFC 2812
	 * 
	 * @param nick the nickname to validate
	 * @return valid valid or not valid
	 */
	public boolean validateNick(String nick) { // Valid chars are 0-9a-z\[]^_`{|}- Can't begin with 0-9 or -
		String nickRegex = "(?i)^[a-z\\\\\\[\\]^_`{|}][0-9a-z\\\\\\[\\]^_`{|}-]{2,15}$";
		Boolean valid = false;
		if(nick.matches(nickRegex))
			valid = true;
		else
			sIRC.tabContainer.consoleMsg(sIRC.i18n.getStr("error.invalidNick"));
		return valid;
	}
	
	/**
	 * Validates the server name
	 * @param server the server hostname
	 * @return valid valid or not valid
	 */
	public boolean validateServer(String server) {
		Boolean valid = false;
		if(server.length() > 3)
			valid = true;
		else
			sIRC.tabContainer.consoleMsg(sIRC.i18n.getStr("error.invalidServer"));
		return valid;
	}

	/**
	 * Handles client-side quit
	 * @param quitMsg the quit message to send to the server
	 */
	public void disConnect(String quitMsg) {
		if(isConnected) {
			session.close(quitMsg);
			isConnected = false;
			sIRC.tabContainer.closeAllTabs();
			sIRC.tabContainer.consoleMsg(sIRC.i18n.getStr("connectionManagement.disconnected"));
		}
	}
	
	/**
	 * Asks server for a list of available servers. We will get a CHANNEL_LIST_EVENT for
	 * every channel on the server. This means A LOT of traffic on big servers.
	 */
	public void ListChannels() {
		String msg = "Attempting to get a channel list from server " + session.getServerInformation().getServerName();
		sIRC.tabContainer.message(msg, "Console", TabComponent.CONSOLE, TabComponent.INFO);
		session.chanList(); // Ask for a list, we'll get an event back
	}
	
	/**
	 * Sets the topic in the channel window for the given channel 
	 * @param channel the channel to set topic for
	 * @param topic the topic to set
	 */
	public void setTopic(String channel, String topic) {
		String nick = session.getNick();
		if(getUsersMode(channel, Action.PLUS, 'o').contains(nick)) 
			session.getChannel(channel).setTopic(topic);
		else
			sIRC.tabContainer.consoleMsg(sIRC.i18n.getStr("error.TopicDenied") + " " + channel);
	}
	
	/**
	 * Handles client-side use of away event
	 * @param awayMsg the away message
	 */
	public void away(String awayMsg) {
		boolean away = (awayMsg != null);
		if(session.isAway() && away == false)
			session.unsetAway();
		else if(away)
			session.setAway(awayMsg);
		sIRC.tabContainer.away(away);	// Toggle away in tabs, so we can remind user
	}
	
	/**
	 * Handles all events from server
	 */
	public void receiveEvent(IRCEvent e) {  
		if (e.getType() == Type.CONNECT_COMPLETE) { // We have connected to irc server
			String server = session.getServerInformation().getServerName();
			sIRC.tabContainer.consoleMsg(sIRC.i18n.getStr("connectionMangement.connected") + " " + server);
			isConnected = true;
		} 

		else if (e.getType() == Type.CHANNEL_MESSAGE) {	// message from others from channel we're in
			MessageEvent me = (MessageEvent) e;
			String message = buildSay(me.getNick(), me.getMessage());
			sIRC.tabContainer.message(message, me.getChannel().getName(), TabComponent.CHANNEL, TabComponent.CHANNEL);			
		}

		else if (e.getType() == Type.PRIVATE_MESSAGE) { // private message going to us
			MessageEvent me = (MessageEvent) e;
			String message = buildSay(me.getNick(), me.getMessage());
			sIRC.tabContainer.message(message, me.getNick(), TabComponent.PM, TabComponent.PM);
		}
		
		else if(e.getType() == Type.ERROR) { // Errors undefined by jerklib
			ErrorEvent ee = (ErrorEvent) e;
			if(ee.getErrorType() == ErrorType.NUMERIC_ERROR) {
				NumericErrorEvent ne = (NumericErrorEvent) ee;
				
				if(ne.getNumeric() == 401) { // No such nick or channel
					// Messages from server look like: ":dreamhack.se.quakenet.org 401 oyvsiSirc oyvsi99999 :No such nick"
					Pattern nickRegex = Pattern.compile("^.+401\\s(\\S+)\\s(\\S+).+$");
					Matcher nickMatch = nickRegex.matcher(ne.getRawEventData());
					
					if(nickMatch.matches()) {  // Error message is in a format we know
						String msg = buildInfoPrefix() + sIRC.i18n.getStr("error.noSuchNick");
						sIRC.tabContainer.message(msg, nickMatch.group(2), TabComponent.PM, TabComponent.INFO);
					}
				}
			}	
		}	
		
		else if (e.getType() == Type.PART) {  // We or someone else leaves channel we're in
			PartEvent pe = (PartEvent) e;
			String channelName = pe.getChannelName();
			String userName = pe.getUserName();
			String nickName = pe.getWho();
			
			if(nickName.equals(session.getNick()) == false) {	// We don't want to notify tab if we left
				String actionMsg = buildInfoPrefix() + nickName + " [" + userName + '@' + pe.getHostName() 
								   + "]  " + sIRC.i18n.getStr("channel.userPart") + "  " + pe.getPartMessage();
				sIRC.tabContainer.message(actionMsg, channelName, TabComponent.CHANNEL, TabComponent.INFO);
				sIRC.tabContainer.userLeft(channelName, nickName);
			}
		}
		
		else if(e.getType() == Type.AWAY_EVENT) {	// Away message from server. Us or someone we talk to
			AwayEvent ae = (AwayEvent) e;
			if(ae.isYou() == false) {
				String nick = ae.getNick();
				String awayMsg = buildInfoPrefix() + nick + " " + 
							     sIRC.i18n.getStr("pm.userAway") + ": " + ae.getAwayMessage();
				sIRC.tabContainer.message(awayMsg, nick, TabComponent.PM, TabComponent.INFO);
			}
		}
		
		else if (e.getType() == Type.MODE_EVENT) {  // Mode is changed for us or someone in a channel we're joined
			
			ModeEvent me = (ModeEvent) e;
			
			List<ModeAdjustment> modeList = me.getModeAdjustments();
			if(me.getModeType() == ModeType.CHANNEL) {
				String channelName = me.getChannel().getName();
				Iterator<ModeAdjustment> modeIter = modeList.iterator();
			
				while(modeIter.hasNext()) {
					ModeAdjustment ma = modeIter.next();
					char mode = ma.getMode();
					Action action = ma.getAction();
					String nick = ma.getArgument();
					String modeText = (action == Action.PLUS) ? "+" : "-";
					String msg = buildInfoPrefix() + sIRC.i18n.getStr("channel.mode") + 
								 "/" + channelName  + " [" + modeText + mode + " " + nick + "]" + 
							     " " + sIRC.i18n.getStr("channel.by") + " " + me.setBy(); 
					

					sIRC.tabContainer.modeChange(channelName);	

					if(mode == 'o' || mode == 'v')
						sIRC.tabContainer.message(msg, channelName, TabComponent.CHANNEL, TabComponent.INFO);
				}				
			}
		}
		
		else if(e.getType() == Type.QUIT) {	// Someone quits and thus leaves a channel we're in
			QuitEvent qe = (QuitEvent) e;
			String nickName = qe.getNick();
			String msg = buildInfoPrefix() + " " + nickName + " [" + qe.getUserName() + 
					     "@" + qe.getHostName() + "] " + sIRC.i18n.getStr("channel.userQuit") + 
					     " [" + qe.getQuitMessage() + "]";
			
			sIRC.tabContainer.userQuit(nickName, msg);	// Pass the quit message to the tab
		}

		else if (e.getType() == Type.TOPIC) {  // Sent on topic changes and channel joins (if topic is set)
			String topicMsg;
			TopicEvent te = (TopicEvent) e;
			String channelName = te.getChannel().getName();
			String topic = te.getTopic();
			String[] topicSetBy = te.getSetBy().split("~");

			sIRC.tabContainer.setTopText(channelName, topic);  // Update top text in tab to the new topic
			long timeNow = System.currentTimeMillis();
			long timeSet = te.getSetWhen().getTime();
			long timeDiff = (timeNow - timeSet) / 1000;
			
			if(timeDiff > 3) {	// We assume a join won't take longer than 3 seconds, and this is the existing topic 
				topicMsg = buildInfoPrefix() + sIRC.i18n.getStr("topic.topicFor") 
				+ " " + channelName + ": " +  topic;
			}
			
			else {	// We're already in channel. So the topic is changed
				topicMsg = timeFormat.format(te.getSetWhen()) + " -!- " + topicSetBy[0] + " " + 
						   sIRC.i18n.getStr("topic.changed") + " " + channelName + " " + 
						   sIRC.i18n.getStr("topic.to") + " " + topic;
			}
			sIRC.tabContainer.message(topicMsg, channelName, TabComponent.CHANNEL, TabComponent.INFO);			
		}
		
		else if (e.getType() == Type.JOIN_COMPLETE) {	// We have successfully joined a channel
			JoinCompleteEvent je = (JoinCompleteEvent) e;
			String topic = je.getChannel().getTopic();
			String channelName = je.getChannel().getName();
			sIRC.tabContainer.newTab(channelName, TabComponent.CHANNEL);
			if(topic.equals(""))	// No topic, we won't get a topic event
				topic = " ";
			sIRC.tabContainer.setTopText(channelName, topic);  // blank the top text in tab
		}
		
		else if (e.getType() == Type.JOIN) {  // Someone joined a channel we're in
			JoinEvent je = (JoinEvent) e;
			String nick = je.getNick();
			String channelName = je.getChannelName();
			sIRC.tabContainer.userJoined(channelName, nick);

			String message = buildInfoPrefix() + 
							 nick + " [" + je.getUserName() + "@" + je.getHostName() + "] " + 
							 sIRC.i18n.getStr("channel.userJoin") + " " + channelName;
			sIRC.tabContainer.message(message, channelName, TabComponent.CHANNEL, TabComponent.INFO);
		}
		

		else if (e.getType() == Type.WHOIS_EVENT) {	// A reply for a whois message
			WhoisEvent we = (WhoisEvent) e;
			String nick = we.getNick();
			String hostName = we.getHost();
			String userName = we.getUser();
			String realName = we.getRealName();
			String server = we.whoisServer();
			String infoPrefix = buildInfoPrefix();
			String whoismessage = infoPrefix +  "WHOIS " + we.getNick()  + "  [" + we.getUser() + "@" 
											 + we.getHost() + "] " + '\n' + 
								  infoPrefix + "ircname	: " + realName + '\n' +
								  infoPrefix + "channels: " + we.getChannelNames() + '\n' +
								  infoPrefix + "server: " + server + '\n' +
								  infoPrefix + "END OF WHOIS";
			sIRC.tabContainer.consoleMsg(whoismessage);
			
		}
		else if (e.getType() == Type.NICK_CHANGE) {	 // We or someone in a channel or pm we're in changed nick
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

		else if (e.getType() == Type.CTCP_EVENT) {	// All CTCP events
			CtcpEvent ce = (CtcpEvent) e;
			String ctcp = ce.getCtcpString();
			System.out.println(ctcp + "   " + ce.getMessage());
			String identifier = ce.getNick();
			
			if(ctcp.startsWith("ACTION")) {	// /me events
				String actionMsg = ctcp.substring("ACTION".length() + 1);
				String printMsg = buildSay("* " + ce.getNick(), actionMsg);
				int type = sIRC.tabContainer.getType(identifier);
				
				if(type == TabComponent.CHANNEL)
					sIRC.tabContainer.message(printMsg, ce.getChannel().getName(), type, TabComponent.INFO);
				else if(type == TabComponent.PM)
					sIRC.tabContainer.message(printMsg, identifier, type, TabComponent.INFO);
			}
			
			if(ctcp.startsWith("PING")) // needs 001 to be sent to user
				session.notice(ce.getNick(), "\001" + ce.getCtcpString() + "\001"); 	
			
			if(ctcp.startsWith("VERSION")) {
				session.notice(ce.getNick(), "\001" + "VERSION sIRC v1.0" + "\001");
			}
			
			if(ctcp.startsWith("TIME")) {
				SimpleDateFormat sdf = new SimpleDateFormat("E M d hh:mm:ss y");
				Date date = new Date();
				sdf.format(date);
				session.notice(ce.getNick(),"\001 TIME " + date + "\001");
			}
	}
		
		else if(e.getType() == Type.CONNECTION_LOST) {	// We lost connection
			isConnected = false;
			sIRC.tabContainer.consoleMsg(sIRC.i18n.getStr("connectionManagement.disconnected"));
		}
		
		else if(e.getType() == Type.CHANNEL_LIST_EVENT) { // We requested a channel list
			ChannelListEvent ce = (ChannelListEvent) e;
			String msg = ce.getChannelName() + "(" + ce.getNumberOfUser() + ") " + ce.getTopic();
			sIRC.tabContainer.message(msg, "Console", TabComponent.CONSOLE, TabComponent.INFO);
		}
		
		else if(e.getRawEventData().contains(" KICK ")) {	// Jerklib has a bug with kick-events. So we parse raw
			String rawData = e.getRawEventData();			// Someone in a channel or pm we're in got kicked
			String [] colonSplit = rawData.split(":");
			String [] excSplit = colonSplit[1].split("!");
			String kicker = excSplit[0];
			String rest = excSplit[1];
			String [] spaceSplit  = rest.split(" ");
			String channel = spaceSplit[2];
			String victim = spaceSplit[3];
			String reason = colonSplit[2];
			sIRC.tabContainer.userLeft(channel, victim);
			String kickMessage = buildInfoPrefix() + victim + " was kicked from " + channel + " by " + kicker + " [" + reason +"] ";
			sIRC.tabContainer.message(kickMessage, channel, TabComponent.CHANNEL, TabComponent.INFO);
						
		} else { // All inimplemented events	
			sIRC.tabContainer.consoleMsg(e.getRawEventData());
		}
	}
	
	
	/**
	 * Relays channel messages from client to server
	 * @param channelName the channel name
	 * @param msg the message to be sent
	 */
	
	public void channelMsg(String channelName, String msg) {
		if(isConnected) {
			String message = buildSay(session.getNick(), msg);
			sIRC.tabContainer.message(message, channelName, TabComponent.CHANNEL, TabComponent.CHANNEL);
			session.getChannel(channelName).say(msg);
		}
	}
	
	/**
	 * Relays action messages from client to server
	 * @param identifier the channel or user 
	 * @param msg the action message 
	 * @param type channel or user
	 */
	public void actionMsg(String identifier, String msg, int type) {
		if(isConnected && (type == TabComponent.PM || type == TabComponent.CHANNEL)) {
			String printMsg = buildSay("* " + session.getNick(), msg);
			session.action(identifier, "ACTION " + msg);	
			sIRC.tabContainer.message(printMsg, identifier, type, TabComponent.INFO);
		}
	}
		
	/**
	 * Relays private message from client to the given user
	 * @param toNick the user to send the message to
	 * @param msg the message 
	 */
	public void privMsg(String toNick, String msg) {
		if(isConnected) {
			String message = buildSay(session.getNick(), msg);
			sIRC.tabContainer.message(message, toNick, TabComponent.PM, TabComponent.PM);
			session.sayPrivate(toNick, msg);
		}
	}
	
	/**
	 * Requests connection to a server 
	 * @param hostName hostname of the server
	 */
	public void newServer(String hostName) {
		manager.requestConnection(hostName);
	}
	
	/** 
	 * Connects to the given channel
	 * @param channelName the channel name
	 */
	public void joinChannel(String channelName) {
		if(isConnected) {
			if(session.isChannelToken(channelName))
				session.join(channelName);
			else
				sIRC.tabContainer.consoleMsg(sIRC.i18n.getStr("error.invalidChannelName") + " " + channelName);
		} else {
			sIRC.tabContainer.consoleMsg(sIRC.i18n.getStr("error.joinDisconnected"));
		}
	}
	
	/**
	 * Closes either a channel tab or pm window
	 * @param identifier the name of the tab
	 * @param type channel or user 
	 * @param partMsg the message
	 */
	public void closeChat(String identifier, int type, String partMsg) {
		if(type == TabComponent.CHANNEL) {
			session.getChannel(identifier).part(partMsg);
			sIRC.tabContainer.closeTab(identifier);
		}
		else if(type == TabComponent.PM) {
			sIRC.tabContainer.closeTab(identifier);
		}
	}
	
	/**
	 * Concatenates time, nick and msg for output in channel or pm
	 * @param nick the users nick
	 * @param msg the message
	 * @return the prefix string for a user who says something
	 */
	private String buildSay(String nick, String msg) {
		Date timeStamp = new Date();
		return timeFormat.format(timeStamp) + "  <" + nick + ">  " + msg;
	}
	
	/**
	 * Concatenates time and -!- for information messages to channel or pm
	 * @return String the concatenated string
	 */
	private String buildInfoPrefix() {
		Date date = new Date();
		return timeFormat.format(date) + " -!- ";
	}
	
	/**
	 * Fetches the users in a channel
	 * @param channelName
	 * @return List<String> the list with users
	 */
	public List<String> getUsers(String channelName) {
		return session.getChannel(channelName).getNicks();
	}
	
	/**
	 * Change of client-side nick. Informs the server if connected
	 * @param newNick our new nick
	 */
	public void changeNick(String newNick) {
		if(isConnected) 
			session.changeNick(newNick);
		
		sIRC.options.setNick(newNick);
		sIRC.tabContainer.consoleMsg(sIRC.i18n.getStr("connectionManagement.changedNick") + " " + newNick);		
	}
	
	/**
	 * Fetches list of users with a given mode set
	 * @param channelName the channel
	 * @param action the action of the mode (+ or -)
	 * @param mode the give mode
	 * @return List<String> the list containing the users
	 */
	public List<String> getUsersMode(String channelName, Action action, char mode) {
		return session.getChannel(channelName).getNicksForMode(action, mode);
	}
	
	/**
	 * Fetches all channels with a given user 
	 * @param session the current session
	 * @param nick the nick to check
	 * @return List<String> the list containing the channels
	 */
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
	
	/**
	 * Fetches a given channel
	 * @param identifier the channel name
	 * @return Channel the channel object returned
	 */
	public Channel getChannel(String identifier) {
		return session.getChannel(identifier);
	}
	
	/**
	 * Fetch the current session
	 * @return session current session
	 */	
	public Session getSession() {
		return session;
	}

	/**
	 * Getter for our connection state
	 * 
	 * @return are we connected to server or not
	 */
	public boolean isConnected() {
		return isConnected;
	}
}
