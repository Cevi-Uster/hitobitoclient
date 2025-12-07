package ch.cevi.db.client.business.export;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ExportDescription {

	public enum ExportFormat {
		CSV, XLS
	};
	
	public static final String FORMAT_EXPORT_FORMAT_PROPERTY = "exportFormat";
	
	public static final String FORMAT_EXPORT_PATH_PROPERTY = "exportPath";
	
	public static final String FORMAT_GROUP_INFO_AS_HEADER_PROPERTY = "formatGroupInfoAsHeader";
	
	public static final String FORMAT_ADD_HEADER_INFO_PROPERTY = "formatAddHeaderInfo";
	
	public static final String FORMAT_EXPORT_HIERARCHICAL_PROPERTY = "formatExportHierarchical";
	
	public static final String EXPORT_ATTRIBUTE_GROUP = "exportGroup";
	
	public static final String EXPORT_ATTRIBUTE_ID_PROPERTY = "exportAttributeId";

	public static final String EXPORT_ROLE_PROPERTY = "exportRole";
	
	public static final String EXPORT_ATTRIBUTE_TYPE_PROPERTY = "exportAttributeType";

	public static final String EXPORT_ATTRIBUTE_HREF_PROPERTY = "exportAttributeHref";

	public static final String EXPORT_ATTRIBUTE_SALUTATION_PROPERTY = "exportAttributeSalutation";

	public static final String EXPORT_ATTRIBUTE_TITLE_PROPERTY = "exportAttributeTitle";

	public static final String EXPORT_ATTRIBUTE_FIRST_NAME_PROPERTY = "exportAttributeFirstName";

	public static final String EXPORT_ATTRIBUTE_LAST_NAME_PROPERTY = "exportAttributeLastName";

	public static final String EXPORT_ATTRIBUTE_NICKNAME_PROPERTY = "exportAttributeNickname";

	public static final String EXPORT_ATTRIBUTE_COMPANY_NAME_PROPERTY = "exportAttributeCompanyName";

	public static final String EXPORT_ATTRIBUTE_IS_COMPANY_PROPERTY = "exportAttributeIsCompany";

	public static final String EXPORT_ATTRIBUTE_GENDER_PROPERTY = "exportAttributeGender";

	public static final String EXPORT_ATTRIBUTE_ADDRESS_PROPERTY = "exportAttributeAddress";

	public static final String EXPORT_ATTRIBUTE_ZIP_CODE_PROPERTY = "exportAttributeZipCode";

	public static final String EXPORT_ATTRIBUTE_CANTON_PROPERTY = "exportAttributeCanton";

	public static final String EXPORT_ATTRIBUTE_TOWN_PROPERTY = "exportAttributeTown";

	public static final String EXPORT_ATTRIBUTE_COUNTRY_PROPERTY = "exportAttributeCountry";
	
	public static final String EXPORT_ATTRIBUTE_EMAIL_PROPERTY = "exportAttributeEmail";
	
	public static final String EXPORT_PUBLIC_EMAIL_ONLY_PROPERTY = "exportPublicEMailOnly";

	public static final String EXPORT_PUBLIC_PHONE_NUMBER_PROPERTY = "exportPublicPhoneNumber";
	
	public static final String EXPORT_PUBLIC_MOBILE_NUMBER_PROPERTY = "exportPublicMobileNumber";
	
	public static final String EXPORT_ATTRIBUTE_SALUTATION_PARENTS_PROPERTY = "exportAttributeSalutationParents";

	public static final String EXPORT_ATTRIBUTE_NAME_PARENTS_PROPERTY = "exportAttributeNameParents";

	public static final String EXPORT_ATTRIBUTE_JS_NUMBER_PROPERTY = "exportAttributeJsNumber";

	public static final String EXPORT_ATTRIBUTE_NATIONALITY_JS_PROPERTY = "exportAttributeNationalityJS";

	public static final String EXPORT_ATTRIBUTE_AHV_NUMBER_PROPERTY = "exportAttributeAhvNumber";

	public static final String EXPORT_ATTRIBUTE_AHV_NUMBER_OLD_PROPERTY = "exportAttributeAhvNumberOld";

	public static final String EXPORT_ATTRIBUTE_PROFESSION_PROPERTY = "exportAttributeProfession";

	public static final String EXPORT_ATTRIBUTE_JOINED_PROPERTY = "exportAttributeJoined";

	public static final String EXPORT_ATTRIBUTE_MEMBER_CARD_NUMBER_PROPERTY = "exportAttributeMemberCardNumber";

	public static final String EXPORT_ATTRIBUTE_NATIONALITY_PROPERTY = "exportAttributeNationality";

	public static final String EXPORT_ATTRIBUTE_CORRESPONDENCE_LANGUAGE_PROPERTY = "exportAttributeCorrespondenceLanguage";

	public static final String EXPORT_ATTRIBUTE_CONFESSION_PROPERTY = "exportAttributeConfession";

	public static final String EXPORT_ATTRIBUTE_BIRTHDAY_PROPERTY = "exportAttributeBirthday";

	public static final String EXPORT_ATTRIBUTE_PRIMARY_GROUP_ID_PROPERTY = "exportAttributePrimaryGroupId";

	public static final String EXPORT_ATTRIBUTE_ADDITIONAL_INFORMATION_PROPERTY = "exportAttributeAdditionalInformation";
	
	private ExportFormat exportFormat;
	
	private String exportPath;
	
	private boolean formatGroupInfoAsHeader;
	
	private boolean formatAddHeaderInfo;
	
	private boolean formatExportHierarchical;
	
	private boolean exportGroup;
	
	private boolean exportAttributeId;

	private boolean exportRole;
	
	private boolean exportAttributeType;

	private boolean exportAttributeHref;

	private boolean exportAttributeSalutation;

	private boolean exportAttributeTitle;

	private boolean exportAttributeFirstName;

	private boolean exportAttributeLastName;

	private boolean exportAttributeNickname;

	private boolean exportAttributeCompanyName;

	private boolean exportAttributeIsCompany;

	private boolean exportAttributeGender;

	private boolean exportAttributeAddress;

	private boolean exportAttributeZipCode;

	private boolean exportAttributeCanton;

	private boolean exportAttributeTown;

	private boolean exportAttributeCountry;

	private boolean exportAttributeEmail;
	
	private boolean exportPublicEMailOnly;
	
	private boolean exportPublicPhoneNumber;
	
	private boolean exportPublicMobileNumber;
	
	private boolean exportAttributeSalutationParents;

	private boolean exportAttributeNameParents;

	private boolean exportAttributeJsNumber;

	private boolean exportAttributeNationalityJS;

	private boolean exportAttributeAhvNumber;

	private boolean exportAttributeAhvNumberOld;

	private boolean exportAttributeProfession;

	private boolean exportAttributeJoined;

	private boolean exportAttributeMemberCardNumber;

	private boolean exportAttributeNationality;

	private boolean exportAttributeCorrespondenceLanguage;

	private boolean exportAttributeConfession;

	private boolean exportAttributeBirthday;

	private boolean exportAttributePrimaryGroupId;

	private boolean exportAttributeAdditionalInformation;
	
	private PropertyChangeSupport propertyChangeSupport;

	public ExportDescription(boolean exportAll) {
		super();
		this.exportFormat = ExportFormat.CSV;
		this.formatExportHierarchical = exportAll;
		this.exportAttributeId = exportAll;
		this.exportGroup = exportAll;
		this.exportRole = exportAll;
		this.exportAttributeType = exportAll;
		this.exportAttributeHref = exportAll;
		this.exportAttributeSalutation = exportAll;
		this.exportAttributeTitle = exportAll;
		this.exportAttributeFirstName = exportAll;
		this.exportAttributeLastName = exportAll;
		this.exportAttributeNickname = exportAll;
		this.exportAttributeCompanyName = exportAll;
		this.exportAttributeIsCompany = exportAll;
		this.exportAttributeGender = exportAll;
		this.exportAttributeAddress = exportAll;
		this.exportAttributeZipCode = exportAll;
		this.exportAttributeTown = exportAll;
		this.exportAttributeCanton = exportAll;
		this.exportAttributeCountry = exportAll;
		this.exportAttributeEmail = exportAll;
		this.exportPublicPhoneNumber = exportAll;
		this.exportPublicMobileNumber = exportAll;
		this.exportAttributeSalutationParents = exportAll;
		this.exportAttributeNameParents = exportAll;
		this.exportAttributeJsNumber = exportAll;
		this.exportAttributeNationalityJS = exportAll;
		this.exportAttributeAhvNumber = exportAll;
		this.exportAttributeAhvNumberOld = exportAll;
		this.exportAttributeProfession = exportAll;
		this.exportAttributeJoined = exportAll;
		this.exportAttributeMemberCardNumber = exportAll;
		this.exportAttributeNationality = exportAll;
		this.exportAttributeCorrespondenceLanguage = exportAll;
		this.exportAttributeConfession = exportAll;
		this.exportAttributeBirthday = exportAll;
		this.exportAttributePrimaryGroupId = exportAll;
		this.exportAttributeAdditionalInformation = exportAll;
		this.exportPublicEMailOnly = exportAll;
		this.propertyChangeSupport = new PropertyChangeSupport(this);
	}
	
	public ExportFormat getExportFormat() {
		return exportFormat;
	}

	public void setExportFormat(ExportFormat exportFormat) {
		this.exportFormat = exportFormat;
	}

	public String getExportPath() {
		return exportPath;
	}

	public void setExportPath(String exportPath) {
		this.exportPath = exportPath;
	}

	public boolean isFormatGroupInfoAsHeader() {
		return formatGroupInfoAsHeader;
	}

	public void setFormatGroupInfoAsHeader(boolean formatGroupInfoAsHeader) {
		this.formatGroupInfoAsHeader = formatGroupInfoAsHeader;
	}

	public boolean isFormatAddHeaderInfo() {
		return formatAddHeaderInfo;
	}

	public void setFormatAddHeaderInfo(boolean formatAddHeaderInfo) {
		this.formatAddHeaderInfo = formatAddHeaderInfo;
	}

	public boolean isFormatExportHierarchical() {
		return formatExportHierarchical;
	}

	public void setFormatExportHierarchical(boolean formatExportHierarchical) {
		this.formatExportHierarchical = formatExportHierarchical;
	}

	public boolean isExportGroup() {
		return exportGroup;
	}

	public void setExportGroup(boolean exportGroup) {
		this.exportGroup = exportGroup;
	}

	public boolean isExportAttributeId() {
		return exportAttributeId;
	}

	public void setExportAttributeId(boolean exportAttributeId) {
		this.exportAttributeId = exportAttributeId;
	}

	public boolean isExportRole() {
		return exportRole;
	}

	public void setExportRole(boolean exportRole) {
		this.exportRole = exportRole;
	}

	public boolean isExportAttributeType() {
		return exportAttributeType;
	}

	public void setExportAttributeType(boolean exportAttributeType) {
		this.exportAttributeType = exportAttributeType;
	}

	public boolean isExportAttributeHref() {
		return exportAttributeHref;
	}

	public void setExportAttributeHref(boolean exportAttributeHref) {
		this.exportAttributeHref = exportAttributeHref;
	}

	public boolean isExportAttributeFirstName() {
		return exportAttributeFirstName;
	}

	public void setExportAttributeFirstName(boolean exportAttributeFirstName) {
		this.exportAttributeFirstName = exportAttributeFirstName;
	}

	public boolean isExportAttributeLastName() {
		return exportAttributeLastName;
	}

	public void setExportAttributeLastName(boolean exportAttributeLastName) {
		this.exportAttributeLastName = exportAttributeLastName;
	}

	public boolean isExportAttributeNickname() {
		return exportAttributeNickname;
	}

	public void setExportAttributeNickname(boolean exportAttributeNickname) {
		this.exportAttributeNickname = exportAttributeNickname;
	}

	public boolean isExportAttributeCompanyName() {
		return exportAttributeCompanyName;
	}

	public void setExportAttributeCompanyName(boolean exportAttributeCompanyName) {
		this.exportAttributeCompanyName = exportAttributeCompanyName;
	}

	public boolean isExportAttributeIsCompany() {
		return exportAttributeIsCompany;
	}

	public void setExportAttributeIsCompany(boolean exportAttributeIsCompany) {
		this.exportAttributeIsCompany = exportAttributeIsCompany;
	}

	public boolean isExportAttributeGender() {
		return exportAttributeGender;
	}

	public void setExportAttributeGender(boolean exportAttributeGender) {
		this.exportAttributeGender = exportAttributeGender;
	}

	public boolean isExportAttributeEmail() {
		return exportAttributeEmail;
	}

	public void setExportAttributeEmail(boolean exportAttributeEmail) {
		this.exportAttributeEmail = exportAttributeEmail;
	}
	

	public boolean isExportPublicEMailOnly() {
		return exportPublicEMailOnly;
	}

	public void setExportPublicEMailOnly(boolean exportPublicEMailOnly) {
		this.exportPublicEMailOnly = exportPublicEMailOnly;
	}

	public boolean isExportAttributeAddress() {
		return exportAttributeAddress;
	}

	public void setExportAttributeAddress(boolean exportAttributeAddress) {
		this.exportAttributeAddress = exportAttributeAddress;
	}

	public boolean isExportAttributeZipCode() {
		return exportAttributeZipCode;
	}

	public void setExportAttributeZipCode(boolean exportAttributeZipCode) {
		this.exportAttributeZipCode = exportAttributeZipCode;
	}

	public boolean isExportAttributeTown() {
		return exportAttributeTown;
	}

	public void setExportAttributeTown(boolean exportAttributeTown) {
		this.exportAttributeTown = exportAttributeTown;
	}

	public boolean isExportAttributeCountry() {
		return exportAttributeCountry;
	}

	public void setExportAttributeCountry(boolean exportAttributeCountry) {
		this.exportAttributeCountry = exportAttributeCountry;
	}

	public boolean isExportPublicPhoneNumber() {
		return exportPublicPhoneNumber;
	}

	public void setExportPublicPhoneNumber(boolean exportPublicPhoneNumber) {
		this.exportPublicPhoneNumber = exportPublicPhoneNumber;
	}

	public boolean isExportPublicMobileNumber() {
		return exportPublicMobileNumber;
	}

	public void setExportPublicMobileNumber(boolean exportPublicMobileNumber) {
		this.exportPublicMobileNumber = exportPublicMobileNumber;
	}

	public boolean isExportAttributeAdditionalInformation() {
		return exportAttributeAdditionalInformation;
	}

	public void setExportAttributeAdditionalInformation(boolean exportAttributeAdditionalInformation) {
		this.exportAttributeAdditionalInformation = exportAttributeAdditionalInformation;
	}

	public boolean isExportAttributeSalutationParents() {
		return exportAttributeSalutationParents;
	}

	public void setExportAttributeSalutationParents(boolean exportAttributeSalutationParents) {
		this.exportAttributeSalutationParents = exportAttributeSalutationParents;
	}

	public boolean isExportAttributeNameParents() {
		return exportAttributeNameParents;
	}

	public void setExportAttributeNameParents(boolean exportAttributeNameParents) {
		this.exportAttributeNameParents = exportAttributeNameParents;
	}

	public boolean isExportAttributeCanton() {
		return exportAttributeCanton;
	}

	public void setExportAttributeCanton(boolean exportAttributeCanton) {
		this.exportAttributeCanton = exportAttributeCanton;
	}

	public boolean isExportAttributeJsNumber() {
		return exportAttributeJsNumber;
	}

	public void setExportAttributeJsNumber(boolean exportAttributeJsNumber) {
		this.exportAttributeJsNumber = exportAttributeJsNumber;
	}

	public boolean isExportAttributeNationalityJS() {
		return exportAttributeNationalityJS;
	}

	public void setExportAttributeNationalityJS(boolean exportAttributeNationalityJS) {
		this.exportAttributeNationalityJS = exportAttributeNationalityJS;
	}

	public boolean isExportAttributeAhvNumber() {
		return exportAttributeAhvNumber;
	}

	public void setExportAttributeAhvNumber(boolean exportAttributeAhvNumber) {
		this.exportAttributeAhvNumber = exportAttributeAhvNumber;
	}

	public boolean isExportAttributeTitle() {
		return exportAttributeTitle;
	}

	public void setExportAttributeTitle(boolean exportAttributeTitle) {
		this.exportAttributeTitle = exportAttributeTitle;
	}

	public boolean isExportAttributeProfession() {
		return exportAttributeProfession;
	}

	public void setExportAttributeProfession(boolean exportAttributeProfession) {
		this.exportAttributeProfession = exportAttributeProfession;
	}

	public boolean isExportAttributeJoined() {
		return exportAttributeJoined;
	}

	public void setExportAttributeJoined(boolean exportAttributeJoined) {
		this.exportAttributeJoined = exportAttributeJoined;
	}

	public boolean isExportAttributeAhvNumberOld() {
		return exportAttributeAhvNumberOld;
	}

	public void setExportAttributeAhvNumberOld(boolean exportAttributeAhvNumberOld) {
		this.exportAttributeAhvNumberOld = exportAttributeAhvNumberOld;
	}

	public boolean isExportAttributeMemberCardNumber() {
		return exportAttributeMemberCardNumber;
	}

	public void setExportAttributeMemberCardNumber(boolean exportAttributeMemberCardNumber) {
		this.exportAttributeMemberCardNumber = exportAttributeMemberCardNumber;
	}

	public boolean isExportAttributeNationality() {
		return exportAttributeNationality;
	}

	public void setExportAttributeNationality(boolean exportAttributeNationality) {
		this.exportAttributeNationality = exportAttributeNationality;
	}

	public boolean isExportAttributeSalutation() {
		return exportAttributeSalutation;
	}

	public void setExportAttributeSalutation(boolean exportAttributeSalutation) {
		this.exportAttributeSalutation = exportAttributeSalutation;
	}

	public boolean isExportAttributeCorrespondenceLanguage() {
		return exportAttributeCorrespondenceLanguage;
	}

	public void setExportAttributeCorrespondenceLanguage(boolean exportAttributeCorrespondenceLanguage) {
		this.exportAttributeCorrespondenceLanguage = exportAttributeCorrespondenceLanguage;
	}

	public boolean isExportAttributeConfession() {
		return exportAttributeConfession;
	}

	public void setExportAttributeConfession(boolean exportAttributeConfession) {
		this.exportAttributeConfession = exportAttributeConfession;
	}

	public boolean isExportAttributeBirthday() {
		return exportAttributeBirthday;
	}

	public void setExportAttributeBirthday(boolean exportAttributeBirthday) {
		this.exportAttributeBirthday = exportAttributeBirthday;
	}

	public boolean isExportAttributePrimaryGroupId() {
		return exportAttributePrimaryGroupId;
	}

	public void setExportAttributePrimaryGroupId(boolean exportAttributePrimaryGroupId) {
		this.exportAttributePrimaryGroupId = exportAttributePrimaryGroupId;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		this.propertyChangeSupport.addPropertyChangeListener(pcl);
	}

	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		this.propertyChangeSupport.removePropertyChangeListener(pcl);
	}
}
