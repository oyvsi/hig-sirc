package no.hig.sss.sirc;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18n {
	private static ResourceBundle messages;
	private Locale currentLocale;
	
	public I18n() {
		currentLocale = Locale.getDefault();
		messages = ResourceBundle.getBundle("i18n/I18N", currentLocale);
	}
	
	public String getStr(String id) {
		return messages.getString(id);
	}
}
