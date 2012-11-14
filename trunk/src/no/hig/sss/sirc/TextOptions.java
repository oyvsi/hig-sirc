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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SpinnerListModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class TextOptions extends JPanel {
	private JComboBox<String> fontName;
	private JCheckBox bold, italic;
	private JSpinner size;
	private StyledDocument preview;
	private Integer[] sizeValues = {8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 36, 48, 72};
	private ColorSelector color;
		
	public TextOptions() {
		fontName = new JComboBox<String>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
		fontName.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED)
					updatePreview();
			}
		});
		
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout(10, 15));
		
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
		size = new JSpinner(new SpinnerListModel(sizeValues));
		size.setValue(12);
		size.addChangeListener(new ChangeListener() {			
			public void stateChanged(ChangeEvent e) {
				updatePreview();
			}
		});
		color = new ColorSelector();
		
		// Add custom action to ColorSelector?
		color.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {}
			
			@Override
			public void focusGained(FocusEvent e) {
				updatePreview();			
			}
		});
		
		JLabel colorLabel = new JLabel("Color");

		previewPanel.add(text);
		settingsPanel.add(fontLabel);
		settingsPanel.add(fontName);
		settingsPanel.add(bold);
		settingsPanel.add(italic);
		settingsPanel.add(size);
		settingsPanel.add(color);
		settingsPanel.add(colorLabel);
		
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
		StyleConstants.setForeground(sas, color.getColor());
		
		return sas;
	}

	public static void main(String args[]) {
		TextOptions test = new TextOptions();
		test.setPreferredSize(new Dimension(800, 600));
		//test.pack();
		test.setVisible(true);
		
	}	
}