package ch.cevi.db.client.communication;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * {@link Session#login} drives a real HTTP round-trip against a live
 * Hitobito instance (Devise sign-in, possibly a second factor), so it can't
 * be exercised end-to-end in a unit test without live credentials. This
 * only covers what's observable without logging in; {@link
 * ServerFacadeTest} covers the JSON:API data-loading logic (which is where
 * almost all of the actual behavior lives) against a stub server.
 */
public class SessionTest {

	@Test
	public void isNotLoggedInBeforeLogin() {
		Session session = new Session("https://db.cevi.ch", false);
		assertFalse(session.isLoggedIn());
		assertNull(session.getLoggedInUser());
		assertNull(session.getApiClient());
		assertNull(session.getJsonApiClient());
	}
}
