package no.hig.sss.sirc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;

import jerklib.events.modes.ModeAdjustment.Action;


/**
 * UserContainer holds all the users, and takes care of
 * adding, removing and mode updates on users
 * 
 *
 */
public class UserModel extends DefaultListModel {
	private List<String> op;
	private List<String> voice;
	private List<String> regulars;
	private ArrayList<String> usersForView;
	private ConnectionManagement cm = sIRC.conManagement;
	private String channel;
	boolean opExist = false;
	boolean voiceExist = false;
	boolean regularsExist = false;
	
	/**
	 * Constructor for UserContainer. Creates the lists used for tracking
	 * users with voice and op, aswell as regular users
	 * @param identifier
	 */
	public UserModel(String identifier) {
		channel = identifier;
		List<String> tmpusers =  new ArrayList<String>(cm.getUsers(identifier));
		usersForView = new ArrayList<String>();
			
		if(cm.getUsersMode(identifier, Action.PLUS, 'o').iterator().hasNext()) {
			opExist = true;
			op = createList(tmpusers, 'o');
		}
		
		if(cm.getUsersMode(identifier, Action.PLUS, 'v').iterator().hasNext()) {
			voiceExist = true;
			voice = createList(tmpusers, 'v');
		}
		
		regulars = new ArrayList<String>(tmpusers);
		regularsExist = true;
		
		updateView();
	}
	

	/**
	 * Adds a user to the appropriate list based on the users mode
	 * @param nick
	 */
	public void addUser(String nick) {
		if(cm.getUsersMode(channel, Action.PLUS, 'o').contains(nick)) {
			if(!opExist) {
				op = new ArrayList<String>();
				opExist = true;
			}
			op.add(nick);
		} else if  (cm.getUsersMode(channel, Action.PLUS, 'v').contains(nick)) {
			if(!voiceExist) {
				voice = new ArrayList<String>();
				voiceExist = true;
			}
			voice.add(nick);
		} else {
			if(!regularsExist) {
				regulars = new ArrayList<String>();
				regularsExist = true;
			}
			regulars.add(nick);
			}
		
		updateView();
	}
	
	/**
	 * Checks if the user is in a given channel
	 * @param nick Nick to be checked
	 * @return usersForView.contains(nick) True or false
	 */
	public boolean userInChannel(String nick) {
		return usersForView.contains(nick);
	}
	
	public List<String> createList(List<String> tmpusers, char mode) {
		List<String> userList = new ArrayList<String>();
		Iterator<String> userIter = cm.getUsersMode(channel, Action.PLUS, mode).iterator();
		userList = cm.getUsersMode(channel, Action.PLUS, mode);
		while(userIter.hasNext()) {
			String user = userIter.next();
			if(tmpusers.contains(user)) {
				tmpusers.remove(user);
				}
			}
		return userList;
	}

	/**
	 * Removes a user 
	 * @param nick the nick of the user to remove
	 */
	
	public void removeUser(String nick) {		
		
		if(opExist && op.contains(nick)) {
			op.remove(nick);
		} else if(voiceExist && voice.contains(nick)) {
			voice.remove(nick);
		} else {
			regulars.remove(nick);
		}
		
		updateView();
	}
	
	/**
	 * Fetch a user from the list and append prefix
	 * @param index 
	 * 
	 */
	public Object getElementAt(int index) {
		String user = usersForView.get(index);
		if(opExist && op.contains(user)) return '@' + user;
		if(voiceExist && voice.contains(user)) return '+' + user;
		return user;
		}
	
	public int getSize() {
		return usersForView.size();
	}
	
	
	public ArrayList<String> getList() {
		return usersForView;
	}
	
	public List<String> getOpList() {
		return op;
		
	}
	
	public List<String> getVoiceList() {
		return voice;
	}
	
	public void sortList(List<String> list) {
		Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
			
	}

	/**
	 * Modifies the 'o' mode for a user, either op or deop
	 * @param nick - The nick of the user
	 * @param action - Either + or -
	 */
	
	public void opMode(String nick, Action action) {
		if(!opExist) {
			op = new ArrayList<String>();
			opExist = true;
		} 
		
		if(action == Action.PLUS) {
			if(voiceExist && voice.contains(nick)) {
				voice.remove(nick);
				op.add(nick);
				if(voice.size() == 0) voiceExist = false;
			} else {
				regulars.remove(nick);
				if(regulars.size() == 0) regularsExist = false;
				op.add(nick);
			}
		}
		
		if(action == Action.MINUS) {
			op.remove(nick);
			regulars.add(nick);
			regularsExist = true;
		}
		
		updateView();
		
	}

	/**
	 * Modifies the mode for a user, either voice or devoice
	 * @param nick The nick which is given the mode
	 * @param action Either + or -
	 */
	public void voiceMode(String nick, Action action) {
		if(!voiceExist) {
			voice = new ArrayList<String>();
			voiceExist = true;
		} 
		if(action == Action.PLUS) {
			if(opExist && op.contains(nick)) {	
				op.remove(nick);
				voice.add(nick);
			if(op.size() == 0) opExist = false;
				
			} else {
				regulars.remove(nick);
				voice.add(nick);
				if(regulars.size() == 0) regularsExist = false;
			} 
		}
		
		if(action == Action.MINUS) {
			voice.remove(nick);
			regulars.add(nick);
			regularsExist = true;
		}
		
		updateView();
		
	}
	
	/**
	 * Changes a users nick 
	 * @param oldNick
	 * @param newNick
	 */
	
	public void nickChange(String oldNick, String newNick) {
		if(opExist && op.contains(oldNick)) {
			op.remove(oldNick);
			op.add(newNick);
		} else if (voiceExist && voice.contains(oldNick)) {
			op.remove(oldNick);
			op.add(newNick);
		} else {
			regulars.remove(oldNick);
			regulars.add(newNick);
		}
		updateView();
}

	/**
	 * Sorts a modified list and adds it to the list (usersForView) used
	 * by JList
	 */
	public void updateView() {
		usersForView.clear();
		if(opExist) { 
			sortList(op);
			usersForView.addAll(op);
		
		}
		if(voiceExist) {
			sortList(voice);
			usersForView.addAll(voice);
		}
		if(regularsExist) {
			sortList(regulars);
			usersForView.addAll(regulars);
		}
		
		fireContentsChanged(this, 0, getSize());
	}

}