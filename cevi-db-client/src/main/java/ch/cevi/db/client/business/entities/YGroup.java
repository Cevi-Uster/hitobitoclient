package ch.cevi.db.client.business.entities;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.Transient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

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

	public void initBasicDataFromJSONObject(JSONObject groupObject) {
		if (groupObject != null) {
			id = groupObject.optString("id");
			name = groupObject.optString("name");
			groupType = groupObject.optString("group_type");
			parentYGroupId = groupObject.optString("parent");
		}
	}

	public void initDetailDataFromJSONObject(JSONObject groupObject) {
		if (groupObject != null) {
			type = groupObject.optString("type");
			isLayer = groupObject.optBoolean("layer");
			shortName = groupObject.optString("short_name");
			email = groupObject.optString("email");
			address = groupObject.optString("address");
			zipCode = groupObject.optString("zip_code");
			town = groupObject.optString("town");
			country = groupObject.optString("country");
			String foundingDateString = groupObject.optString("founding_date");
			if (foundingDateString != null && !foundingDateString.isEmpty()) {
				foundingDate = DatatypeConverter.parseDateTime(foundingDateString).getTime();
			}
			String createdAtString = groupObject.optString("created_at");
			if (createdAtString != null && !createdAtString.isEmpty()) {
				createdAt = DatatypeConverter.parseDateTime(createdAtString).getTime();
			}
			String updatedAtString = groupObject.optString("updated_at");
			if (updatedAtString != null && !updatedAtString.isEmpty()) {
				updatedAt = DatatypeConverter.parseDateTime(updatedAtString).getTime();
			}
			String deletedAtString = groupObject.optString("deleted_at");
			if (deletedAtString != null && !deletedAtString.isEmpty()) {
				deletedAt = DatatypeConverter.parseDateTime(deletedAtString).getTime();
			}
		}
	}

	public void initFromOtherGroup(YGroup otherGroup) {
		if (otherGroup != this) {
			this.id = otherGroup.id;
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
			this.type = otherGroup.type;
			this.parentYGroupId = otherGroup.parentYGroupId;
			this.children.clear();
			this.children.addAll(otherGroup.children);
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

}
