package ch.cevi.db.client.business.entities;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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

	/**
	 * Populates this role from a JSON:API "roles" resource object ({@code
	 * {"id": ..., "attributes": {"name": ..., "type": ..., "label": ...,
	 * "person_id": ..., "group_id": ..., "start_on": ..., "end_on": ...,
	 * "created_at": ..., "updated_at": ...}}}).
	 * <p>
	 * {@code roleType} is mapped from the human-readable {@code name}
	 * attribute (e.g. "Gruppenleiter/-in"), not the Rails class in {@code
	 * type} (e.g. "Group::Jungschar::Gruppenleiter") -- {@link
	 * ch.cevi.db.client.business.RoleComparator} matches against the former.
	 * <p>
	 * Roles have no {@code deleted_at} in the new API; the closest analog is
	 * {@code end_on}, which is only mapped into {@link #deletedAt} once it is
	 * in the past, so {@code getDeletedAt() == null} keeps meaning "this role
	 * is currently active" for every existing caller of that check.
	 */
	public void initFromJSONObject(JSONObject resource) {
		if (resource == null) {
			return;
		}
		id = resource.optString("id", null);
		JSONObject attributes = resource.optJSONObject("attributes");
		if (attributes != null) {
			roleType = attributes.optString("name", null);
			label = attributes.optString("label", null);
			groupId = attributes.optString("group_id", null);
			String createdAtString = attributes.optString("created_at", null);
			if (createdAtString != null && !createdAtString.isEmpty()) {
				createdAt = Date.from(OffsetDateTime.parse(createdAtString).toInstant());
			}
			String updatedAtString = attributes.optString("updated_at", null);
			if (updatedAtString != null && !updatedAtString.isEmpty()) {
				updatedAt = Date.from(OffsetDateTime.parse(updatedAtString).toInstant());
			}
			String endOnString = attributes.optString("end_on", null);
			if (endOnString != null && !endOnString.isEmpty()) {
				LocalDate endOn = LocalDate.parse(endOnString);
				if (!endOn.isAfter(LocalDate.now())) {
					deletedAt = Date.from(endOn.atStartOfDay(ZoneId.systemDefault()).toInstant());
				}
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
