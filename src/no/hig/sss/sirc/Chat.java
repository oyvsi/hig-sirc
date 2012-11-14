package no.hig.sss.sirc;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

import jerklib.Channel;
import jerklib.ConnectionManager;


public class Chat {
	private ChatManager manager;
	private JFrame channelWindow;
	private JPanel chatBoxPanel = new JPanel();
	private JTextField chatBox = new JTextField(20);
	private JTextArea channelChat = new JTextArea(20,20);
	private JTabbedPane jtp = new JTabbedPane();


	public Chat(ChatManager wm, final String chan, final String chatType, final String nick) {
		channelWindow = new JFrame(chan.toString());
		manager = wm;
		channelChat.setEditable(false);
		chatBoxPanel.add(channelChat);
		chatBoxPanel.add(chatBox);
		channelWindow.add(jtp);
		channelWindow.add(chatBoxPanel);
		chatBox.addKeyListener(new KeyAdapter() {
           public void keyReleased(KeyEvent e) {
               if(e.getKeyCode() == KeyEvent.VK_ENTER ) {
        	   JTextField textField = (JTextField) e.getSource();
                String text = textField.getText();
                textField.setText("");
                channelChat.append('\n' + '<' + nick + ">: " + text);
                if(chatType == "PRIVATE") {
                	manager.relayClientPrivateMessage(chan, text);
                } else {
                	manager.relayClientChannelMessage(chan, text);
                }
                
                                
               }
           }
		});
		
		
		channelWindow.setPreferredSize(new Dimension(400,400));
		channelWindow.setVisible(true);
		channelWindow.pack();
	}

	public void insertText(String nick, String txt) {
		channelChat.append('<' + nick + ">: " + txt + '\n');
	}

}
