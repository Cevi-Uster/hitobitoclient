package ch.cevi.db.client.communication;

import java.awt.Desktop;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cevi.db.client.configuration.Constants;

/**
 * Performs an OAuth2 login against a Hitobito instance's Doorkeeper
 * endpoints ({@code /oauth/authorize}, {@code /oauth/token}, {@code
 * /oauth/revoke}, {@code /oauth/profile}), per
 * <a href="https://github.com/hitobito/hitobito/blob/master/doc/developer/people/oauth.md">Hitobito's
 * OAuth documentation</a>.
 * <p>
 * Doorkeeper here is configured as a <b>confidential</b> client (every token
 * request needs {@code client_secret}; PKCE/public clients aren't
 * documented or offered) and requires {@code redirect_uri} to be {@code
 * https://} except on Hitobito's own "integration" deployments -- neither of
 * which a distributed desktop app can offer. So this uses the documented
 * {@code urn:ietf:wg:oauth:2.0:oob} redirect: the user's browser shows the
 * authorization code on the Hitobito page instead of being redirected
 * anywhere, and the user pastes it back into the app (see {@link
 * ch.cevi.db.client.gui.MainController}). The user's system browser is
 * opened to complete the login (and any second-factor step) on Hitobito's
 * own login page; this class never sees a password.
 */
public class OAuthLoginFlow {

	private static final Logger LOGGER = LoggerFactory.getLogger(OAuthLoginFlow.class.getName());

	/**
	 * Result of a token request: an access token to authenticate API calls
	 * with, a refresh token to silently renew it, and when the access token
	 * expires.
	 */
	public record TokenResponse(String accessToken, String refreshToken, Instant expiresAt) {
	}

	private final String baseUrl;

	private final String clientId;

	private final String clientSecret;

	private final HttpClient httpClient;

	public OAuthLoginFlow(String baseUrl, String clientId, String clientSecret, HttpClient httpClient) {
		this.baseUrl = baseUrl;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.httpClient = httpClient;
	}

	/**
	 * The URL to open in the system browser to ask the user to log in and
	 * grant this application access.
	 */
	public String buildAuthorizeUrl() {
		return baseUrl + "/oauth/authorize?response_type=code"
				+ "&client_id=" + urlEncode(clientId)
				+ "&redirect_uri=" + urlEncode(Constants.OAUTH_REDIRECT_URI)
				+ "&scope=" + urlEncode(Constants.OAUTH_SCOPE);
	}

	/**
	 * Opens the system browser to {@link #buildAuthorizeUrl()}.
	 */
	public void openBrowserForLogin() throws Exception {
		LOGGER.info("Opening system browser for OAuth login");
		Desktop.getDesktop().browse(URI.create(buildAuthorizeUrl()));
	}

	/**
	 * Exchanges the authorization code the user read off the Hitobito page
	 * (and pasted back into the app) for an access/refresh token pair.
	 */
	public TokenResponse exchangeCode(String code) throws Exception {
		return requestToken(Map.of(
				"grant_type", "authorization_code",
				"client_id", clientId,
				"client_secret", clientSecret,
				"redirect_uri", Constants.OAUTH_REDIRECT_URI,
				"code", code));
	}

	/**
	 * Exchanges a refresh token for a new access token, without any user
	 * interaction. Per Hitobito's docs, a refresh token is valid for 7 days
	 * and can only be used once.
	 */
	public TokenResponse refresh(String refreshToken) throws Exception {
		return requestToken(Map.of(
				"grant_type", "refresh_token",
				"client_id", clientId,
				"client_secret", clientSecret,
				"refresh_token", refreshToken));
	}

	/**
	 * Revokes an access or refresh token so it can no longer be used.
	 */
	public void revoke(String token) throws Exception {
		if (token == null || token.isEmpty()) {
			return;
		}
		String body = formEncode(Map.of(
				"token", token,
				"client_id", clientId,
				"client_secret", clientSecret));
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(baseUrl + "/oauth/revoke"))
				.header("Content-Type", "application/x-www-form-urlencoded")
				.POST(HttpRequest.BodyPublishers.ofString(body))
				.build();
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() != 200) {
			throw new Exception("Token revocation failed with status " + response.statusCode() + ": " + response.body());
		}
	}

	/**
	 * Fetches the logged-in person's profile via {@code /oauth/profile}.
	 * {@code scope} must be one of the scopes the access token was granted
	 * (Hitobito requires the {@code X-Scope} header to select which of them
	 * to answer with); {@code with_roles} yields the most complete profile.
	 */
	public JSONObject fetchProfile(String accessToken, String scope) throws Exception {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(baseUrl + "/oauth/profile"))
				.header("Authorization", "Bearer " + accessToken)
				.header("X-Scope", scope)
				.GET()
				.build();
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() != 200) {
			throw new Exception("Could not fetch OAuth profile, status " + response.statusCode() + ": " + response.body());
		}
		return new JSONObject(response.body());
	}

	private TokenResponse requestToken(Map<String, String> formParams) throws Exception {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(baseUrl + "/oauth/token"))
				.header("Content-Type", "application/x-www-form-urlencoded")
				.header("Accept", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(formEncode(formParams)))
				.build();
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() != 200) {
			throw new Exception("OAuth token request failed with status " + response.statusCode() + ": " + response.body());
		}
		JSONObject json = new JSONObject(response.body());
		String accessToken = json.getString("access_token");
		String refreshToken = json.has("refresh_token") ? json.optString("refresh_token", null) : null;
		int expiresIn = json.optInt("expires_in", 7200);
		return new TokenResponse(accessToken, refreshToken, Instant.now().plusSeconds(expiresIn));
	}

	private static String formEncode(Map<String, String> formParams) {
		return formParams.entrySet().stream()
				.map(entry -> urlEncode(entry.getKey()) + "=" + urlEncode(entry.getValue()))
				.collect(Collectors.joining("&"));
	}

	private static String urlEncode(String value) {
		return URLEncoder.encode(value, StandardCharsets.UTF_8);
	}
}
