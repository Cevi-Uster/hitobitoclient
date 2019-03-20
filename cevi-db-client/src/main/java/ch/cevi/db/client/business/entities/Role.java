package ch.cevi.db.client.business.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.json.JSONObject;

public class Role {

	private String id;

	private Long version;

	private String roleType;

	private String label;

	private Date createdAt;

	private Date updatedAt;

	private Date deletedAt;

	private String groupId;

	private String layerGroupId;

	private Person person;

	private YGroup yGroup;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Date getDeletedAt() {
		return deletedAt;
	}

	public void setDeletedAt(Date deletedAt) {
		this.deletedAt = deletedAt;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getLayerGroupId() {
		return layerGroupId;
	}

	public void setLayerGroupId(String layerGroupId) {
		this.layerGroupId = layerGroupId;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public YGroup getyGroup() {
		return yGroup;
	}

	public void setyGroup(YGroup yGroup) {
		this.yGroup = yGroup;
	}

	public void initFromJSONObject(JSONObject roleObject) {
		if (roleObject != null) {
			id = roleObject.optString("id");
			roleType = roleObject.optString("role_type");
			label = roleObject.optString("label");
			String createdAtString = roleObject.optString("created_at");
			if (createdAtString != null && !createdAtString.isEmpty()) {
				createdAt = DatatypeConverter.parseDateTime(createdAtString).getTime();
			}
			String updatedAtString = roleObject.optString("updated_at");
			if (updatedAtString != null && !updatedAtString.isEmpty()) {
				updatedAt = DatatypeConverter.parseDateTime(updatedAtString).getTime();
			}
			String deletedAtString = roleObject.optString("deleted_at");
			if (deletedAtString != null && !deletedAtString.isEmpty()) {
				deletedAt = DatatypeConverter.parseDateTime(deletedAtString).getTime();
			}

			JSONObject linksObject = roleObject.optJSONObject("links");
			if (linksObject != null) {
				groupId = linksObject.optString("group");
				layerGroupId = linksObject.optString("layer_group");
			}
		}
	}

	public String getFullyQuallifiedRoleName() {
		StringBuilder fullyQuallifiedRoleName = new StringBuilder();
		List<YGroup> groups = new ArrayList<YGroup>();
		if (yGroup != null) {
			YGroup currentGroup = yGroup;
			while (currentGroup != null) {
				groups.add(currentGroup);
				if (currentGroup.getIsLayer()) {
					break;
				}
				currentGroup = currentGroup.getParentYGroup();
			}
			Collections.reverse(groups);
			for (YGroup group : groups) {
				if (fullyQuallifiedRoleName.length() > 0) {
					fullyQuallifiedRoleName.append(".");
				}
				fullyQuallifiedRoleName.append(group.getGroupType());
			}
			if (fullyQuallifiedRoleName.length() > 0) {
				fullyQuallifiedRoleName.append(".");
			}
			fullyQuallifiedRoleName.append(this.getRoleType());
		}
		return fullyQuallifiedRoleName.toString();
	}
}
