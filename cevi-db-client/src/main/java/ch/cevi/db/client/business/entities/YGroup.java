package ch.cevi.db.client.business.entities;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

public class YGroup {

	public static final String ID_PROPERTY = "id";

	public static final String NAME_PROPERTY = "name";

	public static final String GROUP_TYPE_PROPERTY = "groupType";

	public static final String IS_LAYER_PROPERY = "isLayer";

	public static final String TYPE_PROPERY = "type";

	public static enum GroupType {
		Ortsgruppe
	};

	private String id;

	private Long version;

	private String parentYGroupId;

	private String name;

	private String shortName;

	private String email;

	private String address;

	private String zipCode;

	private String town;

	private String country;

	private Date foundingDate;

	private Date createdAt;

	private Date updatedAt;

	private Date deletedAt;

	private String groupType;

	private boolean isLayer;

	private String layerGroupId;

	private String type;

	private List<Role> groupRoles;

	private YGroup parentYGroup;

	private List<YGroup> children;

	boolean isFullyLoaded;

	private PropertyChangeSupport propertyChangeSupport;

	public YGroup() {
		this.propertyChangeSupport = new PropertyChangeSupport(this);
		this.children = new ArrayList<YGroup>();
		this.groupRoles = new ArrayList<Role>();
		isFullyLoaded = false;
	}

	public List<YGroup> getChildren() {
		return children;
	}

	public void addChild(YGroup childGroup) {
		this.children.add(childGroup);
	}

	public void addChildren(List<YGroup> children) {
		this.children.addAll(children);
	}

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

	public String getParentYGroupId() {
		return parentYGroupId;
	}

	public void setParentGroupId(String parentYGroupId) {
		this.parentYGroupId = parentYGroupId;
	}

	public YGroup getParentYGroup() {
		return parentYGroup;
	}

	public void setParentYGroup(YGroup parentGroup) {
		this.parentYGroup = parentGroup;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zIPCode) {
		zipCode = zIPCode;
	}

	public String getTown() {
		return town;
	}

	public void setTown(String town) {
		this.town = town;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Date getFoundingDate() {
		return foundingDate;
	}

	public void setFoundingDate(Date foundingDate) {
		this.foundingDate = foundingDate;
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

	public String getGroupType() {
		return groupType;
	}

	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}

	public boolean isFullyLoaded() {
		return isFullyLoaded;
	}

	public void setFullyLoaded(boolean isFullyLoaded) {
		this.isFullyLoaded = isFullyLoaded;
	}

	public boolean getIsLayer() {
		return isLayer;
	}

	public void setIsLayer(boolean isLayer) {
		this.isLayer = isLayer;
	}

	public String getLayerGroupId() {
		return layerGroupId;
	}

	public void setLayerGroupId(String layerGroupId) {
		this.layerGroupId = layerGroupId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Role> getGroupRoles() {
		return groupRoles;
	}

	public void setGroupRoles(List<Role> groupRoles) {
		this.groupRoles = groupRoles;
	}

	public List<Person> getGroupMembers() {
		List<Person> groupMembers = new ArrayList<Person>();
		for (Role role : getGroupRoles()) {
			groupMembers.add(role.getPerson());
		}
		return groupMembers;
	}

	/**
	 * Populates the identity fields of this group from a JSON:API "groups"
	 * resource object. {@code groupType} and {@code foundingDate} have no
	 * counterpart in the JSON:API {@code groups} attribute schema and are
	 * left unset -- see the migration notes on {@link Role#getRoleType()}
	 * for why {@code groupType} matters (it feeds {@link
	 * Role#getFullyQuallifiedRoleName()} and {@link
	 * ch.cevi.db.client.business.RoleComparator}'s sort order).
	 */
	public void initBasicDataFromJSONObject(JSONObject resource) {
		if (resource == null) {
			return;
		}
		id = resource.optString("id", null);
		JSONObject attributes = resource.optJSONObject("attributes");
		if (attributes != null) {
			name = attributes.optString("name", null);
			parentYGroupId = attributes.optString("parent_id", null);
		}
	}

	public void initDetailDataFromJSONObject(JSONObject resource) {
		if (resource == null) {
			return;
		}
		JSONObject attributes = resource.optJSONObject("attributes");
		if (attributes != null) {
			type = attributes.optString("type", null);
			isLayer = attributes.optBoolean("layer", false);
			shortName = attributes.optString("short_name", null);
			email = attributes.optString("email", null);
			address = attributes.optString("address", null);
			zipCode = attributes.optString("zip_code", null);
			town = attributes.optString("town", null);
			country = attributes.optString("country", null);
			layerGroupId = attributes.optString("layer_group_id", null);
			createdAt = parseOffsetDateTime(attributes.optString("created_at", null));
			updatedAt = parseOffsetDateTime(attributes.optString("updated_at", null));
			deletedAt = parseOffsetDateTime(attributes.optString("deleted_at", null));
		}
	}

	private static Date parseOffsetDateTime(String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}
		return Date.from(OffsetDateTime.parse(value).toInstant());
	}

	public void initFromOtherGroup(YGroup otherGroup) {
		if (otherGroup != this) {
			this.id = otherGroup.id;
			this.version = otherGroup.version;
			this.name = otherGroup.name;
			this.shortName = otherGroup.shortName;
			this.email = otherGroup.email;
			this.address = otherGroup.address;
			this.zipCode = otherGroup.zipCode;
			this.town = otherGroup.town;
			this.country = otherGroup.country;
			this.foundingDate = otherGroup.foundingDate;
			this.createdAt = otherGroup.createdAt;
			this.updatedAt = otherGroup.updatedAt;
			this.deletedAt = otherGroup.deletedAt;
			this.groupType = otherGroup.groupType;
			this.isLayer = otherGroup.isLayer;
			this.layerGroupId = otherGroup.layerGroupId;
			this.type = otherGroup.type;
			this.parentYGroupId = otherGroup.parentYGroupId;
			this.parentYGroup = otherGroup.parentYGroup;
			this.children.clear();
			this.children.addAll(otherGroup.children);
			this.groupRoles.clear();
			this.groupRoles.addAll(otherGroup.getGroupRoles());
			this.isFullyLoaded = otherGroup.isFullyLoaded;
		}
	}

	@Override
	public String toString() {
		return "Group [id=" + id + ", name=" + name + ", groupType=" + groupType + ", isLayer=" + isLayer + ", type=" + type + ", isFullyLoaded=" + isFullyLoaded + "]";
	}

	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		this.propertyChangeSupport.addPropertyChangeListener(pcl);
	}

	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		this.propertyChangeSupport.removePropertyChangeListener(pcl);
	}

	public void setChildren(List<YGroup> children) {
		this.children = children;
	}

}
