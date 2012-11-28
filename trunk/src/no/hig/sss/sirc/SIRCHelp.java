package no.hig.sss.sirc;
 
import javax.swing.*;
 
import java.awt.*;              //for layout managers and more
import java.awt.event.*;        //for action events
 
import java.net.MalformedURLException;
import java.net.URL;
import java.io.IOException;
 
public class SIRCHelp extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
 
    public SIRCHelp(String url) {
        setLayout(new BorderLayout());
        //Create an editor pane.
        
        //JEditorPane editorPane = createEditorPane(url);
        JEditorPane editorPane = createEditorPane(url);
        JScrollPane editorScrollPane = new JScrollPane(editorPane);
        editorScrollPane.setVerticalScrollBarPolicy(
                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        //editorScrollPane.setPreferredSize(new Dimension(250, 145));
        //editorScrollPane.setMinimumSize(new Dimension(10, 10));

        //Put everything together.
        add(new JLabel("Help"), BorderLayout.NORTH);
        add(editorScrollPane, BorderLayout.CENTER);
    }
 
 
    public void actionPerformed(ActionEvent e) {
    }
 
    private JEditorPane createEditorPane(String url) {
        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable(false);
        
        java.net.URL helpURL;
		try {
			helpURL = getClass().getResource(url);
			if (helpURL != null) {
	            try {
	                editorPane.setPage(helpURL);
	                System.out.println("HELP: " +helpURL);
	            } catch (IOException e) {
	                System.err.println("Attempted to read a bad URL: " + helpURL);
	            }
			}
	        return editorPane;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
    }
}