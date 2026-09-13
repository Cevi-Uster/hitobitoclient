package ch.cevi.db.client.communication;

import ch.cevi.db.client.api.invoker.ApiClient;
import ch.cevi.db.client.business.entities.Person;

public interface ISession {

	/**
	 * Logs in via the OAuth2 Authorization Code + PKCE flow: opens the user's
	 * system browser to Hitobito's own login page (which handles credentials
	 * and any second factor) and waits for the resulting authorization
	 * redirect.
	 */
	void login() throws Exception;

	void logout() throws Exception;

	Person getLoggedInUser();

	String getBaseUrl();

	boolean isLoggedIn();

	String getMainGroupId();

	ApiClient getApiClient();

	JsonApiClient getJsonApiClient();

}
