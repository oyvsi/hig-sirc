package no.hig.sss.sirc;

import java.util.Locale;
import java.util.ResourceBundle;


public class sIRC {
	private static ResourceBundle messages;
	private static Locale currentLocale;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		currentLocale = Locale.getDefault();
		messages = ResourceBundle.getBundle("i18n/I18N", currentLocale);
		
		System.out.println(messages.getString("connectionOptions.dialogTitle"));
	}

}
