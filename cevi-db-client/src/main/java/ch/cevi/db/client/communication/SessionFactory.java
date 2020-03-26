package ch.cevi.db.client.communication;

import ch.cevi.db.client.configuration.Configuration;

public class SessionFactory {

	public static ISession createSession(Configuration configuration) {
		return new Session2(configuration.getBaseUrl().toString(), configuration.getUsePrimaryGroupAsRoot());
	}
	
}
