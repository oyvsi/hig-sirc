package no.hig.sss.sirc;

import java.util.HashMap;
import java.util.Map;



public class ChatManager {
	final Map<String, Chat> chatMap = new HashMap<String, Chat>();
	private Chat chat;
	private ConnectionManagement connectionManagement;

	
	public ChatManager(ConnectionManagement connectionManagement) {
		chat = null;
		this.connectionManagement = connectionManagement;
	}
	

	
	public void createChat(String channel, String chatType, String nick) {
		chat = new Chat(this, channel, chatType, nick);
		chatMap.put(channel, chat);
		
	}

	public void relayClientPrivateMessage(String nick, String msg) {
		connectionManagement.privMsg(nick, msg);
		
	}
	
	public void relayClientChannelMessage(String channel, String msg) {
		connectionManagement.channelMsg(channel, msg);
		
	}
	
	public void relayRemoteChannelMessage(String nick, String msg, String channel) {
		chatMap.get(channel).insertText(nick, msg);
	}
	
	public void relayRemotePrivateMessage(String nick, String msg) {
		chatMap.get(nick).insertText(nick, msg);
	}
	
	public Map<String, Chat> getMap() {
		return chatMap;
	}

}