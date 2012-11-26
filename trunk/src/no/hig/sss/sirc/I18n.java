package no.hig.sss.sirc;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 
 * Class to give out strings in language-file
 * 
 * @author Oyvind Sigerstad, Nils Slaaen, Bjorn-Erik Strand
 *
 */
public class I18n {
	private static ResourceBundle messages;
	private Locale currentLocale;
	
	/**
	 * Constructor. Sets locale
	 */
	public I18n() {
		currentLocale = Locale.getDefault();
		messages = ResourceBundle.getBundle("i18n/I18N", currentLocale);
	}
	
	/**
	 * Fetches the text string from specifies id
	 * 
	 * @param id - The name of the string
	 * @return string - The string from the language file
	 */
	public String getStr(String id) {
		return messages.getString(id);
	}
}
