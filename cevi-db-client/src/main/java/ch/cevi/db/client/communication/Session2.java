package ch.cevi.db.client.communication;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;

import ch.cevi.db.client.business.entities.Person;
import ch.cevi.db.client.configuration.Constants;

/**
 * Represents a session between this client and the hitobito server
 * 
 * @author developer
 *
 */
public class Session2 implements ISession {

	private static final Logger LOGGER = LoggerFactory.getLogger(Session2.class.getName());

	private Person loggedInUser;

	private Client client;

	private String baseUrl;

	private boolean usePrimaryGroupAsRoot;
	
	private final HttpClient httpClient;

	static {
		CookieHandler.setDefault(new CookieManager());		
	}
	
	public Session2(String baseUrl, boolean usePrimaryGroupAsRoot) {
		this.baseUrl = baseUrl;
		this.usePrimaryGroupAsRoot = usePrimaryGroupAsRoot;
		this.httpClient = HttpClient.newBuilder()
	            .version(HttpClient.Version.HTTP_2)
	            .connectTimeout(Duration.ofSeconds(Constants.CONNECTION_TIME_OUT))
	            .cookieHandler(CookieHandler.getDefault())
	            .followRedirects(Redirect.ALWAYS)
	            .executor(Executors.newFixedThreadPool(16))
	            .build();
	}

	@Override
	public synchronized void login(String email, String password) throws Exception {
		String url = String.format("%s/users/sign_in.json?person[email]=%s&person[password]=%s", baseUrl, email, password);

		// add json header
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .uri(URI.create(url))
                .setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // print status code
        LOGGER.info("Login response code: " + response.statusCode());

        // print response body
        LOGGER.debug("Login response body:\n" +response.body());
        
        String output = response.body();
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

	@Override
	public synchronized String callServerMethod(String url) throws Exception {
		String result = null;
		if (httpClient != null && loggedInUser != null) {
			// add json header
	        HttpRequest request = HttpRequest.newBuilder()
	                .GET()
	                .uri(URI.create(url))
	                .setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
	                .header("Content-Type", "application/json")
	                .build();

	     
			long startTime = System.nanoTime();
			int retryCount = 0;
			while (retryCount < 3) {
				try {
					HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
					if (response.statusCode() != 200) {
						retryCount++;
						if (retryCount == 3) {
							throw new Exception("Got error from server, code: " + response.statusCode());
						}
					} else {
						result = response.body();
						break;
					}
				} catch (IOException e) {
					retryCount++;
					if (retryCount == 3) {
						throw new Exception("Got error from server: " + e.getMessage());
					}
				}
			}
			long endTime = System.nanoTime();
			long duration = (endTime - startTime) / 1000000;
			LOGGER.info("Server call duration: " + duration + " ms");
		} else {
			throw new Exception("User is not logged in at current time!");
		}
		return result;
	}
	

	@Override	
	public synchronized void logout() throws Exception {
		try {
			if (client != null && loggedInUser != null) {
				String url = String.format("%s/users/token.json?person[email]=%s&person[password]=%s", baseUrl,
						loggedInUser.getEmail(), loggedInUser.getPassword());
				// add json header
				HttpRequest request = HttpRequest.newBuilder().DELETE()
						.uri(URI.create(url)).setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
						.header("Content-Type", "application/json").build();

				HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

				// print status code
				LOGGER.info("Login response code: " + response.statusCode());

				// print response body
				LOGGER.debug("Login response body:\n" + response.body());
				
				if (response.statusCode() != 200) {
					throw new Exception("Got error from server, code: " + response.statusCode());
				}
			}
		} finally {
			loggedInUser = null;
		}
	}

	@Override
	public Person getLoggedInUser() {
		return loggedInUser;
	}

	@Override
	public String getBaseUrl() {
		return baseUrl;
	}

	@Override
	public boolean isLoggedIn() {
		return loggedInUser != null;
	}

	@Override
	public String getMainGroupId() {
		if (usePrimaryGroupAsRoot && loggedInUser.getPrimaryGroupId() != null) {
			return loggedInUser.getPrimaryGroupId();
		}
		return "1";
	}
}
