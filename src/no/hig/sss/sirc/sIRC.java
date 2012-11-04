package no.hig.sss.sirc;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;


public class sIRC extends JFrame {
	private static ResourceBundle messages;
	private static Locale currentLocale;
	JDesktopPane jdp;
	
	public sIRC() {
		
		jdp = new JDesktopPane();
		GUI gui = new GUI(messages);
		
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                  screenSize.width  - inset*2,
                  screenSize.height - inset*2);
		createOptions();
		setContentPane(jdp);
		setJMenuBar(gui.menu());
		
		jdp.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		
	}
	
	public void createOptions() {
		JInternalFrame frame = new JInternalFrame("Channel",true,true,true,true);

		
		frame.setSize(300,300);
		 
        //Set the window's location.
        frame.setLocation(0, 0);
        frame.setVisible(true); //necessary as of 1.3
        jdp.add(frame);
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		currentLocale = Locale.getDefault();
		messages = ResourceBundle.getBundle("i18n/I18N", currentLocale);
		
		sIRC irc = new sIRC();
		//irc.pack();
		irc.setVisible(true);
		irc.setDefaultCloseOperation(EXIT_ON_CLOSE);

	}
}
