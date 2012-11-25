package no.hig.sss.sirc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;

import jerklib.events.modes.ModeAdjustment.Action;

public class UsersContainer extends AbstractListModel {
	List<String> op;
	List<String> voice;
	List<String> regulars;
	Map<String, String> users = new HashMap<String, String>();
	ArrayList<String> usersForView;
	ConnectionManagement cm = sIRC.conManagement;
	String channel;
	boolean opExist = false;
	boolean voiceExist = false;
	boolean regularsExist = false;
	
	public UsersContainer(String identifier) {
		channel = identifier;
		List<String> tmpusers =  new ArrayList<String>(cm.getUsers(identifier));
		usersForView = new ArrayList<String>();
		System.out.println(tmpusers);
			
		
			if(cm.getUsersMode(identifier, Action.PLUS, 'o').iterator().hasNext())  {
				opExist = true;
				Iterator<String> opUser = cm.getUsersMode(identifier, Action.PLUS, 'o').iterator();
				op = cm.getUsersMode(identifier, Action.PLUS, 'o');
				while(opUser.hasNext()) {
					String user = opUser.next();
					if(tmpusers.contains(user)) {
						tmpusers.remove(user);
					}
				}
				Collections.sort(op, String.CASE_INSENSITIVE_ORDER);
				usersForView.addAll(op);
			}
				
			if(cm.getUsersMode(identifier, Action.PLUS, 'v').iterator().hasNext()) {
				voiceExist = true;
				Iterator<String> voiceUser = cm.getUsersMode(identifier, Action.PLUS, 'v').iterator();
				voice = cm.getUsersMode(identifier, Action.PLUS, 'v');
				while(voiceUser.hasNext()) {
					String user = voiceUser.next();
					if(tmpusers.contains(user)) {
						tmpusers.remove(user);
					}
				}
				Collections.sort(voice, String.CASE_INSENSITIVE_ORDER);
				usersForView.addAll(voice);
			}
			
			regulars = new ArrayList<String>(tmpusers);
			regularsExist = true;
			Collections.sort(regulars, String.CASE_INSENSITIVE_ORDER);
			usersForView.addAll(regulars);
	}
	

	
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
	
	public boolean userInChannel(String nick) {
		return usersForView.contains(nick);
	}
	
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

	public void voiceMode(String nick, Action action) {
		System.out.println(nick);
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
	

