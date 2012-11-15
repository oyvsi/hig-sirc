package no.hig.sss.sirc;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;

public class UsersContainer extends AbstractListModel {
	List<String> users = new ArrayList<String>();
	
	public UsersContainer(List<String> users) {
		this.users = users;
	}
	
	public void addUser(String nick) {
		users.add(nick);
		
	}
	
	public Object getElementAt(int arg0) {
		return users.get(arg0);
	}

	public int getSize() {
		return users.size();
	}

}
