package ch.cevi.db.client.communication;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cevi.db.client.api.invoker.ApiClient;
import ch.cevi.db.client.business.entities.Person;
import ch.cevi.db.client.configuration.Constants;

/**
 * Represents a session between this client and the Hitobito server, backed
 * by an OAuth2 access/refresh token pair (see {@link OAuthLoginFlow}) rather
 * than a browser session cookie. Tokens are kept in memory only, matching the
 * app's existing behavior of not persisting credentials to disk; logging in
 * again after a restart repeats the browser login.
 *
 * @author developer
 */
public class Session implements ISession {

	private static final Logger LOGGER = LoggerFactory.getLogger(Session.class.getName());

	private final String baseUrl;

	private final boolean usePrimaryGroupAsRoot;

	private final HttpClient httpClient;

	private final OAuthLoginFlow oauthLoginFlow;

	private Person loggedInUser;

	private ApiClient apiClient;

	private JsonApiClient jsonApiClient;

	private volatile String accessToken;

	private volatile String refreshToken;

	private volatile Instant accessTokenExpiresAt;

	public Session(String baseUrl, String oauthClientId, boolean usePrimaryGroupAsRoot) {
		this.baseUrl = baseUrl;
		this.usePrimaryGroupAsRoot = usePrimaryGroupAsRoot;
		this.httpClient = HttpClient.newBuilder()
				.version(HttpClient.Version.HTTP_2)
				.connectTimeout(Duration.ofMillis(Constants.CONNECTION_TIME_OUT))
				.executor(Executors.newFixedThreadPool(16))
				.build();
		this.oauthLoginFlow = new OAuthLoginFlow(baseUrl, oauthClientId, httpClient);
	}

	@Override
	public synchronized void login() throws Exception {
		applyTokens(oauthLoginFlow.login());
		apiClient = new ApiClient();
		apiClient.setBasePath(baseUrl + "/api");
		apiClient.addDefaultHeader("Authorization", "Bearer " + accessToken);
		jsonApiClient = new JsonApiClient(apiClient, httpClient, () -> accessToken);
		loggedInUser = resolveLoggedInUser();
	}

	private void applyTokens(OAuthLoginFlow.TokenResponse tokens) {
		this.accessToken = tokens.accessToken();
		if (tokens.refreshToken() != null) {
			this.refreshToken = tokens.refreshToken();
		}
		this.accessTokenExpiresAt = tokens.expiresAt();
	}

	/**
	 * Refreshes the access token if it has expired or is about to. Business
	 * operations that may run long after login should call this first; {@link
	 * #getApiClient()}'s {@code Authorization} header is updated in place.
	 */
	public synchronized void ensureFreshAccessToken() throws Exception {
		if (refreshToken == null || accessTokenExpiresAt == null) {
			return;
		}
		if (Instant.now().isBefore(accessTokenExpiresAt.minusSeconds(30))) {
			return;
		}
		LOGGER.info("Access token expired or expiring soon, refreshing");
		applyTokens(oauthLoginFlow.refresh(refreshToken));
		apiClient.addDefaultHeader("Authorization", "Bearer " + accessToken);
	}

	/**
	 * Resolves who just logged in via Doorkeeper's token-info endpoint (which
	 * reports the {@code resource_owner_id} the access token was issued for),
	 * then loads that person through the JSON:API.
	 */
	private Person resolveLoggedInUser() throws Exception {
		JSONObject tokenInfo = fetchTokenInfo();
		String personId = tokenInfo.optString("resource_owner_id", null);
		if (personId == null || personId.isEmpty()) {
			throw new Exception("Could not determine the logged-in person from the OAuth token.");
		}
		JSONObject personDocument = jsonApiClient.get("/people/" + personId);
		JSONObject resource = personDocument.optJSONObject("data");
		if (resource == null) {
			throw new Exception("Could not load the logged-in person (id " + personId + ") after login.");
		}
		Person person = new Person();
		person.initFromJSONObject(resource);
		return person;
	}

	private JSONObject fetchTokenInfo() throws Exception {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(baseUrl + "/oauth/token/info"))
				.header("Authorization", "Bearer " + accessToken)
				.header("Accept", "application/json")
				.GET()
				.build();
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() != 200) {
			throw new Exception("Could not fetch OAuth token info, status " + response.statusCode() + ": " + response.body());
		}
		return new JSONObject(response.body());
	}

	@Override
	public synchronized void logout() throws Exception {
		try {
			if (accessToken != null) {
				oauthLoginFlow.revoke(refreshToken != null ? refreshToken : accessToken);
			}
		} finally {
			loggedInUser = null;
			accessToken = null;
			refreshToken = null;
			accessTokenExpiresAt = null;
			apiClient = null;
			jsonApiClient = null;
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
