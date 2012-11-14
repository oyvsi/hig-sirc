/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
 
package no.hig.sss.sirc;
 
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
 
//SplitPaneDemo itself is not a visible component.
public class Options extends JPanel
                          implements ListSelectionListener {
    private JLabel picture;
    private JList list;
    private JSplitPane splitPane;
    private String[] imageNames = { "Connection", "Text", "Font"};
    public Options() {
 
        //Create the list of images and put it in a scroll pane.
         
        list = new JList(imageNames);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.addListSelectionListener(this);
        
        
        JScrollPane listScrollPane = new JScrollPane(list);
        picture = new JLabel();
        picture.setFont(picture.getFont().deriveFont(Font.ITALIC));
        picture.setHorizontalAlignment(JLabel.CENTER);
        
        JScrollPane pictureScrollPane = new JScrollPane(picture);
        ConnectionOptions.setMessages (ResourceBundle.getBundle ("i18n/I18N"));
        JInternalFrame co = new ConnectionOptions(); 
        
        //Create a split pane with the two scroll panes in it.
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                   listScrollPane, co);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(100);
 
        
        //Provide minimum sizes for the two components in the split pane.
        Dimension minimumSize = new Dimension(100, 30);
        listScrollPane.setMinimumSize(minimumSize);
        pictureScrollPane.setMinimumSize(minimumSize);
        minimumSize = new Dimension(500, 300);
        co.setMinimumSize(minimumSize);
        //Provide a preferred size for the split pane.
        
        splitPane.setPreferredSize(new Dimension(600, 300));
        splitPane.setMinimumSize(new Dimension(600, 300));
    }
     
    //Listens to the list
    public void valueChanged(ListSelectionEvent e) {
        JList list = (JList)e.getSource();
        switch(list.getSelectedIndex()) {
        case 0:
        	System.out.println("Connection");
        	setViewConnectionOptions();
        	break;
        case 1:
        	System.out.println("Text");
        	setViewColorOptions();
        	break;
        	
        case 2:
        	System.out.println("Font Options");
        	setViewFontOptions();
        	break;
        }
        //updateLabel(imageNames[list.getSelectedIndex()]);
    }
    void setViewConnectionOptions() {
    	JInternalFrame co = new ConnectionOptions(); 
    	splitPane.setRightComponent(co);
    }
    void setViewColorOptions() {
    	JInternalFrame to = new TextOptions();  
		TextOptions test = new TextOptions();
		to.setPreferredSize(new Dimension(800, 600));
		to.pack();
		to.setVisible(true);
    	
    	splitPane.setRightComponent(to);
    	
    }
    void setViewFontOptions() {
    	
    }

    //Used by SplitPaneDemo2
    public JList getImageList() {
        return list;
    }
 
    public JSplitPane getSplitPane() {
        return splitPane;
    }
 
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
 
        //Create and set up the window.
        JFrame frame = new JFrame("SplitPaneDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Options splitPaneDemo = new Options();
        frame.getContentPane().add(splitPaneDemo.getSplitPane());
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
 
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
 
       
}