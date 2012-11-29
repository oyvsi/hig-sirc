package no.hig.sss.sirc;

/**
 * This class holds one server, with name, url, group, 
 * and the different ports the server accepts
 * 
 * @author Oyvind Sigerstad, Nils Slaaen, Bjorn-Erik Strand
 * 
 */
public class OptionsServerPrefs {
	private String serverName;
	private String serverUrl;
	private String serverGroup;
	private int[] port;
	
	/**
	 * class constructor not used
	 */
	public OptionsServerPrefs() {}
	
	/**
	 * Class constructor, sets values for object
	 * @param serverName
	 * @param serverUrl
	 * @param serverGroup
	 * @param port
	 */
	public OptionsServerPrefs(String serverName, String serverUrl, String serverGroup, int[] port) {
		this.serverName = serverName;
		this.serverUrl = serverUrl;
		this.serverGroup = serverGroup;
		this.port = port;
	}

	/**
	 * Getter for server name
	 * @return the serverName
	 */
	public String getServerName() {
		return serverName;
	}

	/**
	 * Setter for server name
	 * @param serverName the serverName to set
	 */
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	/**
	 * Getter for serverurl
	 * @return the serverUrl
	 */
	public String getServerUrl() {
		return serverUrl;
	}

	/**
	 * Setter for serverurl
	 * @param serverUrl the serverUrl to set
	 */
	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	/**
	 * Getter for server group
	 * @return the serverGroup
	 */
	public String getServerGroup() {
		return serverGroup;
	}

	/**
	 * Setter for server group
	 * @param serverGroup the serverGroup to set
	 */
	public void setServerGroup(String serverGroup) {
		this.serverGroup = serverGroup;
	}

	/**
	 * Getter for port
	 * @return the port
	 */
	public int[] getPort() {
		return port;
	}

	/**
	 * Setter for port
	 * @param port the port to set
	 */
	public void setPort(int[] port) {
		this.port = port;
	}
}