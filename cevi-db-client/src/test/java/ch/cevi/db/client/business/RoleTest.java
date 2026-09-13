package ch.cevi.db.client.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.json.JSONObject;
import org.junit.Test;

import ch.cevi.db.client.business.entities.Role;

public class RoleTest {

	@Test
	public void testInitFromJSONObjectActiveRole() {
		JSONObject resource = new JSONObject(
				"{\"id\":\"11\",\"type\":\"roles\",\"attributes\":{\"name\":\"Aktive/-r Kursleiter/-in\",\"type\":\"Group::Kurs::Kursleiter\",\"label\":\"Chef\",\"group_id\":10,\"created_at\":\"2014-05-15T16:22:03.000+02:00\",\"updated_at\":\"2014-05-15T16:22:03.000+02:00\"}}");
		Role role = new Role();
		role.initFromJSONObject(resource);
		assertEquals("Aktive/-r Kursleiter/-in", role.getRoleType());
		assertEquals("Chef", role.getLabel());
		assertNotNull(role.getCreatedAt());
		assertNotNull(role.getUpdatedAt());
		assertEquals("10", role.getGroupId());
		// no deleted_at in the new API; getDeletedAt() == null means "active"
		assertNull(role.getDeletedAt());
	}

	@Test
	public void testInitFromJSONObjectEndedRoleIsTreatedAsDeleted() {
		JSONObject resource = new JSONObject(
				"{\"id\":\"12\",\"type\":\"roles\",\"attributes\":{\"name\":\"Leiter\",\"group_id\":10,\"end_on\":\"2020-01-01\"}}");
		Role role = new Role();
		role.initFromJSONObject(resource);
		assertNotNull(role.getDeletedAt());
	}
}
