package ch.cevi.db.client.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.json.JSONObject;
import org.junit.Test;

import ch.cevi.db.client.business.entities.YGroup;

public class GroupTest {

	@Test
	public void testInitFromJSONObject() {
		JSONObject resource = new JSONObject(
				"{\"id\":\"7\",\"type\":\"groups\",\"attributes\":{\"name\":\"Cevi Alpin\",\"parent_id\":3,\"type\":\"Group::Ortsverein\",\"layer\":false,\"short_name\":\"CA\",\"email\":\"info@cevi-alpin.ch\",\"zip_code\":8500}}");
		YGroup group = new YGroup();
		group.initBasicDataFromJSONObject(resource);
		group.initDetailDataFromJSONObject(resource);
		assertEquals("7", group.getId());
		assertEquals("Cevi Alpin", group.getName());
		assertEquals("3", group.getParentYGroupId());
		assertEquals("Group::Ortsverein", group.getType());
		assertFalse(group.getIsLayer());
		assertEquals("CA", group.getShortName());
		assertEquals("info@cevi-alpin.ch", group.getEmail());
		assertEquals("8500", group.getZipCode());
		// groupType has no counterpart in the JSON:API groups attribute
		// schema (see the migration notes on YGroup#initBasicDataFromJSONObject).
		assertNull(group.getGroupType());
	}
}
