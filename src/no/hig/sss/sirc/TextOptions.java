package no.hig.sss.sirc;

import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextPane;
import javax.swing.SpinnerListModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class TextOptions extends JPanel {
	private String fontName;
	private Boolean italic;
	private Boolean bold;
	private Color color;
	private Integer fontSize;
	private StyledDocument preview;
		
	public TextOptions() {
		fontName = "Serif";
		italic = bold = false;
		fontSize = 12;
		color = new Color(0, 0, 0);	// Black
		Integer[] fontSizeValues = {8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 36, 48, 72};

		JPanel mainPanel = new JPanel(new BorderLayout(10, 15));		
		JPanel previewPanel = new JPanel();
		previewPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED), "Preview"));
		JPanel settingsPanel = new JPanel();				
	    preview = new DefaultStyledDocument();

		JTextPane text = new JTextPane(preview);
		text.setEditable(false);
		text.setOpaque(false);	// Set transparent
		
		final JComboBox<String> selFontName = new JComboBox<String>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
		selFontName.setSelectedItem(fontName);
		JLabel fontLabel = new JLabel("Font");	
		final JCheckBox selBold = new JCheckBox("Bold");
		final JCheckBox selItalic = new JCheckBox("Italic");
		final JSpinner selFontSize = new JSpinner(new SpinnerListModel(fontSizeValues));
		selFontSize.setValue(fontSize);

		final ColorSelector selColor = new ColorSelector();	
		JLabel selColorLabel = new JLabel("Color");

		previewPanel.add(text);
		settingsPanel.add(fontLabel);
		settingsPanel.add(selFontName);
		settingsPanel.add(selBold);
		settingsPanel.add(selItalic);
		settingsPanel.add(selFontSize);
		settingsPanel.add(selColor);
		settingsPanel.add(selColorLabel);
		
		updatePreview();

		mainPanel.add(previewPanel, BorderLayout.SOUTH);
		mainPanel.add(settingsPanel, BorderLayout.WEST);
		add(mainPanel);
		
		/* Listeneres */
		
		selBold.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				bold = selBold.isSelected();
				updatePreview();
			}
		});
		selItalic.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				italic = selItalic.isSelected();
				updatePreview();
			}
		});
		
		selFontSize.addChangeListener(new ChangeListener() {			
			public void stateChanged(ChangeEvent e) {
				fontSize = (Integer) selFontSize.getValue();
				updatePreview();
			}
		});
		
		selFontName.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					fontName = selFontName.getSelectedItem().toString();
					updatePreview();
				}
			}
		});
		
		// Add custom action to ColorSelector?
		selColor.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {}
			
			@Override
			public void focusGained(FocusEvent e) {
				color = selColor.getColor();
				updatePreview();			
			}
		});

	}
	
	private void updatePreview() {
		try {
			preview.remove(0, preview.getLength());
			preview.insertString(0, fontName, format());
			//preview.insertString(offset, str, a)
		} catch (BadLocationException e) {
			e.printStackTrace();
		}	
	}
	
	public SimpleAttributeSet format() {
		SimpleAttributeSet sas = new SimpleAttributeSet();
		StyleConstants.setFontFamily(sas, fontName);
		StyleConstants.setBold(sas, bold);
		StyleConstants.setItalic(sas, italic);
		StyleConstants.setFontSize(sas, fontSize);
		StyleConstants.setForeground(sas, color);
		
		return sas;
	}	
}