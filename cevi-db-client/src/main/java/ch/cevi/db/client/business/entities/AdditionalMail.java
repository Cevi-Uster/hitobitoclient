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

	public void initFromJSONObject(JSONObject additionalMailObject) {
		if (additionalMailObject != null) {
			id = additionalMailObject.optString("id");
			email = additionalMailObject.optString("email");
			label = additionalMailObject.optString("label");
			publicVisible = additionalMailObject.optBoolean("public");
			includeInMailings = additionalMailObject.optBoolean("mailings");
		}
	}
}
