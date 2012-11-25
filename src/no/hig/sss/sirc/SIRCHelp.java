package no.hig.sss.sirc;
 
import javax.swing.*;
 
import java.awt.*;              //for layout managers and more
import java.awt.event.*;        //for action events
 
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
 
public class SIRCHelp extends JPanel implements ActionListener {
    
    protected JLabel actionLabel;
 
    public SIRCHelp(String url) {
        setLayout(new BorderLayout());
        //Create an editor pane.
        
        //JEditorPane editorPane = createEditorPane(url);
        JEditorPane editorPane = createEditorPane(url);
        JScrollPane editorScrollPane = new JScrollPane(editorPane);
        editorScrollPane.setVerticalScrollBarPolicy(
                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        editorScrollPane.setPreferredSize(new Dimension(250, 145));
        editorScrollPane.setMinimumSize(new Dimension(10, 10));

        //Put everything together.
        JPanel leftPane = new JPanel(new BorderLayout());
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
			helpURL = new URL(url);
			if (helpURL != null) {
	            try {
	                editorPane.setPage(helpURL);
	            } catch (IOException e) {
	                System.err.println("Attempted to read a bad URL: " + helpURL);
	            }
			}
	        return editorPane;
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
    }
 

 
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("MORDIÆMANN!!!");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Add content to the window.
        frame.add(new SIRCHelp("File:test.html"));
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
 
    public static void main(String[] args) {
        //Schedule a job for the event dispatching thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                 //Turn off metal's use of bold fonts
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        createAndShowGUI();
            }
        });
    }
}