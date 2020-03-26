package ch.cevi.db.client.communication;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SessionTest {

	ISession session;

	@Before
	public void buildSession() throws Exception {
		session = new Session("https://cevi.puzzle.ch", false);
		session.login("simba.uster@cevi.ch", "cevi14cevi");
	}

	@Test
	public void connectionStatusTest() {
		assertNotNull(session);
		assertNotNull(session.getLoggedInUser());
		assertNotNull(session.getLoggedInUser().getEmail());
		assertNotNull(session.getLoggedInUser().getAuthenticationToken());
	}

	@After
	public void closeSession() throws Exception {
		session.logout();
		assertNull(session.getLoggedInUser());
	}
}
