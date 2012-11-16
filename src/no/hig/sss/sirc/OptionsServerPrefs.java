package no.hig.sss.sirc;

public class OptionsServerPrefs {
	private String serverName;
	private String serverUrl;
	private String serverGroup;
	private int[] port;
	
	public OptionsServerPrefs() {
		
	}
	public OptionsServerPrefs(String serverName, String serverUrl, String serverGroup, int[] port) {
		this.serverName = serverName;
		this.serverUrl = serverUrl;
		this.serverGroup = serverGroup;
		this.port = port;
	}
	

	/**
	 * @return the serverName
	 */
	public String getServerName() {
		return serverName;
	}

	/**
	 * @param serverName the serverName to set
	 */
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	/**
	 * @return the serverUrl
	 */
	public String getServerUrl() {
		return serverUrl;
	}

	/**
	 * @param serverUrl the serverUrl to set
	 */
	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	/**
	 * @return the serverGroup
	 */
	public String getServerGroup() {
		return serverGroup;
	}

	/**
	 * @param serverGroup the serverGroup to set
	 */
	public void setServerGroup(String serverGroup) {
		this.serverGroup = serverGroup;
	}

	/**
	 * @return the port
	 */
	public int[] getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int[] port) {
		this.port = port;
	}
	
}