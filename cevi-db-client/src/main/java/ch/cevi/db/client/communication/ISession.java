package ch.cevi.db.client.communication;

import ch.cevi.db.client.business.entities.Person;

public interface ISession {

	void login(String email, String password) throws Exception;

	String callServerMethod(String url) throws Exception;

	void logout() throws Exception;

	Person getLoggedInUser();

	String getBaseUrl();

	boolean isLoggedIn();

	String getMainGroupId();

}