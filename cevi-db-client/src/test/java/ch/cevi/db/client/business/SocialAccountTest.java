package ch.cevi.db.client.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.json.JSONObject;
import org.junit.Test;

import ch.cevi.db.client.business.entities.AdditionalMail;

public class SocialAccountTest {

	@Test
	public void testInitFromJSONObject() {
		JSONObject jsonObject = new JSONObject("{\"id\":\"1706\",\"email\":\"marc@mabaka.ch\",\"label\":\"Privat\",\"public\":true,\"mailings\":true}");
		AdditionalMail additionalMail = new AdditionalMail();
		additionalMail.initFromJSONObject(jsonObject);
		assertEquals("marc@mabaka.ch", additionalMail.getEmail());
		assertEquals("Privat", additionalMail.getLabel());
		assertTrue(additionalMail.isPublicVisible());
		assertTrue(additionalMail.isIncludeInMailings());
	}
}
