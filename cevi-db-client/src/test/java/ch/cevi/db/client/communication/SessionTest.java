package ch.cevi.db.client.communication;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SessionTest {

private static final String DB_USER;
	
	private static final String DB_PASSWORD;
	
	static {
		DB_USER = System.getenv("CEVI_DB_USER");
		DB_PASSWORD = System.getenv("CEVI_DB_PASSWORD");
	}
	
	ISession session;

	@Before
	public void buildSession() throws Exception {
		session = new Session("https://cevi.puzzle.ch", false);
		session.login(DB_USER, DB_PASSWORD);
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
