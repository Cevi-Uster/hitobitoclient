package ch.cevi.db.client.business;

import static org.junit.Assert.assertEquals;

import org.json.JSONObject;
import org.junit.Test;

import ch.cevi.db.client.business.entities.Person;

public class PersonTest {

	@Test
	public void testInitFromJSONObject() {
		JSONObject jsonObject = new JSONObject(
				"{\"id\":\"513\",\"type\":\"people\",\"href\":\"http://cevi.puzzle.ch/groups/1/people/513.json\",\"first_name\":\"Marc\",\"last_name\":\"Baumgartner\",\"nickname\":\"Simba\",\"company_name\":\"\",\"company\":false,\"gender\":\"m\",\"email\":\"simba.uster@cevi.ch\",\"authentication_token\":\"7jPGGF9PbU3zvqyqMcx2\",\"last_sign_in_at\":\"2014-11-17T23:34:23.000+01:00\",\"current_sign_in_at\":\"2014-11-17T23:34:40.770+01:00\"}}");
		Person person = new Person();
		person.initFromJSONObject(jsonObject);
		assertEquals("simba.uster@cevi.ch", person.getEmail());
		assertEquals("7jPGGF9PbU3zvqyqMcx2", person.getAuthenticationToken());
	}
}
