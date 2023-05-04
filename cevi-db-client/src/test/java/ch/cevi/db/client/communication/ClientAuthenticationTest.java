package ch.cevi.db.client.communication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * A simple example that uses HttpClient to execute an HTTP request against a
 * target site that requires user authentication.
 */
public class ClientAuthenticationTest {
	
private static final String DB_USER;
	
	private static final String DB_PASSWORD;
	
	static {
		DB_USER = System.getenv("CEVI_DB_USER");
		DB_PASSWORD = System.getenv("CEVI_DB_PASSWORD");
	}

	@Test
	public void signInTest() {
		try {

			Client client = Client.create();

			WebResource webResource = client.resource("https://cevi.puzzle.ch/users/sign_in.json?person[email]="+DB_USER+"&person[password]="+DB_PASSWORD);
			ClientResponse response = webResource.accept("application/json").post(ClientResponse.class);

			if (response.getStatus() != 200) {
				fail("Got error from server, code: " + response.getStatus());
			}

			String output = response.getEntity(String.class);

			System.out.println("Output from Server .... \n");
			System.out.println(output);

			// Try to get authentication token from output
			JSONObject jsonObject = new JSONObject(output);
			JSONArray peopleArray = jsonObject.optJSONArray("people");

			assertNotNull(peopleArray);

			assertEquals(1, peopleArray.length());

			JSONObject personObject = peopleArray.optJSONObject(0);

			assertNotNull(personObject);

			String token = personObject.getString("authentication_token");

			assertNotNull(token);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception catched: " + e);
		}
	}
}
