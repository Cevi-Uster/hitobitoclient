package ch.cevi.db.client.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.json.JSONObject;
import org.junit.Test;

import ch.cevi.db.client.business.entities.Role;

public class RoleTest {

	@Test
	public void testInitFromJSONObject() {
		JSONObject jsonObject = new JSONObject(
				"{\"id\":\"11\",\"role_type\":\"Aktive/-r Kursleiter/-in\",\"label\":\"Chef\",\"created_at\":\"2014-05-15T16:22:03.000+02:00\",\"updated_at\":\"2014-05-15T16:22:03.000+02:00\",\"deleted_at\":null,\"links\":{\"group\":\"10\",\"layer_group\":\"5\"}}]");
		Role role = new Role();
		role.initFromJSONObject(jsonObject);
		assertEquals("Aktive/-r Kursleiter/-in", role.getRoleType());
		assertEquals("Chef", role.getLabel());
		assertNotNull(role.getCreatedAt());
		assertNotNull(role.getUpdatedAt());
		assertNull(role.getDeletedAt());
		assertEquals("10", role.getGroupId());
		assertEquals("5", role.getLayerGroupId());
	}
}
