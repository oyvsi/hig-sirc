package no.hig.sss.sirc;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 
 * Give out strings in language-file
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
	 * Returns the text string from specifies id
	 * 
	 * @param id the name of the string
	 * @return string the string from the language file
	 */
	public String getStr(String id) {
		return messages.getString(id);
	}
}
