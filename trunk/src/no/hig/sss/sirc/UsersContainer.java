package no.hig.sss.sirc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractListModel;

import jerklib.events.modes.ModeAdjustment.Action;

public class UsersContainer extends AbstractListModel {
	List<String> usersForView = new ArrayList<String>();
	List<String> op;
	List<String> voice;
	ConnectionManagement cm = sIRC.conManagement;
	
	public UsersContainer(String identifier) {
		usersForView = new ArrayList<String>();
		usersForView = cm.getUsers(identifier);
		op = cm.getUsersMode(identifier, Action.PLUS, 'o');
		voice = cm.getUsersMode(identifier, Action.PLUS, 'v');
	}
	
	public void addUser(Character mode, String nick) {
		usersForView.add(nick);
	}
	
	public Object getElementAt(int arg0) {
		String user = usersForView.get(arg0);
		if(op.contains(user)) return "@" + user;
		if(voice.contains(user)) return "+" + user;
		return user;
	}

	public int getSize() {
		return usersForView.size();
	}
	
	public List<String> sortList(List<String> users) {
		Iterator<String> user = users.iterator();
		while(user.hasNext()) {
			
		}
		return users;
	
		}
	}
