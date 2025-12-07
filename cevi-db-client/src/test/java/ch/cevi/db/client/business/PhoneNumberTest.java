package ch.cevi.db.client.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.json.JSONObject;
import org.junit.Test;

import ch.cevi.db.client.business.entities.PhoneNumber;

public class PhoneNumberTest {

	@Test
	public void testInitFromJSONObject() {
		JSONObject jsonObject = new JSONObject("{\"id\":\"14\",\"number\":\"153-764-9526 x30624\",\"label\":\"Privat\",\"public\":true}");
		PhoneNumber phoneNumber = new PhoneNumber();
		phoneNumber.initFromJSONObject(jsonObject);
		assertEquals("153-764-9526 x30624", phoneNumber.getNumber());
		assertEquals("Privat", phoneNumber.getLabel());
		assertTrue(phoneNumber.getIsPublic());
	}
}
