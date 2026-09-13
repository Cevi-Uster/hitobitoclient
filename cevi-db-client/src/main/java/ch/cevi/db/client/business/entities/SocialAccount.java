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

	/**
	 * Populates this social account from a JSON:API "social_accounts"
	 * resource object, as returned under {@code included} when a people
	 * request includes {@code social_accounts}.
	 */
	public void initFromJSONObject(JSONObject resource) {
		if (resource == null) {
			return;
		}
		id = resource.optString("id", null);
		JSONObject attributes = resource.optJSONObject("attributes");
		if (attributes != null) {
			name = attributes.optString("name", null);
			label = attributes.optString("label", null);
			publicVisible = attributes.optBoolean("public", false);
		}
	}
}
