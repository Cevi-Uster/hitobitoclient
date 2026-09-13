package ch.cevi.db.client.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.json.JSONObject;
import org.junit.Test;

import ch.cevi.db.client.business.entities.AdditionalMail;

public class AdditionalMailTest {

	@Test
	public void testInitFromJSONObject() {
		JSONObject resource = new JSONObject(
				"{\"id\":\"1706\",\"type\":\"additional_emails\",\"attributes\":{\"email\":\"marc@mabaka.ch\",\"label\":\"Privat\",\"public\":true,\"mailings\":true}}");
		AdditionalMail additionalMail = new AdditionalMail();
		additionalMail.initFromJSONObject(resource);
		assertEquals("marc@mabaka.ch", additionalMail.getEmail());
		assertEquals("Privat", additionalMail.getLabel());
		assertTrue(additionalMail.isPublicVisible());
		assertTrue(additionalMail.isIncludeInMailings());
	}
}
