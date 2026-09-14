package ch.cevi.db.client.communication;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import ch.cevi.db.client.api.invoker.ApiClient;
import ch.cevi.db.client.business.entities.Person;
import ch.cevi.db.client.configuration.Constants;

/**
 * Represents a session between this client and the Hitobito server.
 * <p>
 * Login uses the same Devise session Hitobito's own {@code /users/sign_in}
 * page uses (GET it for a CSRF token, POST credentials, handle a second
 * factor if requested), rather than OAuth2: OAuth requires a root-group
 * admin to register an application (client_id/client_secret) on the
 * instance, which most users of this app -- including its maintainer -- do
 * not have the rights to do. The resulting {@code _session_id} cookie is
 * then carried into the generated {@link ApiClient} via {@code
 * addDefaultCookie}, so the same session that logged into the website is
 * used for all JSON:API calls (the spec declares this as a valid auth
 * scheme: {@code SessionAuth}, an apiKey in the {@code _session_id} cookie).
 * Nothing is persisted to disk; logging in again after a restart repeats
 * this flow.
 *
 * @author developer
 */
public class Session implements ISession {

	private static final Logger LOGGER = LoggerFactory.getLogger(Session.class.getName());

	private static final String SESSION_COOKIE_NAME = "_session_id";

	private final String baseUrl;

	private final boolean usePrimaryGroupAsRoot;

	private final CookieManager cookieManager;

	private final HttpClient httpClient;

	private Person loggedInUser;

	private ApiClient apiClient;

	private JsonApiClient jsonApiClient;

	public Session(String baseUrl, boolean usePrimaryGroupAsRoot) {
		this.baseUrl = baseUrl;
		this.usePrimaryGroupAsRoot = usePrimaryGroupAsRoot;
		this.cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
		this.httpClient = HttpClient.newBuilder()
				.version(HttpClient.Version.HTTP_2)
				.connectTimeout(Duration.ofMillis(Constants.CONNECTION_TIME_OUT))
				.cookieHandler(cookieManager)
				.followRedirects(Redirect.NORMAL)
				.executor(Executors.newFixedThreadPool(16))
				.build();
	}

	@Override
	public synchronized void login(String email, String password, String secondFactorCode) throws Exception {
		String signInUrl = baseUrl + "/users/sign_in";
		String authenticityToken = fetchAuthenticityToken(signInUrl);

		String loginBody = "person[login_identity]=" + urlEncode(email)
				+ "&person[password]=" + urlEncode(password)
				+ "&person[remember_me]=0"
				+ "&authenticity_token=" + urlEncode(authenticityToken);
		HttpResponse<String> loginResponse = postForm(signInUrl, loginBody);
		LOGGER.info("Login response code: " + loginResponse.statusCode());

		if (loginResponse.body().contains("second_factor")) {
			if (secondFactorCode == null || secondFactorCode.isEmpty()) {
				throw new Exception("Second factor code required but not provided.");
			}
			String secondFactorToken = extractAuthenticityToken(loginResponse.body()).orElse(authenticityToken);
			String secondFactorBody = "second_factor_code=" + urlEncode(secondFactorCode)
					+ "&commit=Absenden"
					+ "&authenticity_token=" + urlEncode(secondFactorToken);
			HttpResponse<String> secondFactorResponse = postForm(baseUrl + "/users/second_factor?second_factor=totp", secondFactorBody);
			LOGGER.info("Second factor response code: " + secondFactorResponse.statusCode());
			if (secondFactorResponse.statusCode() != 200) {
				throw new Exception("Second factor authentication failed: " + secondFactorResponse.body());
			}
		} else if (loginResponse.statusCode() != 200) {
			throw new Exception("Login failed: " + loginResponse.body());
		}

		String sessionCookie = findSessionCookie()
				.orElseThrow(() -> new Exception("Login did not yield a session cookie; check credentials."));

		apiClient = new ApiClient();
		apiClient.setBasePath(baseUrl + "/api");
		apiClient.addDefaultCookie(SESSION_COOKIE_NAME, sessionCookie);
		jsonApiClient = new JsonApiClient(apiClient, httpClient,
				requestBuilder -> requestBuilder.header("Cookie", SESSION_COOKIE_NAME + "=" + sessionCookie));

		loggedInUser = resolveLoggedInUser(email);
	}

	private Optional<String> findSessionCookie() {
		return cookieManager.getCookieStore().getCookies().stream()
				.filter(cookie -> SESSION_COOKIE_NAME.equals(cookie.getName()))
				.map(HttpCookie::getValue)
				.findFirst();
	}

	private String fetchAuthenticityToken(String url) throws Exception {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("User-Agent", "Java HttpClient")
				.GET()
				.build();
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		return extractAuthenticityToken(response.body())
				.orElseThrow(() -> new Exception("Could not find authenticity_token on " + url));
	}

	private static Optional<String> extractAuthenticityToken(String html) {
		Document document = Jsoup.parse(html);
		Element tokenInput = document.selectFirst("input[name=authenticity_token]");
		return tokenInput != null ? Optional.of(tokenInput.attr("value")) : Optional.empty();
	}

	private HttpResponse<String> postForm(String url, String body) throws Exception {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("User-Agent", "Java HttpClient")
				.header("Content-Type", "application/x-www-form-urlencoded")
				.POST(HttpRequest.BodyPublishers.ofString(body))
				.build();
		return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
	}

	private static String urlEncode(String value) {
		return URLEncoder.encode(value, StandardCharsets.UTF_8);
	}

	/**
	 * Resolves the logged-in person by the email used to log in. Session
	 * (cookie) auth has no equivalent of an OAuth "resource_owner_id" to
	 * read an id from directly, but the spec supports filtering people by
	 * email.
	 */
	private Person resolveLoggedInUser(String email) throws Exception {
		MultiValueMap<String, String> query = new LinkedMultiValueMap<>();
		query.add("filter[email][eq]", email);
		JSONObject document = jsonApiClient.get("/people", query);
		JSONArray dataArray = document.optJSONArray("data");
		JSONObject resource = (dataArray != null && dataArray.length() > 0) ? dataArray.optJSONObject(0) : null;
		if (resource == null) {
			throw new Exception("Could not find a person with email " + email + " after login.");
		}
		Person person = new Person();
		person.initFromJSONObject(resource);
		return person;
	}

	@Override
	public synchronized void logout() throws Exception {
		try {
			Optional<String> sessionCookie = findSessionCookie();
			if (sessionCookie.isPresent()) {
				try {
					String authenticityToken = fetchAuthenticityToken(baseUrl + "/");
					HttpRequest request = HttpRequest.newBuilder()
							.uri(URI.create(baseUrl + "/users/sign_out"))
							.header("User-Agent", "Java HttpClient")
							.header("X-CSRF-Token", authenticityToken)
							.DELETE()
							.build();
					httpClient.send(request, HttpResponse.BodyHandlers.discarding());
				} catch (Exception e) {
					LOGGER.warn("Could not sign out cleanly, discarding the local session anyway: " + e.getMessage());
				}
			}
		} finally {
			loggedInUser = null;
			apiClient = null;
			jsonApiClient = null;
			cookieManager.getCookieStore().removeAll();
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

	@Override
	public ApiClient getApiClient() {
		return apiClient;
	}

	@Override
	public JsonApiClient getJsonApiClient() {
		return jsonApiClient;
	}
}
