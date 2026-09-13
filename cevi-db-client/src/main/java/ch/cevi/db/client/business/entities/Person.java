package ch.cevi.db.client.business.entities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import ch.cevi.db.client.communication.JsonApiClient;

public class Person {

	private static final SimpleDateFormat JSON_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	private String id;

	private Long version;

	private String type;

	private String href;

	private String firstName;

	private String lastName;

	private String nickname;

	private String companyName;

	private boolean isCompany;

	private String gender;

	private String email;

	private String address;

	private String zipCode;

	private String town;

	private String country;

	private String lastSignInAt;

	private String currentSignInAt;

	private String localDepartment;

	private String additionalInformation;

	private String pictureUrl;

	private String salutationParents;

	private String nameParents;

	private String canton;

	private String jsNumber;

	private String nationalityJS;

	private String ahvNumber;

	private String title;

	private String profession;

	private Date joined;

	private String ahvNumberOld;

	private String memberCardNumber;

	private String nationality;

	private String salutation;

	private String correspondenceLanguage;

	private String confession;

	private Date birthday;

	private String primaryGroupId;

	private List<PhoneNumber> phoneNumbers;

	private List<Role> roles;

	private List<AdditionalMail> additionalMails;

	private List<SocialAccount> socialAccounts;

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public boolean isCompany() {
		return isCompany;
	}

