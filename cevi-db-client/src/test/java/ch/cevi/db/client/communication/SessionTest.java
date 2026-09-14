package ch.cevi.db.client.communication;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * {@link Session#login()} now drives an interactive OAuth2 Authorization
 * Code + PKCE flow (see {@link OAuthLoginFlow}): it opens a system browser
 * and waits for a human to complete the login, so it can no longer be
 * exercised end-to-end in a unit test the way the old username/password
 * login could. This only covers what's observable without logging in;
 * {@link ServerFacadeTest} covers the JSON:API data-loading logic (which is
 * where almost all of the actual behavior lives) against a stub server.
 */
public class SessionTest {

	@Test
	public void isNotLoggedInBeforeLogin() {
		Session session = new Session("https://db.cevi.ch", "unused-test-client-id", "unused-test-client-secret", false);
		assertFalse(session.isLoggedIn());
		assertNull(session.getLoggedInUser());
		assertNull(session.getApiClient());
		assertNull(session.getJsonApiClient());
	}
}
