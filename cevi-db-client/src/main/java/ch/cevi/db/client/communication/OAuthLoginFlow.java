package ch.cevi.db.client.communication;

import java.awt.Desktop;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpServer;

import ch.cevi.db.client.configuration.Constants;

/**
 * Performs an OAuth2 Authorization Code + PKCE login against a Hitobito
 * instance's Doorkeeper endpoints ({@code /oauth/authorize}, {@code
 * /oauth/token}, {@code /oauth/revoke}).
 * <p>
 * The user's system browser is opened to complete the login (and any
 * second-factor step) on Hitobito's own login page; this class never sees a
 * password. The authorization code is captured by a short-lived local HTTP
 * server listening on {@link Constants#OAUTH_REDIRECT_URI}.
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

	private final HttpClient httpClient;

	public OAuthLoginFlow(String baseUrl, String clientId, HttpClient httpClient) {
		this.baseUrl = baseUrl;
		this.clientId = clientId;
		this.httpClient = httpClient;
	}

	/**
	 * Runs the full interactive login: opens the system browser, waits for the
	 * authorization redirect, and exchanges the resulting code for tokens.
	 */
	public TokenResponse login() throws Exception {
		String codeVerifier = generateCodeVerifier();
		String codeChallenge = codeChallengeS256(codeVerifier);
		String state = generateState();

		CompletableFuture<Map<String, String>> callback = new CompletableFuture<>();
		HttpServer server = startCallbackServer(callback);
		try {
			String authorizeUrl = buildAuthorizeUrl(state, codeChallenge);
			LOGGER.info("Opening system browser for OAuth login");
			LOGGER.debug("Authorize URL: " + authorizeUrl);
			Desktop.getDesktop().browse(URI.create(authorizeUrl));

			Map<String, String> params;
			try {
				params = callback.get(Constants.OAUTH_LOGIN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
			} catch (TimeoutException e) {
				throw new Exception("Timed out waiting for the browser login to complete.", e);
			}
			if (params.containsKey("error")) {
				throw new Exception("OAuth authorization failed: " + params.getOrDefault("error_description", params.get("error")));
			}
			if (!state.equals(params.get("state"))) {
				throw new Exception("OAuth state mismatch - aborting login (possible CSRF attempt).");
			}
			String code = params.get("code");
			if (code == null || code.isEmpty()) {
				throw new Exception("OAuth authorization response did not contain a code.");
			}
			return exchangeToken(Map.of(
					"grant_type", "authorization_code",
					"code", code,
					"redirect_uri", Constants.OAUTH_REDIRECT_URI,
					"client_id", clientId,
					"code_verifier", codeVerifier));
		} finally {
			server.stop(0);
		}
	}

	/**
	 * Exchanges a refresh token for a new access token, without any user
	 * interaction.
	 */
	public TokenResponse refresh(String refreshToken) throws Exception {
		return exchangeToken(Map.of(
				"grant_type", "refresh_token",
				"refresh_token", refreshToken,
				"client_id", clientId));
	}

	/**
	 * Revokes an access or refresh token so it can no longer be used.
	 */
	public void revoke(String token) throws Exception {
		if (token == null || token.isEmpty()) {
			return;
		}
		String body = "token=" + URLEncoder.encode(token, StandardCharsets.UTF_8)
				+ "&client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8);
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

	private TokenResponse exchangeToken(Map<String, String> formParams) throws Exception {
		String body = formParams.entrySet().stream()
				.map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "="
						+ URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
				.collect(Collectors.joining("&"));
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(baseUrl + "/oauth/token"))
				.header("Content-Type", "application/x-www-form-urlencoded")
				.header("Accept", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(body))
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

	private String buildAuthorizeUrl(String state, String codeChallenge) {
		String query = "response_type=code"
				+ "&client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
				+ "&redirect_uri=" + URLEncoder.encode(Constants.OAUTH_REDIRECT_URI, StandardCharsets.UTF_8)
				+ "&state=" + URLEncoder.encode(state, StandardCharsets.UTF_8)
				+ "&code_challenge=" + URLEncoder.encode(codeChallenge, StandardCharsets.UTF_8)
				+ "&code_challenge_method=S256";
		return baseUrl + "/oauth/authorize?" + query;
	}

	private HttpServer startCallbackServer(CompletableFuture<Map<String, String>> callback) throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", Constants.OAUTH_CALLBACK_PORT), 0);
		server.createContext("/callback", exchange -> {
			Map<String, String> params = parseQuery(exchange.getRequestURI().getRawQuery());
			boolean success = !params.containsKey("error");
			String html = success
					? "<html><body><h2>Login successful</h2><p>You may close this window and return to Cevi.DB Client.</p></body></html>"
					: "<html><body><h2>Login failed</h2><p>You may close this window and return to Cevi.DB Client.</p></body></html>";
			byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
			exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
			exchange.sendResponseHeaders(200, bytes.length);
			try (OutputStream body = exchange.getResponseBody()) {
				body.write(bytes);
			} finally {
				exchange.close();
			}
			callback.complete(params);
		});
		server.start();
		return server;
	}

	private static Map<String, String> parseQuery(String rawQuery) {
		Map<String, String> result = new LinkedHashMap<>();
		if (rawQuery == null || rawQuery.isEmpty()) {
			return result;
		}
		for (String pair : rawQuery.split("&")) {
			int eq = pair.indexOf('=');
			String key = eq >= 0 ? pair.substring(0, eq) : pair;
			String value = eq >= 0 ? pair.substring(eq + 1) : "";
			result.put(URLDecoder.decode(key, StandardCharsets.UTF_8), URLDecoder.decode(value, StandardCharsets.UTF_8));
		}
		return result;
	}

	private static String generateCodeVerifier() {
		byte[] bytes = new byte[64];
		new SecureRandom().nextBytes(bytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}

	private static String generateState() {
		byte[] bytes = new byte[32];
		new SecureRandom().nextBytes(bytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}

	private static String codeChallengeS256(String codeVerifier) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
		return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
	}
}
