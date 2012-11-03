package no.hig.sss.sirc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Saves and reads personal info stored for ConnectionOptions
 * 
 * @author nilssl, oyvsi, bundy
 * 
 */
public class ConnectionOptionsPrefs {
	private String fullName, email, nickname, altnick; 
	
	File 	filePrefs,  	// Prefs file for ConnectionOptions preferences 
			fileServers; 	// List of known servers
	

	ConnectionOptionsPrefs() {
		filePrefs = new File("connetionoptionsprefs.ini");
		fileServers = new File("servers.ini");
	}

	public void save() {
		// Try to update values
		try {			
			FileOutputStream fos = new FileOutputStream(filePrefs);
			Properties pri = new Properties();
			
			pri.store(fos, "");
			pri.setProperty("fullname", "Oyvsi");
			pri.setProperty("email", "oyvsi@gmail.com");
			pri.setProperty("nickname", "oyvsi");
			pri.setProperty("altnick", "oyvsi_1");
			pri.store(fos, "");
			
			fos.close();
			
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}

	public void load() {
		try {
			FileInputStream fis = new FileInputStream(filePrefs);
			Properties pro = new Properties();
			
			pro.load(fis);
			// Try to display them
			this.setFullName(pro.getProperty("fullname"));
			this.setEmail(pro.getProperty("email"));
			this.setNickname(pro.getProperty("nickname"));
			this.setAltnick(pro.getProperty("altnick"));
			
			fis.close();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	public static void main(String[] args) {
		ConnectionOptionsPrefs cop = new ConnectionOptionsPrefs();
		cop.save();
		cop.load();
		System.out.print(cop.getNickname());
		System.out.print(cop.getEmail());
		System.out.print(cop.getFullName());
		System.out.print(cop.getAltnick());
		
	}

	/**
	 * @return the altnick
	 */
	public String getAltnick() {
		return altnick;
	}

	/**
	 * @param altnick the altnick to set
	 */
	public void setAltnick(String altnick) {
		this.altnick = altnick;
	}

	/**
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * @param nickname the nickname to set
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * @param fullName the fullName to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
}