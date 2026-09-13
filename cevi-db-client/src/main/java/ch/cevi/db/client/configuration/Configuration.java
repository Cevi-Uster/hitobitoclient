package ch.cevi.db.client.configuration;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Configuration {

	public enum BaseUrl {
		Live("https://db.cevi.ch", System.getProperty("cevidb.oauth.clientId.live", "REPLACE_WITH_LIVE_OAUTH_CLIENT_ID")),
		Test("http://cevi.puzzle.ch", System.getProperty("cevidb.oauth.clientId.test", "REPLACE_WITH_TEST_OAUTH_CLIENT_ID"));

		private final String url;

		private final String oauthClientId;

		private BaseUrl(String url, String oauthClientId) {
			this.url = url;
			this.oauthClientId = oauthClientId;
		}

		public boolean equalsUrl(String otherUrl) {
			return (otherUrl == null) ? false : url.equals(otherUrl);
		}

		/**
		 * The client_id of the OAuth application registered for this Hitobito
		 * instance. Register one application per environment in the instance's
		 * admin UI (redirect URI: {@link Constants#OAUTH_REDIRECT_URI}), then set
		 * it via the {@code cevidb.oauth.clientId.live}/{@code .test} system
		 * properties (the defaults here are placeholders).
		 */
		public String getOauthClientId() {
			return oauthClientId;
		}

		public String toString() {
			return this.url;
		}
	};

	public static final String BASE_URL_PROPERTY = "baseUrl";

	public static final String USE_PRIMARY_GROUP_AS_ROOT_PROPERY = "usePrimaryGroupAsRoot";

	private PropertyChangeSupport propertyChangeSupport;
	private BaseUrl baseUrl;
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
