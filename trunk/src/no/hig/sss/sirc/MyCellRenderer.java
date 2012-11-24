package no.hig.sss.sirc;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

class MyCellRenderer extends JLabel implements ListCellRenderer<String> {
	UsersContainer userContainer;
	
	public MyCellRenderer(UsersContainer userContainer) {
		this.userContainer = userContainer;
	}
	
	
	
	public Component getListCellRendererComponent(JList list, String value,
			int index, boolean isSelected, boolean cellHasFocus) {
		
		if(userContainer.getOpList().contains(value)) {
			setText('@' + value.toString());
			return this;
		}
		
		if(userContainer.getVoiceList().contains(value)) {
			setText('+' + value.toString());
			return this;
		}
		
		setText(value.toString());
		
		return this;
	}

	
}

