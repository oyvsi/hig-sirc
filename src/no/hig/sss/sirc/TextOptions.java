package no.hig.sss.sirc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class TextOptions extends JFrame {
	private JComboBox<String> fontName;
	private JCheckBox bold, italic;
	private JSpinner size;
	private StyledDocument preview;
	
	public TextOptions() {
		fontName = new JComboBox<String>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
		fontName.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED)
					updatePreview();
			}
		});
		
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		
		JPanel previewPanel = new JPanel();
		previewPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED), "Preview"));

		JPanel settingsPanel = new JPanel();
				
	    preview = new DefaultStyledDocument();

		JTextPane text = new JTextPane(preview);
		text.setEditable(false);
		text.setOpaque(false);
		
		JLabel fontLabel = new JLabel("Font");
		
		ItemListener update = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				updatePreview();
			}
		};
		
		bold = new JCheckBox("Bold");
		bold.addItemListener(update);
		italic = new JCheckBox("Italic");
		italic.addItemListener(update);
		size = new JSpinner();
		size.setValue(12);
		size.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				updatePreview();
			}
		});

		previewPanel.add(text);
		settingsPanel.add(fontLabel);
		settingsPanel.add(fontName);
		settingsPanel.add(bold);
		settingsPanel.add(italic);
		settingsPanel.add(size);
		
		updatePreview();

		mainPanel.add(previewPanel, BorderLayout.NORTH);
		mainPanel.add(settingsPanel, BorderLayout.WEST);
		add(mainPanel);

	}
	
	private void updatePreview() {
	    String name = fontName.getSelectedItem().toString();
		try {
			preview.remove(0, preview.getLength());
			preview.insertString(0, name, format(name));
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
	}
	
	private SimpleAttributeSet format(String fontName) {
		SimpleAttributeSet sas = new SimpleAttributeSet();
		StyleConstants.setFontFamily(sas, fontName);
		StyleConstants.setBold(sas, bold.isSelected());
		StyleConstants.setItalic(sas, italic.isSelected());
		StyleConstants.setFontSize(sas, (int) size.getValue());
		
		return sas;
	}

	public static void main(String args[]) {
		TextOptions test = new TextOptions();
		test.setPreferredSize(new Dimension(800, 600));
		test.pack();
		test.setVisible(true);
		
	}	
}