package ch.cevi.db.client.business;

import static org.junit.Assert.assertEquals;

import org.json.JSONObject;
import org.junit.Test;

import ch.cevi.db.client.business.entities.YGroup;

public class GroupTest {

	@Test
	public void testInitFromJSONObject() {
		JSONObject jsonObject = new JSONObject("{\"id\":\"7\",\"name\":\"Cevi Alpin\",\"group_type\":\"Mitgliederorganisation\"},");
		YGroup groupe = new YGroup();
		groupe.initBasicDataFromJSONObject(jsonObject);
		assertEquals("7", groupe.getId());
		assertEquals("Cevi Alpin", groupe.getName());
		assertEquals("Mitgliederorganisation", groupe.getGroupType());
	}
}
