package ch.cevi.db.client.configuration;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Configuration {

	public enum BaseUrl {
		Live("https://db.cevi.ch"), Test("http://cevi.puzzle.ch");

		private final String url;

		private BaseUrl(String s) {
			url = s;
		}

		public boolean equalsUrl(String otherUrl) {
			return (otherUrl == null) ? false : url.equals(otherUrl);
		}

		public String toString() {
			return this.url;
		}
	};

	public static final String BASE_URL_PROPERTY = "baseUrl";

	public static final String USERNAME_PROPERTY = "username";

	public static final String PASSWORD_PROPERTY = "password";

	public static final String USE_PRIMARY_GROUP_AS_ROOT_PROPERY = "usePrimaryGroupAsRoot";

	private PropertyChangeSupport propertyChangeSupport;
	private BaseUrl baseUrl;
	private String username;
	private String password;
	private boolean usePrimaryGroupAsRoot;

	public Configuration() {
		super();
		this.propertyChangeSupport = new PropertyChangeSupport(this);
	}

	public BaseUrl getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(BaseUrl baseUrl) {
		propertyChangeSupport.firePropertyChange(BASE_URL_PROPERTY, this.baseUrl, baseUrl);
		this.baseUrl = baseUrl;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		propertyChangeSupport.firePropertyChange(USERNAME_PROPERTY, this.username, username);
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		propertyChangeSupport.firePropertyChange(PASSWORD_PROPERTY, this.password, password);
		this.password = password;
	}

	public boolean getUsePrimaryGroupAsRoot() {
		return usePrimaryGroupAsRoot;
	}

	public void setUsePrimaryGroupAsRoot(boolean usePrimaryGroupAsRoot) {
		this.usePrimaryGroupAsRoot = usePrimaryGroupAsRoot;
	}

	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		this.propertyChangeSupport.addPropertyChangeListener(pcl);
	}

	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		this.propertyChangeSupport.removePropertyChangeListener(pcl);
	}
}
