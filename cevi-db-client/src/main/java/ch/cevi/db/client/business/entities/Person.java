package ch.cevi.db.client.business.entities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

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

	private String password;

	private String authenticationToken;

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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAuthenticationToken() {
		return authenticationToken;
	}

	public void setAuthenticationToken(String authenticationToken) {
		this.authenticationToken = authenticationToken;
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

	public void initFromJSONObject(JSONObject personObject) {
		if (personObject != null) {
			id = personObject.optString("id");
			type = personObject.optString("type");
			href = personObject.optString("href");
			firstName = personObject.optString("first_name");
			lastName = personObject.optString("last_name");
			nickname = personObject.optString("nickname");
			companyName = personObject.optString("company_name");
			isCompany = personObject.optBoolean("company", false);
			address = personObject.optString("address");
			zipCode = personObject.optString("zip_code");
			town = personObject.optString("town");
			country = personObject.optString("country");
			gender = personObject.optString("gender");
			email = personObject.optString("email");
			authenticationToken = personObject.optString("authentication_token");
			lastSignInAt = personObject.optString("last_sign_in_at");
			currentSignInAt = personObject.optString("current_sign_in_at");
			localDepartment = personObject.optString("ortsgruppe");
			additionalInformation = personObject.optString("additional_information");
			pictureUrl = personObject.optString("picture_url");
			salutationParents = personObject.optString("salutation_parents");
			nameParents = personObject.optString("name_parents");
			canton = personObject.optString("canton");
			jsNumber = personObject.optString("j_s_number");
			nationalityJS = personObject.optString("nationality_j_s");
			ahvNumber = personObject.optString("ahv_number");
			title = personObject.optString("title");
			profession = personObject.optString("profession");
			try {
				joined = JSON_DATE_FORMAT.parse(personObject.optString("joined"));
			} catch (ParseException pe) {
				// ignore
			}
			ahvNumberOld = personObject.optString("ahv_number_old");
			memberCardNumber = personObject.optString("member_card_number");
			nationality = personObject.optString("nationality");
			salutation = personObject.optString("salutation");
			correspondenceLanguage = personObject.optString("correspondence_language");
			confession = personObject.optString("confession");
			try {
				birthday = JSON_DATE_FORMAT.parse(personObject.optString("birthday"));
			} catch (ParseException pe) {
				// ignore
			}
			JSONObject linkObject = personObject.optJSONObject("links");
			if (linkObject != null) {
				primaryGroupId = linkObject.optString("primary_group");
			}
			// password is never delivered from server
		}
	}

	public void addPhoneNumbers(JSONObject linksObject, JSONArray phoneNumbersArray) {
		JSONArray phoneNumberIdArray = linksObject.optJSONArray("phone_numbers");
		List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();
		this.setPhoneNumbers(phoneNumbers);
		if (phoneNumberIdArray != null && phoneNumbersArray != null) {
			for (int phoneNumberIdIndex = 0; phoneNumberIdIndex < phoneNumberIdArray.length(); phoneNumberIdIndex++) {
				for (int phoneNumberIndex = 0; phoneNumberIndex < phoneNumbersArray.length(); phoneNumberIndex++) {
					JSONObject phoneNumberObject = phoneNumbersArray.optJSONObject(phoneNumberIndex);
					if (phoneNumberObject != null && phoneNumberIdArray.optString(phoneNumberIdIndex).equals(phoneNumberObject.opt("id"))) {
						PhoneNumber phoneNumber = new PhoneNumber();
						phoneNumber.setOwner(this);
						phoneNumber.initFromJSONObject(phoneNumberObject);
						phoneNumbers.add(phoneNumber);
					}
				}
			}
		}
	}

	public boolean removeRole(Role role) {
		return this.roles.remove(role);
	}

	public void addRoles(JSONObject linksObject, JSONArray rolesArray) {
		JSONArray rolesIdArray = linksObject.optJSONArray("roles");
		List<Role> roles = new ArrayList<Role>();
		this.setRoles(roles);
		if (rolesIdArray != null && rolesArray != null) {
			for (int roleIdIndex = 0; roleIdIndex < rolesIdArray.length(); roleIdIndex++) {
				for (int roleIndex = 0; roleIndex < rolesArray.length(); roleIndex++) {
					JSONObject roleObject = rolesArray.optJSONObject(roleIndex);
					if (roleObject != null && rolesIdArray.optString(roleIdIndex).equals(roleObject.opt("id"))) {
						Role role = new Role();
						role.setPerson(this);
						role.initFromJSONObject(roleObject);
						roles.add(role);
					}
				}
			}
		}
	}

	public void addAdditionalMails(JSONObject linksObject, JSONArray additionalMailsArray) {
		JSONArray additionalMailIdArray = linksObject.optJSONArray("additional_emails");
		List<AdditionalMail> additionalMails = new ArrayList<AdditionalMail>();
		this.setAdditionalMails(additionalMails);
		if (additionalMailIdArray != null && additionalMailsArray != null) {
			for (int additionalMailIdIndex = 0; additionalMailIdIndex < additionalMailIdArray.length(); additionalMailIdIndex++) {
				for (int additionalMailIndex = 0; additionalMailIndex < additionalMailsArray.length(); additionalMailIndex++) {
					JSONObject additionalMailObject = additionalMailsArray.optJSONObject(additionalMailIndex);
					if (additionalMailObject != null && additionalMailIdArray.optString(additionalMailIdIndex).equals(additionalMailObject.opt("id"))) {
						AdditionalMail additionalMail = new AdditionalMail();
						additionalMail.setOwner(this);
						additionalMail.initFromJSONObject(additionalMailObject);
						additionalMails.add(additionalMail);
					}
				}
			}
		}
	}

	public void addSocialAccounts(JSONObject linksObject, JSONArray socialAccountsArray) {
		JSONArray socialAccountsIdArray = linksObject.optJSONArray("social_accounts");
		List<SocialAccount> socialAccounts = new ArrayList<SocialAccount>();
		this.setSocialAccounts(socialAccounts);
		if (socialAccountsIdArray != null && socialAccountsArray != null) {
			for (int socialAccountIdIndex = 0; socialAccountIdIndex < socialAccountsIdArray.length(); socialAccountIdIndex++) {
				for (int socialAccountIndex = 0; socialAccountIndex < socialAccountsArray.length(); socialAccountIndex++) {
					JSONObject socialAccountObject = socialAccountsArray.optJSONObject(socialAccountIndex);
					if (socialAccountObject != null && socialAccountsIdArray.optString(socialAccountIdIndex).equals(socialAccountObject.opt("id"))) {
						SocialAccount socialAccount = new SocialAccount();
						socialAccount.setOwner(this);
						socialAccount.initFromJSONObject(socialAccountObject);
						socialAccounts.add(socialAccount);
					}
				}
			}
		}
	}

	@Override
	public String toString() {
		return "Person [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", nickname=" + nickname + ", email=" + email + "]";
	}
}
