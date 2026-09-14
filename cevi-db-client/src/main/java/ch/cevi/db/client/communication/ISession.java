package ch.cevi.db.client.communication;

import java.util.function.Supplier;

import ch.cevi.db.client.api.invoker.ApiClient;
import ch.cevi.db.client.business.entities.Person;

public interface ISession {

	/**
	 * Logs in via OAuth2: opens the user's system browser to Hitobito's own
	 * login page (which handles credentials and any second factor), then
	 * blocks on {@code authorizationCodePrompt} for the authorization code
	 * the user reads off that page and pastes back.
	 */
	void login(Supplier<String> authorizationCodePrompt) throws Exception;

	void logout() throws Exception;

	Person getLoggedInUser();

	String getBaseUrl();

	boolean isLoggedIn();

	String getMainGroupId();

	ApiClient getApiClient();

	JsonApiClient getJsonApiClient();

}
