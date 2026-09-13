package ch.cevi.db.client.business.entities;

import org.json.JSONObject;

public class AdditionalMail {

	private String id;

	private Long version;

	private String email;

	private String label;

	private boolean publicVisible;

	private boolean includeInMailings;

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public boolean isIncludeInMailings() {
		return includeInMailings;
	}

	public void setIncludeInMailings(boolean includeInMailings) {
		this.includeInMailings = includeInMailings;
	}

	public Person getOwner() {
		return owner;
	}

	public void setOwner(Person owner) {
		this.owner = owner;
	}

	/**
	 * Populates this additional email from a JSON:API "additional_emails"
	 * resource object, as returned under {@code included} when a people
	 * request includes {@code additional_emails}.
	 */
	public void initFromJSONObject(JSONObject resource) {
		if (resource == null) {
			return;
		}
		id = resource.optString("id", null);
		JSONObject attributes = resource.optJSONObject("attributes");
		if (attributes != null) {
			email = attributes.optString("email", null);
			label = attributes.optString("label", null);
			publicVisible = attributes.optBoolean("public", false);
			includeInMailings = attributes.optBoolean("mailings", false);
		}
	}
}
