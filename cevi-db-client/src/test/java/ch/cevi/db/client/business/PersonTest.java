package ch.cevi.db.client.business;

import static org.junit.Assert.assertEquals;

import org.json.JSONObject;
import org.junit.Test;

import ch.cevi.db.client.business.entities.Person;

public class PersonTest {

	@Test
	public void testInitFromJSONObject() {
		JSONObject resource = new JSONObject(
				"{\"id\":\"513\",\"type\":\"people\",\"attributes\":{\"first_name\":\"Marc\",\"last_name\":\"Baumgartner\",\"nickname\":\"Simba\",\"company\":false,\"gender\":\"m\",\"email\":\"simba.uster@cevi.ch\"}}");
		Person person = new Person();
		person.initFromJSONObject(resource);
		assertEquals("513", person.getId());
		assertEquals("Marc", person.getFirstName());
		assertEquals("Baumgartner", person.getLastName());
		assertEquals("simba.uster@cevi.ch", person.getEmail());
	}
}
