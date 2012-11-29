package no.hig.sss.sirc;

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * This class makes JPanel for viewing html help files
 * 
 * @author Oyvind Sigerstad, Nils Slaaen, Bjorn-Erik Strand
 * 
 */
public class SIRCHelp extends JPanel {
	private static final long serialVersionUID = 1L;
 
	/**
	 * Inserts editor pane and JLabel into Panel
	 * 
	 * @param url path to the html file to view
	 */
    public SIRCHelp(String url) {
        setLayout(new BorderLayout());
        
        // Create an editor pane.
        JEditorPane editorPane = createEditorPane(url);
        JScrollPane editorScrollPane = new JScrollPane(editorPane);
        editorScrollPane.setVerticalScrollBarPolicy(
                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        // Put everything together.
        add(new JLabel("Help"), BorderLayout.NORTH);
        add(editorScrollPane, BorderLayout.CENTER);
    }
 
    /**
     * Tries to read html file from path
     * 
     * @param url path to html file
     * @return editorpane
     */
    private JEditorPane createEditorPane(String url) {
        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable(false);
        URL helpURL;
		
        try {
			helpURL = getClass().getResource(url);
			if (helpURL != null) {
	            try {
	                editorPane.setPage(helpURL);
	            } catch (IOException e) {
	                System.err.println("Attempted to read a bad URL: " + helpURL);
	            }
			}
	        
			return editorPane;  // Returns the editor pane with actual content
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;	// Returns nothing if the url is bad
    }
}