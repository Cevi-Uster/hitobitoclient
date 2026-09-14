package ch.cevi.db.client.communication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import ch.cevi.db.client.api.invoker.ApiClient;
import ch.cevi.db.client.business.entities.Person;
import ch.cevi.db.client.business.entities.Role;
import ch.cevi.db.client.business.entities.YGroup;

/**
 * Exercises {@link ServerFacade}'s JSON:API group/role/person loading and
 * graph-wiring logic against a local HTTP stub serving canned JSON:API
 * fixtures, rather than the real db.cevi.ch/cevi.puzzle.ch instances the old
 * (username+password) version of this suite logged into live. That login no
 * longer exists -- see {@link SessionTest} -- and a stub server is a better
 * fit anyway: it runs offline, needs no credentials, and pins down exactly
 * the group/child/role/person wiring this class is responsible for.
 */
public class ServerFacadeTest {

	private HttpServer stubServer;

	private ServerFacade serverFacade;

	@Before
	public void startStubServerAndFacade() throws IOException {
		stubServer = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
		stubServer.createContext("/api/groups/1", exchange -> respondJson(exchange, GROUP_1));
		stubServer.createContext("/api/groups", exchange -> respondJson(exchange,
				exchange.getRequestURI().getQuery() != null && exchange.getRequestURI().getQuery().contains("parent_id")
						? CHILD_GROUPS_OF_1
						: EMPTY_COLLECTION));
		stubServer.createContext("/api/roles", exchange -> respondJson(exchange, ROLES_OF_GROUP_1));
		stubServer.createContext("/api/people", exchange -> respondJson(exchange, PERSON_513_WITH_PHONE_NUMBER));
		stubServer.start();

		String baseUrl = "http://127.0.0.1:" + stubServer.getAddress().getPort();
		ApiClient apiClient = new ApiClient();
		apiClient.setBasePath(baseUrl + "/api");
		JsonApiClient jsonApiClient = new JsonApiClient(apiClient, HttpClient.newHttpClient(), requestBuilder -> { });
		serverFacade = new ServerFacade(new StubSession(baseUrl, jsonApiClient));
	}

	@After
	public void stopStubServer() {
		stubServer.stop(0);
	}

	@Test
	public void loadGroupWithDetailsWiresChildrenRolesAndPeople() throws Exception {
		YGroup group = serverFacade.loadGroupWithDetails("1", null);

		assertNotNull(group);
		assertEquals("Cevi Alpin", group.getName());

		assertEquals(1, group.getChildren().size());
		assertEquals("7", group.getChildren().get(0).getId());

		assertEquals(1, group.getGroupRoles().size());
		Role role = group.getGroupRoles().get(0);
		assertEquals("Leiter", role.getRoleType());
		assertNotNull(role.getPerson());
		assertEquals("513", role.getPerson().getId());

		List<Person> members = group.getGroupMembers();
		assertEquals(1, members.size());
		Person person = members.get(0);
		assertEquals("Marc", person.getFirstName());
		assertEquals("Baumgartner", person.getLastName());

		assertNotNull(person.getPhoneNumbers());
		assertEquals(1, person.getPhoneNumbers().size());
		assertEquals("Privat", person.getPhoneNumbers().get(0).getLabel());

		// role<->person wiring is bidirectional and uses the same instances
		assertNotNull(person.getRoles());
		assertEquals(1, person.getRoles().size());
		assertSame(role, person.getRoles().get(0));
	}

	@Test
	public void loadPersonListReturnsGroupMembers() throws Exception {
		List<Person> persons = serverFacade.loadPersonList("1");
		assertEquals(1, persons.size());
		assertEquals("513", persons.get(0).getId());
	}

	private static void respondJson(HttpExchange exchange, String body) throws IOException {
		byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
		exchange.getResponseHeaders().set("Content-Type", "application/vnd.api+json");
		exchange.sendResponseHeaders(200, bytes.length);
		try (OutputStream responseBody = exchange.getResponseBody()) {
			responseBody.write(bytes);
		}
	}

	private static final String GROUP_1 = "{\"data\":{\"id\":\"1\",\"type\":\"groups\","
			+ "\"attributes\":{\"name\":\"Cevi Alpin\",\"type\":\"Group::Dachverband\",\"layer\":true}}}";

	private static final String CHILD_GROUPS_OF_1 = "{\"data\":[{\"id\":\"7\",\"type\":\"groups\","
			+ "\"attributes\":{\"name\":\"Ortsverein\",\"parent_id\":1}}]}";

	private static final String EMPTY_COLLECTION = "{\"data\":[]}";

	private static final String ROLES_OF_GROUP_1 = "{\"data\":[{\"id\":\"100\",\"type\":\"roles\","
			+ "\"attributes\":{\"name\":\"Leiter\",\"type\":\"Group::Vorstand::Leiter\",\"person_id\":513,\"group_id\":1,"
			+ "\"created_at\":\"2014-05-15T16:22:03.000+02:00\"}}]}";

	private static final String PERSON_513_WITH_PHONE_NUMBER = "{\"data\":[{\"id\":\"513\",\"type\":\"people\","
			+ "\"attributes\":{\"first_name\":\"Marc\",\"last_name\":\"Baumgartner\",\"email\":\"simba.uster@cevi.ch\"},"
			+ "\"relationships\":{\"phone_numbers\":{\"data\":[{\"type\":\"phone_numbers\",\"id\":\"14\"}]}}}],"
			+ "\"included\":[{\"id\":\"14\",\"type\":\"phone_numbers\","
			+ "\"attributes\":{\"number\":\"0791234567\",\"label\":\"Privat\",\"public\":true}}]}";

	/**
	 * Minimal {@link ISession} that skips OAuth entirely and just hands the
	 * facade a {@link JsonApiClient} pointed at the stub server.
	 */
	private static class StubSession implements ISession {

		private final String baseUrl;

		private final JsonApiClient jsonApiClient;

		StubSession(String baseUrl, JsonApiClient jsonApiClient) {
			this.baseUrl = baseUrl;
			this.jsonApiClient = jsonApiClient;
		}

		@Override
		public void login(String email, String password, String secondFactorCode) {
		}

		@Override
		public void logout() {
		}

		@Override
		public Person getLoggedInUser() {
			return null;
		}

		@Override
		public String getBaseUrl() {
			return baseUrl;
		}

		@Override
		public boolean isLoggedIn() {
			return true;
		}

		@Override
		public String getMainGroupId() {
			return "1";
		}

		@Override
		public ApiClient getApiClient() {
			return null;
		}

		@Override
		public JsonApiClient getJsonApiClient() {
			return jsonApiClient;
		}
	}
}
