package no.hig.sss.sirc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import jerklib.events.modes.ModeAdjustment.Action;

public class UsersContainer {
	List<String> users;
	List<String> op;
	List<String> voice;
	List<String> regular;
	Vector<String> usersForView;
	ConnectionManagement cm = sIRC.conManagement;
	
	public UsersContainer(String identifier) {
		op = new ArrayList<String>();
		voice = new ArrayList<String>();
		users = new ArrayList<String>(cm.getUsers(identifier));
		usersForView = new Vector<String>();
		System.out.println(users);
		List<String> tmpusers = new ArrayList<String>(users);
		System.out.println(tmpusers);
			
		
			if(cm.getUsersMode(identifier, Action.PLUS, 'o').iterator().hasNext())  {
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
			System.out.println(tmpusers);
			regular = new ArrayList<String>(tmpusers);
			System.out.println(regular);
			Collections.sort(regular, String.CASE_INSENSITIVE_ORDER);
			usersForView.addAll(regular);
		}
	

	
	public void addUser(Character mode, String nick) {
		users.add(nick);
	}
	
	public Vector<String> getList() {
		return usersForView;
	}
	
	public List<String> getOpList() {
		return op;
		
	}
	
	public List<String> getVoiceList() {
		return voice;
	}
}
