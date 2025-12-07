package ch.cevi.db.client.business.entities;

import org.json.JSONObject;

public class SocialAccount {

	private String id;

	private Long version;

	private String name;

	private String label;

	private boolean publicVisible;

	private Person owner;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isPublicVisible() {
		return publicVisible;
	}

	public void setPublicVisible(boolean publicVisible) {
		this.publicVisible = publicVisible;
	}

	public Person getOwner() {
		return owner;
	}

	public void setOwner(Person owner) {
		this.owner = owner;
	}

	public void initFromJSONObject(JSONObject socialAccountObject) {
		if (socialAccountObject != null) {
			id = socialAccountObject.optString("id");
			name = socialAccountObject.optString("name");
			label = socialAccountObject.optString("label");
			publicVisible = socialAccountObject.optBoolean("public");
		}
	}
}
