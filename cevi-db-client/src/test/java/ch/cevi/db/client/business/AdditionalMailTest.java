package ch.cevi.db.client.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.json.JSONObject;
import org.junit.Test;

import ch.cevi.db.client.business.entities.SocialAccount;

public class AdditionalMailTest {

	@Test
	public void testInitFromJSONObject() {
		JSONObject jsonObject = new JSONObject("{\"id\":\"1674\",\"name\":\"mbaumgar\",\"label\":\"Skype\",\"public\":true}");
		SocialAccount socialAccount = new SocialAccount();
		socialAccount.initFromJSONObject(jsonObject);
		assertEquals("mbaumgar", socialAccount.getName());
		assertEquals("Skype", socialAccount.getLabel());
		assertTrue(socialAccount.isPublicVisible());
	}
}
