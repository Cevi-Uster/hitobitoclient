package ch.cevi.db.client.business.entities;

import org.json.JSONObject;

public class PhoneNumber {

	private String id;

	private Long version;

	private String number;

	private String label;

	private boolean isPublic;

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

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public Person getOwner() {
		return owner;
	}

	public void setOwner(Person owner) {
		this.owner = owner;
	}

	public void initFromJSONObject(JSONObject numberObject) {
		if (numberObject != null) {
			id = numberObject.optString("id");
			number = numberObject.optString("number");
			label = numberObject.optString("label");
			isPublic = numberObject.optBoolean("public");
		}
	}

}
