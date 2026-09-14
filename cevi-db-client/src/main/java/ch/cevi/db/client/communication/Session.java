package ch.cevi.db.client.communication;

import java.net.http.HttpClient;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

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

	public Session(String baseUrl, String oauthClientId, String oauthClientSecret, boolean usePrimaryGroupAsRoot) {
		this.baseUrl = baseUrl;
		this.usePrimaryGroupAsRoot = usePrimaryGroupAsRoot;
		this.httpClient = HttpClient.newBuilder()
				.version(HttpClient.Version.HTTP_2)
				.connectTimeout(Duration.ofMillis(Constants.CONNECTION_TIME_OUT))
				.executor(Executors.newFixedThreadPool(16))
				.build();
		this.oauthLoginFlow = new OAuthLoginFlow(baseUrl, oauthClientId, oauthClientSecret, httpClient);
	}

	/**
	 * Opens the system browser to Hitobito's login/consent page, then blocks
	 * on {@code authorizationCodePrompt} for the code the user reads off
	 * that page and pastes back (Hitobito's OAuth redirect_uri here is the
	 * {@code urn:ietf:wg:oauth:2.0:oob} constant, so there is no automatic
	 * redirect to catch -- see {@link OAuthLoginFlow}). {@code
	 * authorizationCodePrompt} is expected to show a UI prompt and is called
	 * from whatever thread this method runs on (a background {@code
	 * SwingWorker} thread in {@link ch.cevi.db.client.gui.MainController}),
	 * so it must hop to the EDT itself if it needs to.
	 */
	@Override
	public synchronized void login(Supplier<String> authorizationCodePrompt) throws Exception {
		oauthLoginFlow.openBrowserForLogin();
		String code = authorizationCodePrompt.get();
		if (code == null || code.isBlank()) {
			throw new Exception("Login cancelled: no authorization code entered.");
		}
		applyTokens(oauthLoginFlow.exchangeCode(code.trim()));
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
	 * Resolves who just logged in via {@code /oauth/profile} (which, with
	 * the {@code with_roles} scope, reports the full profile including
	 * {@code id}) -- see {@link OAuthLoginFlow#fetchProfile}.
	 */
	private Person resolveLoggedInUser() throws Exception {
		JSONObject profile = oauthLoginFlow.fetchProfile(accessToken, "with_roles");
		Person person = new Person();
		person.initFromOAuthProfile(profile);
		if (person.getId() == null) {
			throw new Exception("Could not determine the logged-in person from the OAuth profile.");
		}
		return person;
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
