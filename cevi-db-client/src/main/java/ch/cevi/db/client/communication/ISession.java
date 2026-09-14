package ch.cevi.db.client.communication;

import ch.cevi.db.client.api.invoker.ApiClient;
import ch.cevi.db.client.business.entities.Person;

public interface ISession {

	/**
	 * Logs in via the classic Devise session (the same login Hitobito's own
	 * {@code /users/sign_in} page uses), and carries the resulting {@code
	 * _session_id} cookie into the JSON:API client. No OAuth application
	 * registration is required -- see {@link Session} for why this is the
	 * mechanism used instead of OAuth2.
	 */
	void login(String email, String password, String secondFactorCode) throws Exception;

	void logout() throws Exception;

	Person getLoggedInUser();

	String getBaseUrl();

	boolean isLoggedIn();

	String getMainGroupId();

	ApiClient getApiClient();

	JsonApiClient getJsonApiClient();

}
