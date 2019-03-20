package ch.cevi.db.client.communication;

import java.util.List;

import javax.ws.rs.core.NewCookie;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import ch.cevi.db.client.business.entities.Person;
import ch.cevi.db.client.configuration.Constants;

/**
 * Represents a session between this client and the hitobito server
 * 
 * @author developer
 *
 */
public class Session {

	private static final Logger LOGGER = LoggerFactory.getLogger(Session.class.getName());

	private Person loggedInUser;

	private Client client;

	private String baseUrl;

	private List<NewCookie> cookies;

	private boolean usePrimaryGroupAsRoot;

	public Session(String baseUrl, boolean usePrimaryGroupAsRoot) {
		this.baseUrl = baseUrl;
		this.usePrimaryGroupAsRoot = usePrimaryGroupAsRoot;
	}

	public synchronized void login(String email, String password) throws Exception {
		client = Client.create();
		client.setConnectTimeout(Constants.CONNECTION_TIME_OUT);
		String url = String.format("%s/users/sign_in.json?person[email]=%s&person[password]=%s", baseUrl, email, password);

		WebResource webResource = client.resource(url);
		ClientResponse response = webResource.accept("application/json").post(ClientResponse.class);

		if (response.getStatus() != 200) {
			throw new Exception("Got error from server, code: " + response.getStatus());
		}

		String output = response.getEntity(String.class);
		cookies = response.getCookies();
		// Try to get authentication token from output
		JSONObject jsonObject = new JSONObject(output);
		JSONArray peopleArray = jsonObject.optJSONArray("people");

		if (peopleArray != null && peopleArray.length() > 0 && peopleArray.optJSONObject(0) != null) {
			JSONObject personObject = peopleArray.optJSONObject(0);
			Person person = new Person();
			person.initFromJSONObject(personObject);
			if (person != null) {
				loggedInUser = person;
				loggedInUser.setPassword(password);
			}
		} else {
			throw new Exception("Unexpected answer from server: " + output);
		}
	}

	public synchronized String callServerMethod(String url) throws Exception {
		String result = null;
		if (client != null && loggedInUser != null) {
			WebResource webResource = client.resource(url);
			if (cookies != null) {
				for (NewCookie cookie : cookies) {
					webResource.cookie(cookie);
				}
			}
			long startTime = System.nanoTime();
			ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
			if (response.getStatus() != 200) {
				throw new Exception("Got error from server, code: " + response.getStatus());
			}
			result = response.getEntity(String.class);
			long endTime = System.nanoTime();
			long duration = (endTime - startTime) / 1000000;
			LOGGER.info("Server call duration: " + duration + " ms");
		} else {
			throw new Exception("User is not logged in at current time!");
		}
		return result;
	}

	public synchronized void logout() throws Exception {
		try {
			if (client != null && loggedInUser != null) {
				String url = String.format("%s/users/token.json?person[email]=%s&person[password]=%s", baseUrl, loggedInUser.getEmail(), loggedInUser.getPassword());
				WebResource webResource = client.resource(url);
				if (cookies != null) {
					for (NewCookie cookie : cookies) {
						webResource.cookie(cookie);
					}
				}
				ClientResponse response = webResource.accept("application/json").delete(ClientResponse.class);

				if (response.getStatus() != 200) {
					throw new Exception("Got error from server, code: " + response.getStatus());
				}

			}
		} finally {
			loggedInUser = null;
			cookies = null;
			if (client != null) {
				client.destroy();
				client = null;
			}
		}
	}

	public Person getLoggedInUser() {
		return loggedInUser;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public boolean isLoggedIn() {
		return loggedInUser != null;
	}

	public String getMainGroupId() {
		if (usePrimaryGroupAsRoot && loggedInUser.getPrimaryGroupId() != null) {
			return loggedInUser.getPrimaryGroupId();
		}
		return "1";
	}
}