	public void setCompany(boolean isCompany) {
		this.isCompany = isCompany;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
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

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
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

	public String getLastSignInAt() {
		return lastSignInAt;
	}

	public void setLastSignInAt(String lastSignInAt) {
		this.lastSignInAt = lastSignInAt;
	}

	public String getCurrentSignInAt() {
		return currentSignInAt;
	}

	public void setCurrentSignInAt(String currentSignInAt) {
		this.currentSignInAt = currentSignInAt;
	}

	public String getAdditionalInformation() {
		return additionalInformation;
	}

	public void setAdditionalInformation(String additionalInformation) {
		this.additionalInformation = additionalInformation;
	}

	public String getLocalDepartment() {
		return localDepartment;
	}

	public void setLocalDepartment(String localDepartment) {
		this.localDepartment = localDepartment;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}

	public String getSalutationParents() {
		return salutationParents;
	}

	public void setSalutationParents(String salutationParents) {
		this.salutationParents = salutationParents;
	}

	public String getNameParents() {
		return nameParents;
	}

	public void setNameParents(String nameParents) {
		this.nameParents = nameParents;
	}

	public String getCanton() {
		return canton;
	}

	public void setCanton(String canton) {
		this.canton = canton;
	}

	public String getJsNumber() {
		return jsNumber;
	}

	public void setJsNumber(String jsNumber) {
		this.jsNumber = jsNumber;
	}

	public String getNationalityJS() {
		return nationalityJS;
	}

	public void setNationalityJS(String nationalityJS) {
		this.nationalityJS = nationalityJS;
	}

	public String getAhvNumber() {
		return ahvNumber;
	}

	public void setAhvNumber(String ahvNumber) {
		this.ahvNumber = ahvNumber;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public Date getJoined() {
		return joined;
	}

	public void setJoined(Date joined) {
		this.joined = joined;
	}

	public String getAhvNumberOld() {
		return ahvNumberOld;
	}

	public void setAhvNumberOld(String ahvNumberOld) {
		this.ahvNumberOld = ahvNumberOld;
	}

	public String getMemberCardNumber() {
		return memberCardNumber;
	}

	public void setMemberCardNumber(String memberCardNumber) {
		this.memberCardNumber = memberCardNumber;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getSalutation() {
		return salutation;
	}

	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}

	public String getCorrespondenceLanguage() {
		return correspondenceLanguage;
	}

	public void setCorrespondenceLanguage(String correspondenceLanguage) {
		this.correspondenceLanguage = correspondenceLanguage;
	}

	public String getConfession() {
		return confession;
	}

	public void setConfession(String confession) {
		this.confession = confession;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public List<PhoneNumber> getPhoneNumbers() {
		return phoneNumbers;
	}

	public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public List<AdditionalMail> getAdditionalMails() {
		return additionalMails;
	}

	public void setAdditionalMails(List<AdditionalMail> additionalMails) {
		this.additionalMails = additionalMails;
	}

	public List<SocialAccount> getSocialAccounts() {
		return socialAccounts;
	}

	public void setSocialAccounts(List<SocialAccount> socialAccounts) {
		this.socialAccounts = socialAccounts;
	}

	public String getPrimaryGroupId() {
		return primaryGroupId;
	}

	public void setPrimaryGroupId(String primaryGroupId) {
		this.primaryGroupId = primaryGroupId;
	}

	/**
	 * Populates this person from a JSON:API "people" resource object
	 * ({@code {"id": ..., "type": "people", "attributes": {...}}}), as
	 * returned under {@code data} or {@code included} by the new Hitobito
	 * JSON:API.
	 * <p>
	 * The people attribute schema does not carry every field the legacy API
	 * exposed: {@code salutation}, {@code title}, {@code canton}, {@code
	 * salutation_parents}, {@code name_parents}, {@code j_s_number}, {@code
	 * nationality_j_s}, {@code ahv_number}, {@code ahv_number_old}, {@code
	 * profession}, {@code joined}, {@code member_card_number}, {@code
	 * nationality}, {@code correspondence_language}, {@code confession} and
	 * {@code ortsgruppe} (local department) have no counterpart in the
	 * published spec as of this writing and are left unset here; see the
	 * migration notes for how to verify whether the live API actually
	 * returns them regardless (Cevi-specific "wagon" attributes sometimes
	 * exist beyond a vendored spec).
	 */
	public void initFromJSONObject(JSONObject resource) {
		if (resource == null) {
			return;
		}
		id = resource.optString("id", null);
		type = resource.optString("type", null);
		JSONObject attributes = resource.optJSONObject("attributes");
		if (attributes != null) {
			firstName = attributes.optString("first_name", null);
			lastName = attributes.optString("last_name", null);
			nickname = attributes.optString("nickname", null);
			companyName = attributes.optString("company_name", null);
			isCompany = attributes.optBoolean("company", false);
			email = attributes.optString("email", null);
			address = attributes.optString("address", null);
			zipCode = attributes.optString("zip_code", null);
			town = attributes.optString("town", null);
			country = attributes.optString("country", null);
			gender = attributes.optString("gender", null);
			additionalInformation = attributes.optString("additional_information", null);
			pictureUrl = attributes.optString("picture", null);
			primaryGroupId = attributes.optString("primary_group_id", null);
			String birthdayString = attributes.optString("birthday", null);
			if (birthdayString != null && !birthdayString.isEmpty()) {
				try {
					birthday = JSON_DATE_FORMAT.parse(birthdayString);
				} catch (ParseException pe) {
					// ignore
				}
			}
		}
	}

	/**
	 * Like {@link #initFromJSONObject(JSONObject)}, but also populates phone
	 * numbers, social accounts and additional emails from the given
	 * resource's {@code relationships}, resolved against an included-resource
	 * index (see {@link JsonApiClient#indexIncluded}) -- i.e. the caller must
	 * have fetched this person with {@code include=phone_numbers,
	 * social_accounts,additional_emails}.
	 * <p>
	 * Roles are deliberately not populated here: group membership is loaded
	 * group-first (one {@code /roles?filter[group_id][eq]=...} call covers a
	 * whole group), and {@link #addRole(Role)} is used to attach each
	 * resulting {@link Role} to the same {@link Person} instance handed back
	 * by this method, rather than fetching and re-instantiating roles a
	 * second time per person.
	 */
	public void initFromJsonApiResource(JSONObject resource, Map<String, JSONObject> included) {
		initFromJSONObject(resource);

		List<PhoneNumber> phones = new ArrayList<>();
		for (JSONObject phoneResource : JsonApiClient.resolveToMany(resource, "phone_numbers", included)) {
			PhoneNumber phoneNumber = new PhoneNumber();
			phoneNumber.setOwner(this);
			phoneNumber.initFromJSONObject(phoneResource);
			phones.add(phoneNumber);
		}
		this.phoneNumbers = phones;

		List<SocialAccount> accounts = new ArrayList<>();
		for (JSONObject accountResource : JsonApiClient.resolveToMany(resource, "social_accounts", included)) {
			SocialAccount socialAccount = new SocialAccount();
			socialAccount.setOwner(this);
			socialAccount.initFromJSONObject(accountResource);
			accounts.add(socialAccount);
		}
		this.socialAccounts = accounts;

		List<AdditionalMail> mails = new ArrayList<>();
		for (JSONObject mailResource : JsonApiClient.resolveToMany(resource, "additional_emails", included)) {
			AdditionalMail additionalMail = new AdditionalMail();
			additionalMail.setOwner(this);
			additionalMail.initFromJSONObject(mailResource);
			mails.add(additionalMail);
		}
		this.additionalMails = mails;
	}

	public void addRole(Role role) {
		if (this.roles == null) {
			this.roles = new ArrayList<>();
		}
		this.roles.add(role);
	}

	public boolean removeRole(Role role) {
		return this.roles != null && this.roles.remove(role);
	}

	@Override
	public String toString() {
		return "Person [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", nickname=" + nickname + ", email=" + email + "]";
	}
}
