package no.hig.sss.sirc;

import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

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

import com.apple.jobjc.Coder.SELCoder;
import com.sun.org.apache.xpath.internal.axes.SelfIteratorNoPredicate;

public class TextOptions extends JPanel {
	private String optionName;
	private String fontName;
	private Boolean italic;
	private Boolean bold;
	private Color color;
	private Integer fontSize;
	private StyledDocument preview;
	
	private final JComboBox<String> selFontName;
	private final JCheckBox selBold;
	private final JCheckBox selItalic;
	private final JSpinner selFontSize;
	private final ColorSelector selColor;
	
		
	public TextOptions(String optionName) {
		this.optionName = optionName;
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
		
		selFontName = new JComboBox<String>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
		selFontName.setSelectedItem(fontName);
		JLabel fontLabel = new JLabel("Font");	
		selBold = new JCheckBox("Bold");
		selItalic = new JCheckBox("Italic");
		selFontSize = new JSpinner(new SpinnerListModel(fontSizeValues));
		selFontSize.setValue(fontSize);
		selColor = new ColorSelector();	
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
	
	public Properties save() {
		Properties pro = new Properties();
		String rgbColor = Integer.toString(color.getRed()) + " " + 
						  Integer.toString(color.getGreen()) + " " +
						  Integer.toString(color.getBlue());
		
		pro.setProperty(optionName + "FontName", fontName);
		pro.setProperty(optionName + "Italic", italic.toString());
		pro.setProperty(optionName + "Bold", bold.toString());
		pro.setProperty(optionName + "FontSize", fontSize.toString());
		pro.setProperty(optionName + "RGBColor", rgbColor);
		
		return pro;
			
	}
	
	public void load(Properties pro) {
		try {
			fontName = pro.getProperty(optionName + "FontName");
			italic = pro.getProperty(optionName + "Italic").equals("true");
			bold = pro.getProperty(optionName + "Bold").equals("true");
			
			String fontSizeRead = pro.getProperty(optionName + "FontSize");
			fontSize = Integer.parseInt(fontSizeRead);
			
			String rgbColor = pro.getProperty(optionName + "RGBColor");
			String[] rgb = rgbColor.split(" ");
			int red = Integer.parseInt(rgb[0]);
			int green = Integer.parseInt(rgb[1]);
			int blue = Integer.parseInt(rgb[2]);
			color = new Color(red, green, blue);
			
			selItalic.setSelected(italic);
			selBold.setSelected(bold);
			selFontSize.setValue(fontSize);
			selFontName.setSelectedItem(fontName);
			selColor.setColor(color);
			
			
			updatePreview();
		
		} catch (Exception e) {
			System.out.println("Error loading TextOptions");
			e.printStackTrace();
		}
	}
}