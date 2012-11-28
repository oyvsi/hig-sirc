package no.hig.sss.sirc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
public class UserModel extends AbstractListModel {
	private static final long serialVersionUID = 1L;
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
		usersForView = new ArrayList<String>();
		importList();
		updateView();
	}
	

	/**
	 * Adds a user to the appropriate list based on the users mode
	 * @param nick
	 */
	public void addUser(String nick) {
		usersForView.add(nick);
		updateView();
	}
	
	/**
	 * Checks if the user is in a given channel
	 * @param nick Nick to be checked
	 * @return usersForView.contains(nick) True or false
	 */
	public boolean userInChannel(String nick) {
		return usersForView.contains("@"+nick);
		
	}
	
	public void importList() {
		Iterator<String> userIter = cm.getUsers(channel).iterator();
		while(userIter.hasNext()) {
			String user = userIter.next();
			System.out.println(user);
			if(cm.getUsersMode(channel, Action.PLUS, 'o').contains(user)) {
				usersForView.add('@'+user);
				continue;
			} else if (cm.getUsersMode(channel, Action.PLUS, 'v').contains(user)) {
				usersForView.add('+'+user);
				continue;
			} else {
				usersForView.add(user);
			}
		}
		sortList(usersForView);
	}

	/**
	 * Removes a user 
	 * @param nick the nick of the user to remove
	 */
	
	public void removeUser(String nick) {		
		usersForView.remove(nick);
		updateView();
	}
	
	/**
	 * Fetch a user from the list and append prefix
	 * @param index 
	 * 
	 */
	public Object getElementAt(int index) {
		return usersForView.get(index);
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
		Collections.sort(list, new SubstringComparator());
	}

	/**
	 * Modifies the 'o' mode for a user, either op or deop
	 * @param nick - The nick of the user
	 * @param action - Either + or -
	 */
	
	public void modeChange() {
		 usersForView.clear();
		 importList();
		 updateView();
		
	}

	/**
	 * Modifies the mode for a user, either voice or devoice
	 * @param nick The nick which is given the mode
	 * @param action Either + or -
	 */
	public void voiceMode(String nick, Action action) {
		  // Some channels have autovoice, and then we might be voiced before the mode comes 
		
		if(opExist && !op.contains(nick))
			if(action == Action.PLUS) {
			if(!voiceExist && regulars.contains(nick)) {
				voice = new ArrayList<String>();
				voiceExist = true;
				regulars.remove(nick);
				if(regulars.size() == 0) regularsExist = false;
			} 
			voice.add(nick);
		}
		
		if(action == Action.MINUS) {
			if(regularsExist && !regulars.contains(nick)) {
				voice.remove(nick);
				regulars.add(nick);
			}	
		}
		
		updateView();
		
	}

	
	/**
	 * Changes a users nick 
	 * @param oldNick
	 * @param newNick
	 */
	
	public void nickChange(String oldNick, String newNick) {
		System.out.println(usersForView);
		if(usersForView.contains('@'+oldNick)) {
			usersForView.remove('@'+oldNick);
			usersForView.add('@'+newNick);
			
		} else if (usersForView.contains('+'+oldNick)) {
			usersForView.remove('+'+oldNick);
			usersForView.add('+'+newNick);
		} else {
			usersForView.remove(oldNick);
			usersForView.add(newNick);
		}
		sortList(usersForView);
		updateView();
}

	/**
	 * Sorts a modified list and adds it to the list (usersForView) used
	 * by JList
	 */
	public void updateView() {
		fireContentsChanged(this, 0, getSize());
	}

	class SubstringComparator implements Comparator<String> {
		public int compare (String s1, String s2) {
		
		if  (s1.charAt(0) == '@' && s2.charAt(0) != '@') return -1;
		else if  (s1.charAt(0) != '@' && s2.charAt(0) == '@') return 1;
		else if  (s1.charAt(0) == '+' && s2.charAt(0) != '+') return -1;
		else if  (s1.charAt(0) != '+' && s2.charAt(0) == '+') return 1;
		else return s1.compareToIgnoreCase(s2);
		}
	
}
}