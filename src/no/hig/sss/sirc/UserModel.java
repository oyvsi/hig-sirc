package no.hig.sss.sirc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractListModel;


import jerklib.events.modes.ModeAdjustment.Action;

/**
 * UserContainer holds all the users, and takes care of adding, removing and
 * mode updates on users
 * @author Oyvind Sigerstad, Nils Slaaen, Bjorn-Erik Strand
 * 
 */
public class UserModel extends AbstractListModel {
	private static final long serialVersionUID = 1L;
	private ArrayList<String> op;
	private ArrayList<String> voice;
	private ArrayList<String> regulars;
	private ArrayList<String> usersForView;
	private ConnectionManagement cm = sIRC.conManagement;

	boolean opEdited;
	boolean voiceEdited;
	boolean regularsEdited;

	/**
	 * Constructor for UserContainer. Creates the lists used for tracking users
	 * with voice, op, regulars and the list used for display
	 * 
	 */
	public UserModel() {
		op = new ArrayList<String>();
		voice = new ArrayList<String>();
		regulars = new ArrayList<String>();
		usersForView = new ArrayList<String>();;
	}
	
	/**
	 * Adds a user to the appropriate list based on the users mode
	 * 
	 * @param nick - Nick of user to be added
	 */
	public void addUser(String nick) {
		if (op.contains(nick)) {
			op.add(nick);
			opEdited = true;
		} else if (voice.contains(nick)) {
			voice.add(nick);
			voiceEdited = true;
		} else {
			regulars.add(nick);
			regularsEdited = true;
		}
		
		updateList();
	}

	/**
	 * Checks if the user is in a given channel
	 * 
	 * @param nick Nick to be checked
	 * @return boolean
	 */
	public boolean userInChannel(String nick) {
		return usersForView.contains(nick);
	}
	
	/**
	 * Removes a user from the lists he exists in
	 * 
	 * @param nick the nick of the user to remove
	 */
	public void removeUser(String nick) {
		if (op.contains(nick)) {
			op.remove(nick);
			opEdited = true;
		} else if (voice.contains(nick)) {
			voice.remove(nick);
			voiceEdited = true;
		} else {
			regulars.remove(nick);
			regularsEdited = true;
		}

		updateList();
	}
	/**
	 * Get the element at index with the prefix for given mode on user
	 * User by ListModel
	 * @return user with prefix if mode is set
	 */
	@Override
	public Object getElementAt(int index) {
		String user = usersForView.get(index);
		if (op.contains(user)) return '@' + user;
		if (voice.contains(user)) return '+' + user;
		return user;
	}
	
	/**
	 * Get size of usersForView which is user for display
	 * @return int - The Size
	 */
	public int getSize() {
		return usersForView.size();
	}

	/**
	 * Get the op list
	 * @return op The op list
	 */
	public List<String> getOpList() {
		return op;

	}

	/**
	 * Sorts the a given list with case insensitive order
	 * @param list The list to sort
	 */
	public void sortList(List<String> list) {
		Collections.sort(list, String.CASE_INSENSITIVE_ORDER);

	}

	/**
	 * Modifies the 'o' mode for a user, either op or deop
	 * @param nick The nick of the user
	 * @param action Either + or -
	 */
	public void opMode(String nick, Action action) {
		if (action == Action.PLUS && !op.contains(nick)) { // Op: User is not op 
			if (regulars.contains(nick)) {				   // Remove from regular if he has no privileges
				regulars.remove(nick);
				regularsEdited = true;
			} 
				op.add(nick);							
		
		}
		// Deop: Non-voiced user goes to regulars
		if (action == Action.MINUS) {
			if(!voice.contains(nick) && !regulars.contains(nick)) { 
				regulars.add(nick);
				regularsEdited = true;
			}
			op.remove(nick);
		}
		
		opEdited = true;
		updateList();

	}

	/**
	 * Modifies the mode for a user, either voice or devoice
	 * 
	 * @param nick The nick which is given the mode
	 * @param action Either + or -
	 */
	public void voiceMode(String nick, Action action) {
		// Voice
		if (action == Action.PLUS) {
			// User is either in op or regular
			if (!voice.contains(nick) ) {   
			// If in regular remove, (if it is in OP it still is displayed as OP)
			   if(regulars.contains(nick)) { 
					regulars.remove(nick);
					regularsEdited = true;
				}
			  voice.add(nick);
			} 
		// Devoice
		} else { 	
			// User is no OP and is actually voiced, add to regular 
			if (!op.contains(nick) && voice.contains(nick)) { 
				regulars.add(nick);
				regularsEdited = true;
			}
		voice.remove(nick);	 // Remove devoiced user from voice list
		}

		voiceEdited = true;
		updateList();
	}

	/**
	 * Changes a users nick
	 * 
	 * @param oldNick - From this nick
	 * @param newNick
	 */

	public void nickChange(String oldNick, String newNick) {
		if (op.contains(oldNick)) {
			op.remove(oldNick);
			op.add(newNick);
			opEdited = true;
		} else if (voice.contains(oldNick)) {
			voice.remove(oldNick);
			voice.add(newNick);
			voiceEdited = true;
		} else {
			regulars.remove(oldNick);
			regulars.add(newNick);
			regularsEdited = true;
		}

		updateList();
	}

	/**
	 * Sorts the different lists if there have been changes 
	 * and adds it to the list (usersForView) used 
	 * for display
	 */
	public void updateList() {
		boolean duplicate = false;
		if(!usersForView.isEmpty()) usersForView.clear();
		
		if (!op.isEmpty()) {
			if(opEdited) {	
				sortList(op);
				opEdited = false;
			}
			
			usersForView.addAll(op);
		}
		
			
		if(!voice.isEmpty()) {
			if(voiceEdited) {
				sortList(voice);
				voiceEdited = false;
			}
		
			/* Remove voiced users who are OP */
			List<String> tmpVoice = new ArrayList<String>(voice);
			for (String user : voice) {
				if(op.contains(user)) {
					tmpVoice.remove(user);
					duplicate = true;
				}
			}
		
			/* If any duplicates found, use filtered list*/
			if(duplicate) {
				if(!tmpVoice.isEmpty()) {
					sortList(tmpVoice);
				}
				usersForView.addAll(tmpVoice); 
			} else {
				usersForView.addAll(voice);
			}
			
		}
		
		if(!regulars.isEmpty()) {
			if (regularsEdited) {
				sortList(regulars);
				regularsEdited = false;
			}
			
			usersForView.addAll(regulars);
		}
		
		fireContentsChanged(this, 0, usersForView.size());
	}

	/** 
	 * Sets the different users into the correct lists depending on mode
	 * @param regulars - Non-priviledged users
	 * @param op - OP users
	 * @param voice - Voice users
	 */
	public void injectUsers(List<String> regulars, List<String> op, List<String> voice) {
		this.regulars = (ArrayList<String>) regulars;
		this.op = (ArrayList<String>) op;
		this.voice = (ArrayList<String>) voice;
		opEdited = true;
		voiceEdited = true;
		regularsEdited = true;
		
		updateList();
	}
}