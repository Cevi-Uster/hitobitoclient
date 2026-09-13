package ch.cevi.db.client.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.json.JSONObject;
import org.junit.Test;

import ch.cevi.db.client.business.entities.PhoneNumber;

public class PhoneNumberTest {

	@Test
	public void testInitFromJSONObject() {
		JSONObject resource = new JSONObject(
				"{\"id\":\"14\",\"type\":\"phone_numbers\",\"attributes\":{\"number\":\"153-764-9526 x30624\",\"label\":\"Privat\",\"public\":true}}");
		PhoneNumber phoneNumber = new PhoneNumber();
		phoneNumber.initFromJSONObject(resource);
		assertEquals("153-764-9526 x30624", phoneNumber.getNumber());
		assertEquals("Privat", phoneNumber.getLabel());
		assertTrue(phoneNumber.getIsPublic());
	}
}
