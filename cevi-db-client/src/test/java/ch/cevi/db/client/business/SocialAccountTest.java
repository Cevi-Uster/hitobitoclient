package ch.cevi.db.client.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.json.JSONObject;
import org.junit.Test;

import ch.cevi.db.client.business.entities.SocialAccount;

public class SocialAccountTest {

	@Test
	public void testInitFromJSONObject() {
		JSONObject resource = new JSONObject(
				"{\"id\":\"1674\",\"type\":\"social_accounts\",\"attributes\":{\"name\":\"mbaumgar\",\"label\":\"Skype\",\"public\":true}}");
		SocialAccount socialAccount = new SocialAccount();
		socialAccount.initFromJSONObject(resource);
		assertEquals("mbaumgar", socialAccount.getName());
		assertEquals("Skype", socialAccount.getLabel());
		assertTrue(socialAccount.isPublicVisible());
	}
}
