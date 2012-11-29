package no.hig.sss.sirc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractListModel;

import jerklib.events.modes.ModeAdjustment.Action;


/**
 * UserContainer holds all the users, and takes care of
 * adding, removing and mode updates
 * @author Oyvind Sigerstad, Nils Slaaen, Bjorn-Erik Strand
 *
 */
public class UserModel extends AbstractListModel {
	private static final long serialVersionUID = 1L;

	private ArrayList<String> usersForView;
	private ConnectionManagement cm = sIRC.conManagement;
	private String identifier;
	boolean opExist = false;
	boolean voiceExist = false;
	boolean regularsExist = false;
	
	/**
	 * Constructor for UserContainer. Creates the lists used for tracking
	 * users with voice and op, aswell as regular users
	 * @param identifier
	 */
	public UserModel(String identifier) {
		this.identifier = identifier;
		usersForView = new ArrayList<String>();
		importList();
		updateView();
	}

	/**
	 * Adds a user and paint the list
	 * @param nick - Nick to be added
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
		boolean found = false;
		if(usersForView.contains("@" + nick))
				found = true;
		if(usersForView.contains("+" + nick))
				found = true;
		if(usersForView.contains("" + nick))
				found = true;
		return found;
	}
	
	/**
	 * Imports the user list from the channel and appends the prefix
	 * for op or voice
	 */
	public void importList() {
		Iterator<String> userIter = cm.getUsers(identifier).iterator();
		while(userIter.hasNext()) {
			String user = userIter.next();
			// OP user
			if(cm.getUsersMode(identifier, Action.PLUS, 'o').contains(user)) {
				usersForView.add('@'+user);
				continue;
			} 
			// Voiced user
			else if (cm.getUsersMode(identifier, Action.PLUS, 'v').contains(user)) {
				usersForView.add('+'+user);
				continue;
			} 
			// Regular user
			else {
				usersForView.add(user);
			}
		}
		
		sortList(usersForView);
		updateView();
	}

	/**
	 * Removes a user, sorts the new list and repaints the list
	 * @param nick the nick of the user to remove
	 */
	public void removeUser(String nick) {		
		usersForView.remove(nick);
		sortList(usersForView);
		updateView();
	}
	
	/**
	 * Fetch a user from the list
	 * @param index 
	 * 
	 */
	public Object getElementAt(int index) {
		return usersForView.get(index);
	}
	/**
	 * Get the size of usersForView used by JList
	 */
	public int getSize() {
		return usersForView.size();
	}
	
	/**
	 * Sorts the list based on the UserComparator
	 * @param list - List to be sorted
	 */
	public void sortList(List<String> list) {
		Collections.sort(list, new UserComparator());
	}

	/**
	 * Clears the list, imports and sorts when a mode has changed
	 */
	public void modeChange() {
		 usersForView.clear();
		 importList();
	}
	
	/**
	 * Changes a users nick, sorts and repaints the list
	 * @param oldNick
	 * @param newNick
	 */
	public void nickChange(String oldNick, String newNick) {
		// User is OP
		if(usersForView.contains('@'+oldNick)) {
			usersForView.remove('@'+oldNick);
			usersForView.add('@'+newNick);
		// User is voice
		} else if (usersForView.contains('+'+oldNick)) {
			usersForView.remove('+'+oldNick);
			usersForView.add('+'+newNick);
		// User is regular
		} else {
			usersForView.remove(oldNick);
			usersForView.add(newNick);
		}
		
		sortList(usersForView);
		updateView();
}

	/**
	 * Notifies the JList that contents has changed for repaint
	 */
	public void updateView() {
		fireContentsChanged(this, 0, getSize());
	}

	/**
	 * Comparator class used to sort users with precedence on prefix: @ > + > ""  
	 * Default is + > @ > ""
	 * @author Oyvind Sigerstad, Nils Slaaen, Bjorn-Erik Strand
	 *
	 */
	class UserComparator implements Comparator<String> {
		public int compare (String s1, String s2) {
		
		if  (s1.startsWith("@") && !s2.startsWith("@")) return -1; // Place s1 before s2
		else if  (!s1.startsWith("@") && s2.startsWith("@")) return 1;  // Place s2 before s1
		else return s1.compareToIgnoreCase(s2); // Regular sort
		}
	}
}