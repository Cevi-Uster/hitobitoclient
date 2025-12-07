package ch.cevi.db.client.communication;

import ch.cevi.db.client.api.invoker.ApiClient;
import ch.cevi.db.client.business.entities.Person;

public interface ISession {

	ServerFacade login(String email, String password, String secondFactorCode) throws Exception;
	
	ApiClient getApiClient();

	String callServerMethod(String url) throws Exception;

	void logout() throws Exception;

	Person getLoggedInUser();

	String getBaseUrl();

	boolean isLoggedIn();

	String getMainGroupId();

}