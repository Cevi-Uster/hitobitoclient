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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
public class Session implements ISession {

	private static final Logger LOGGER = LoggerFactory.getLogger(Session.class.getName());

	private Person loggedInUser;

	private Client client;

	private String baseUrl;
	
	private String authenticityToken;

	private boolean usePrimaryGroupAsRoot;

	private final HttpClient httpClient;

	static {
		CookieHandler.setDefault(new CookieManager());
	}

	public Session(String baseUrl, boolean usePrimaryGroupAsRoot) {
		this.baseUrl = baseUrl;
		this.usePrimaryGroupAsRoot = usePrimaryGroupAsRoot;
		this.httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2)
				.connectTimeout(Duration.ofSeconds(Constants.CONNECTION_TIME_OUT))
				.cookieHandler(CookieHandler.getDefault()).followRedirects(Redirect.ALWAYS)
				.executor(Executors.newFixedThreadPool(16)).build();
	}

	/**
	 * Login to the new Cevi.DB API using two-step authentication if required.
	 * @param email User email
	 * @param password User password
	 * @param secondFactorCode Optional second factor code (TOTP)
	 */
	@Override
	public synchronized void login(final String email, final String password, final String secondFactorCode) throws Exception {
        // Step 0: GET /users/sign_in to fetch CSRF token
        String signInUrl = baseUrl + "/users/sign_in";
        HttpRequest signInGetRequest = HttpRequest.newBuilder()
            .uri(URI.create(signInUrl))
            .header("User-Agent", "Java 11 HttpClient Bot")
            .GET()
            .build();
        HttpResponse<String> signInGetResponse = httpClient.send(signInGetRequest, HttpResponse.BodyHandlers.ofString());
        String authenticityToken = null;
        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(signInGetResponse.body());
        org.jsoup.nodes.Element tokenInput = doc.selectFirst("input[name=authenticity_token]");
        if (tokenInput != null) {
            authenticityToken = tokenInput.attr("value");
        } else {
            throw new Exception("Could not find authenticity_token on login page");
        }
        // Step 1: POST credentials to /users/sign_in with authenticity_token
        String loginUrl = baseUrl + "/users/sign_in";
        String loginBody = String.format("person[login_identity]=%s&person[password]=%s&person[remember_me]=0&authenticity_token=%s", 
            email, password, java.net.URLEncoder.encode(authenticityToken, java.nio.charset.StandardCharsets.UTF_8));
        HttpRequest loginRequest = HttpRequest.newBuilder()
            .uri(URI.create(loginUrl))
            .header("User-Agent", "Java 11 HttpClient Bot")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(loginBody))
            .build();
        HttpResponse<String> loginResponse = httpClient.send(loginRequest, HttpResponse.BodyHandlers.ofString());
        LOGGER.info("Login response code: " + loginResponse.statusCode());
        LOGGER.debug("Login response body:\n" + loginResponse.body());
        // Check if second factor is required
        boolean requiresSecondFactor = loginResponse.body().contains("second_factor");
        if (requiresSecondFactor) {
            if (secondFactorCode == null || secondFactorCode.isEmpty()) {
                throw new Exception("Second factor code required but not provided.");
            }
            // Fetch authenticity_token for second factor page
            org.jsoup.nodes.Document doc2 = org.jsoup.Jsoup.parse(loginResponse.body());
            org.jsoup.nodes.Element tokenInput2 = doc2.selectFirst("input[name=authenticity_token]");
            String secondFactorToken = tokenInput2 != null ? tokenInput2.attr("value") : authenticityToken;
            // Step 2: POST second factor code
            String secondFactorUrl = baseUrl + "/users/second_factor?second_factor=totp";
            String secondFactorBody = String.format("second_factor_code=%s&commit=Absenden&authenticity_token=%s", 
                secondFactorCode, java.net.URLEncoder.encode(secondFactorToken, java.nio.charset.StandardCharsets.UTF_8));
            HttpRequest secondFactorRequest = HttpRequest.newBuilder()
                .uri(URI.create(secondFactorUrl))
                .header("User-Agent", "Java 11 HttpClient Bot")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(secondFactorBody))
                .build();
            HttpResponse<String> secondFactorResponse = httpClient.send(secondFactorRequest, HttpResponse.BodyHandlers.ofString());
            LOGGER.info("Second factor response code: " + secondFactorResponse.statusCode());
            LOGGER.debug("Second factor response body:\n" + secondFactorResponse.body());
            if (secondFactorResponse.statusCode() != 200) {
                throw new Exception("Second factor authentication failed: " + secondFactorResponse.body());
            }
        } else if (loginResponse.statusCode() != 200) {
            throw new Exception("Login failed: " + loginResponse.body());
        }
        // After successful login, fetch user info by email and set loggedInUser
        String peopleUrl = String.format("%s/api/people?filter[email_eq]=%s", baseUrl, email);
        HttpRequest peopleRequest = HttpRequest.newBuilder()
            .uri(URI.create(peopleUrl))
            .header("User-Agent", "Java 11 HttpClient Bot")
            .GET()
            .build();
        HttpResponse<String> peopleResponse = httpClient.send(peopleRequest, HttpResponse.BodyHandlers.ofString());
        if (peopleResponse.statusCode() == 200) {
            org.json.JSONObject peopleObject = new org.json.JSONObject(peopleResponse.body());
            org.json.JSONArray dataArray = peopleObject.optJSONArray("data");
            if (dataArray != null && dataArray.length() > 0) {
                org.json.JSONObject userData = dataArray.getJSONObject(0);
                Person person = new Person();
                person.initFromJSONObject(userData);
                loggedInUser = person;
                loggedInUser.setPassword(password); // Optionally store password
            } else {
                throw new Exception("Could not find user info after login.");
            }
        } else {
            throw new Exception("Failed to fetch user info after login: " + peopleResponse.body());
        }
		// Session cookie (_session_id) is managed by HttpClient's CookieHandler

	}

	private void initCsrfToken() throws Exception {
		final String url = String.format("%s/users/sign_in", baseUrl);

		final HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
				.setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
				.GET()
				.build();

		final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		
		updateCsrfToken(response);
	}

	private void updateCsrfToken(final HttpResponse<String> response) {
		final Document document = Jsoup.parse(response.body());
		final Element meta = document.select("meta[name=csrf-token]").first();
		if (meta != null) {
			authenticityToken = meta.attr("content");
			LOGGER.info("Authenticity token found and initialized");
		} else {
			LOGGER.warn("Authenticity token NOT found");
		}
	}

	@Override
	public synchronized String callServerMethod(String url) throws Exception {
		String result = null;
		if (httpClient != null && loggedInUser != null) {
			// add json header
			HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url))
					.setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
					.header("Content-Type", "application/json").build();

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
				HttpRequest request = HttpRequest.newBuilder().DELETE().uri(URI.create(url))
						.setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
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