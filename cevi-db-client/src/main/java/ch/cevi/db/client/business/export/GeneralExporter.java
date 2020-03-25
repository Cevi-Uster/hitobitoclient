package ch.cevi.db.client.business.export;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.swing.SwingWorker;

import ch.cevi.db.client.business.GroupByNameComparator;
import ch.cevi.db.client.business.LoadingListner;
import ch.cevi.db.client.business.PersonByNameComparator;
import ch.cevi.db.client.business.RoleComparator;
import ch.cevi.db.client.business.entities.AdditionalMail;
import ch.cevi.db.client.business.entities.Person;
import ch.cevi.db.client.business.entities.PhoneNumber;
import ch.cevi.db.client.business.entities.Role;
import ch.cevi.db.client.business.entities.YGroup;
import ch.cevi.db.client.communication.ServerFacade;
import ch.cevi.db.client.configuration.Texts;

/**
 * Exports a {@link YGroup} and all its subgroups and members. Only not deleted
 * groups and roles are exported.
 * 
 * @author developer
 *
 */
public abstract class GeneralExporter {

	private static final SimpleDateFormat EXPORT_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
	
	private YGroup yGroup;

	private ExportDescription exportDescription;

	private ServerFacade serverFacade;

	protected File targetFile;

	public GeneralExporter(ExportDescription exportDescription, ServerFacade serverFacade, YGroup yGroup) {
		this.exportDescription = exportDescription;
		this.serverFacade = serverFacade;
		this.targetFile = new File(exportDescription.getExportPath());
		this.yGroup = yGroup;
	}

	public void exportData(final LoadingListner<Object> loadingListener) {
		SwingWorker<?, ?> worker = new SwingWorker<Object, Object>() {

			private Exception e;

			@Override
			protected Object doInBackground() throws Exception {
				try {
					exportDataImpl(loadingListener);
				} catch (Exception e) {
					this.e = e;
				}
				return null;
			}

			@Override
			protected void done() {
				loadingListener.loadingFinished(null, e);
				super.done();
			}
		};
		worker.execute();
	}
	
	protected abstract void saveToFile(List<List<String>> data) throws Exception;

	private void exportDataImpl(LoadingListner<Object> loadingListener) throws Exception {
		serverFacade.loadAllChildren(yGroup, loadingListener);
		List<List<String>> data = new ArrayList<List<String>>();
		if (!exportDescription.isFormatGroupInfoAsHeader()) {
			if (exportDescription.isFormatAddHeaderInfo()){
				exportColumnHeaders(data);
			}
		}
		exportGroup(yGroup, data);

		saveToFile(data);
	}

	private void exportColumnHeaders(List<List<String>> data) {
		List<String> headerData = new ArrayList<String>(2);
		if (!exportDescription.isFormatGroupInfoAsHeader()) {
			headerData.add(Texts.getTranslatedText(Texts.EXPORT_HEADER_GROUP_NAME));
		}
		if (exportDescription.isExportAttributeId()) {
			headerData.add(Texts.getTranslatedText(Texts.EXPORT_HEADER_ID));
		}
		if (exportDescription.isExportAttributeType()) {
			headerData.add(Texts.getTranslatedText(Texts.EXPORT_HEADER_TYPE));
		}
		if (exportDescription.isExportAttributeHref()) {
			headerData.add(Texts.getTranslatedText(Texts.EXPORT_HEADER_HREF));
		}
		if (exportDescription.isExportRole()) {
			headerData.add(Texts.getTranslatedText(Texts.EXPORT_HEADER_ROLE_TYPE));
		}
		if (exportDescription.isExportAttributeSalutation()) {
			headerData.add(Texts.getTranslatedText(Texts.EXPORT_HEADER_SALUTATION));
		}
		if (exportDescription.isExportAttributeTitle()) {
			headerData.add(Texts.getTranslatedText(Texts.EXPORT_HEADER_TITLE));
		}
		if (exportDescription.isExportAttributeFirstName()) {
			headerData.add(Texts.getTranslatedText(Texts.EXPORT_HEADER_FIRST_NAME));
		}
		if (exportDescription.isExportAttributeLastName()) {
			headerData.add(Texts.getTranslatedText(Texts.EXPORT_HEADER_LAST_NAME));
		}
		if (exportDescription.isExportAttributeNickname()) {
			headerData.add(Texts.getTranslatedText(Texts.EXPORT_HEADER_NICKNAME));
		}
		if (exportDescription.isExportAttributeCompanyName()) {
			headerData.add(Texts.getTranslatedText(Texts.EXPORT_HEADER_COMPANY_NAME));
		}
		if (exportDescription.isExportAttributeGender()) {
			headerData.add(Texts.getTranslatedText(Texts.EXPORT_HEADER_COMPANY_NAME));
		}
		if (exportDescription.isExportAttributeAddress()) {
			headerData.add(Texts.getTranslatedText(Texts.EXPORT_HEADER_ADDRESS));
		}
		if (exportDescription.isExportAttributeZipCode()) {
			headerData.add(Texts.getTranslatedText(Texts.EXPORT_HEADER_ZIP_CODE));
		}
		if (exportDescription.isExportAttributeTown()) {
			headerData.add(Texts.getTranslatedText(Texts.EXPORT_HEADER_TOWN));
		}
		if (exportDescription.isExportAttributeCanton()) {
			headerData.add(Texts.getTranslatedText(Texts.EXPORT_HEADER_CANTON));
		}
		if (exportDescription.isExportAttributeCountry()) {
			headerData.add(Texts.getTranslatedText(Texts.EXPORT_HEADER_COUNTRY));
		}
		if (exportDescription.isExportPublicPhoneNumber()) {
			headerData.add(Texts.getTranslatedText(Texts.EXPORT_HEADER_MOBILE_NUMBER));
		}
		if (exportDescription.isExportPublicMobileNumber()) {
			headerData.add(Texts.getTranslatedText(Texts.EXPORT_HEADER_PHONE));
		}
		if (exportDescription.isExportAttributeEmail()) {
			headerData.add(Texts.getTranslatedText(Texts.EXPORT_HEADER_EMAIL));
		}
		if (exportDescription.isExportAttributeSalutationParents()){
			headerData.add(Texts.getTranslatedText(Texts.EXPORT_HEADER_SALUTATION_PARENTS));
		}
		if (exportDescription.isExportAttributeNameParents()){
			headerData.add(Texts.getTranslatedText(Texts.EXPORT_HEADER_NAME_PARENTS));
		}
		if (exportDescription.isExportAttributeJsNumber()){
			headerData.add(Texts.getTranslatedText(Texts.EXPORT_HEADER_JS_NUMBER));
		}
		if (exportDescription.isExportAttributeNationalityJS()){
			headerData.add(Texts.getTranslatedText(Texts.EXPORT_HEADER_NATIONALITY_JS));
		}
		if (exportDescription.isExportAttributeAhvNumber()){
			headerData.add(Texts.getTranslatedText(Texts.EXPORT_HEADER_AHV_NUMBER));
		}
		if (exportDescription.isExportAttributeAhvNumberOld()){
			headerData.add(Texts.getTranslatedText(Texts.EXPORT_HEADER_AHV_NUMBER_OLD));
		}
		if (exportDescription.isExportAttributeProfession()){
			headerData.add(Texts.getTranslatedText(Texts.EXPORT_HEADER_PROFESSION));
		}
		if (exportDescription.isExportAttributeJoined()){
			headerData.add(Texts.getTranslatedText(Texts.EXPORT_HEADER_BIRTHDAY));
		}
		if (exportDescription.isExportAttributeMemberCardNumber()){
			headerData.add(Texts.getTranslatedText(Texts.EXPORT_HEADER_MEMBER_CARD_NUMBER));
		}
		if (exportDescription.isExportAttributeNationality()){
			headerData.add(Texts.getTranslatedText(Texts.EXPORT_HEADER_NATIONALITY));
		}
		if (exportDescription.isExportAttributeCorrespondenceLanguage()){
			headerData.add(Texts.getTranslatedText(Texts.EXPORT_HEADER_CORRESPONDENCE_LANG));
		}
		if (exportDescription.isExportAttributeConfession()){
			headerData.add(Texts.getTranslatedText(Texts.EXPORT_HEADER_CONFESSION));
		}
		if (exportDescription.isExportAttributeBirthday()){
			headerData.add(Texts.getTranslatedText(Texts.EXPORT_HEADER_BIRTHDAY));
		}
		if (exportDescription.isExportAttributePrimaryGroupId()){
			headerData.add(Texts.getTranslatedText(Texts.EXPORT_HEADER_PRIMARY_GROUP));
		}
		if (exportDescription.isExportAttributeAdditionalInformation()){
			headerData.add(Texts.getTranslatedText(Texts.EXPORT_HEADER_ADDITIONAL_INFORMATION));
		}
		data.add(headerData);
	}
	
	/**
	 * Export the supplied {@link YGroup} to the supplied {@link List}. If
	 * the {@link YGroup} is deleted nothing is written.
	 * 
	 * @param yGroup
	 * @param data
	 * @throws IOException
	 */
	private void exportGroup(YGroup yGroup, List<List<String>> data) {
		if (yGroup.getDeletedAt() == null) {
			if (exportDescription.isFormatGroupInfoAsHeader()) {
				List<String> groupData = new ArrayList<String>(2);
				groupData.add(yGroup.getName());
				groupData.add(yGroup.getEmail());
				data.add(groupData);
				if (exportDescription.isFormatAddHeaderInfo()){
					exportColumnHeaders(data);
				}
			}

			List<Person> groupMembers = yGroup.getGroupMembers();
			Collections.sort(groupMembers, new PersonByNameComparator());
			groupMembers = sortGroupMembersByRole(yGroup, groupMembers);
			for (Person member : groupMembers) {
				List<Role> roles = member.getRoles();
				Collections.sort(roles, new RoleComparator());
				for (Role role : roles) {
					if (role.getGroupId() != null && role.getGroupId().equals(yGroup.getId()) && role.getDeletedAt() == null) {
						List<String> memberData = new ArrayList<String>();
						if (!exportDescription.isFormatGroupInfoAsHeader()) {
							memberData.add(yGroup.getName());
						}
						if (exportDescription.isExportAttributeId()) {
							memberData.add(member.getId());
						}
						if (exportDescription.isExportAttributeType()) {
							memberData.add(member.getType());
						}
						if (exportDescription.isExportAttributeHref()) {
							memberData.add(member.getHref());
						}
						if (exportDescription.isExportRole()) {
							if (role.getLabel() != null && !role.getLabel().isEmpty()) {
								memberData.add(String.format("%s (%s)", role.getRoleType(), role.getLabel()));
							} else {
								memberData.add(role.getRoleType());
							}
						}
						if (exportDescription.isExportAttributeSalutation()) {
							memberData.add(member.getSalutation());
						}
						if (exportDescription.isExportAttributeTitle()) {
							memberData.add(member.getTitle());
						}
						if (exportDescription.isExportAttributeFirstName()) {
							memberData.add(member.getFirstName());
						}
						if (exportDescription.isExportAttributeLastName()) {
							memberData.add(member.getLastName());
						}
						if (exportDescription.isExportAttributeNickname()) {
							memberData.add(member.getNickname());
						}
						if (exportDescription.isExportAttributeCompanyName()) {
							memberData.add(member.getCompanyName());
						}
						if (exportDescription.isExportAttributeGender()) {
							memberData.add(member.getGender());
						}
						if (exportDescription.isExportAttributeAddress()) {
							memberData.add(member.getAddress());
						}
						if (exportDescription.isExportAttributeZipCode()) {
							memberData.add(member.getZipCode());
						}
						if (exportDescription.isExportAttributeTown()) {
							memberData.add(member.getTown());
						}
						if (exportDescription.isExportAttributeCanton()) {
							memberData.add(member.getCanton());
						}
						if (exportDescription.isExportAttributeCountry()) {
							memberData.add(member.getCountry());
						}
						if (exportDescription.isExportPublicPhoneNumber()) {
							memberData.add(getPublicHomeNumber(member.getPhoneNumbers()));
						}
						if (exportDescription.isExportPublicMobileNumber()) {
							memberData.add(getPublicMobileNumber(member.getPhoneNumbers()));
						}
						if (exportDescription.isExportAttributeEmail()) {
							memberData.add(getMailAddress(member));
						}
						if (exportDescription.isExportAttributeSalutationParents()){
							memberData.add(member.getSalutationParents());
						}
						if (exportDescription.isExportAttributeNameParents()){
							memberData.add(member.getNameParents());
						}
						if (exportDescription.isExportAttributeJsNumber()){
							memberData.add(member.getJsNumber());
						}
						if (exportDescription.isExportAttributeNationalityJS()){
							memberData.add(member.getNationalityJS());
						}
						if (exportDescription.isExportAttributeAhvNumber()){
							memberData.add(member.getAhvNumber());
						}
						if (exportDescription.isExportAttributeAhvNumberOld()){
							memberData.add(member.getAhvNumberOld());
						}
						if (exportDescription.isExportAttributeProfession()){
							memberData.add(member.getProfession());
						}
						if (exportDescription.isExportAttributeJoined()){
							memberData.add(member.getJoined() != null ? EXPORT_DATE_FORMAT.format(member.getJoined()) : "");
						}
						if (exportDescription.isExportAttributeMemberCardNumber()){
							memberData.add(member.getMemberCardNumber());
						}
						if (exportDescription.isExportAttributeNationality()){
							memberData.add(member.getNationality());
						}
						if (exportDescription.isExportAttributeCorrespondenceLanguage()){
							memberData.add(member.getCorrespondenceLanguage());
						}
						if (exportDescription.isExportAttributeConfession()){
							memberData.add(member.getConfession());
						}
						if (exportDescription.isExportAttributeBirthday()){
							memberData.add(member.getBirthday() != null ? EXPORT_DATE_FORMAT.format(member.getBirthday()) : "");
						}
						if (exportDescription.isExportAttributePrimaryGroupId()){
							memberData.add(member.getPrimaryGroupId());
						}
						if (exportDescription.isExportAttributeAdditionalInformation()){
							memberData.add(member.getAdditionalInformation());
						}
						data.add(memberData);
					}
				}
			}

			List<YGroup> childGroups = yGroup.getChildren();
			Collections.sort(childGroups, new GroupByNameComparator());

			for (YGroup childGroup : childGroups) {
				// Add empty line
				data.add(new ArrayList<String>());
				exportGroup(childGroup, data);
			}
		}
	}

	private static List<Person> sortGroupMembersByRole(YGroup yGroup, List<Person> groupMembers) {
		List<Person> sortedMembers = new ArrayList<Person>();
		Map<Role, Person> rolePersonMap = new LinkedHashMap<Role, Person>();
		for (Person person : groupMembers) {
			for (Role role : person.getRoles()) {
				if (role.getGroupId() != null && role.getGroupId().equals(yGroup.getId()) && role.getDeletedAt() == null) {
					rolePersonMap.put(role, person);
				}
			}
		}
		List<Role> sortedRoles = new ArrayList<Role>(rolePersonMap.keySet());
		Collections.sort(sortedRoles, new RoleComparator());
		for (Role role : sortedRoles) {
			sortedMembers.add(rolePersonMap.get(role));
		}
		return sortedMembers;
	}

	private String getPublicHomeNumber(List<PhoneNumber> phoneNumbers) {
		PhoneNumber homeNumber = null;
		for (PhoneNumber phoneNumber : phoneNumbers) {
			if (homeNumber == null) {
				if (phoneNumber.getIsPublic() && "Privat".equals(phoneNumber.getLabel())) {
					homeNumber = phoneNumber;
					break;
				}
			}
		}
		return homeNumber != null ? homeNumber.getNumber() : "";
	}

	private String getPublicMobileNumber(List<PhoneNumber> phoneNumbers) {
		PhoneNumber mobileNumber = null;
		for (PhoneNumber phoneNumber : phoneNumbers) {
			if (mobileNumber == null) {
				if (phoneNumber.getIsPublic() && "Mobil".equals(phoneNumber.getLabel())) {
					mobileNumber = phoneNumber;
					break;
				}
			}
		}
		return mobileNumber != null ? mobileNumber.getNumber() : "";

	}

	private String getMailAddress(Person person) {
		if (person.getEmail() != null && !person.getEmail().isEmpty()) {
			return person.getEmail();
		} else {
			Optional<AdditionalMail> optionalAdditionalMail = person.getAdditionalMails().stream().filter(mail -> mail.isPublicVisible()).findFirst();
			if (optionalAdditionalMail.isPresent()) {
				return optionalAdditionalMail.get().getEmail();
			} else if (!exportDescription.isExportPublicEMailOnly()) {
				Optional<AdditionalMail> optionalAdditionalPrivateMail = person.getAdditionalMails().stream().findFirst();
				if (optionalAdditionalPrivateMail.isPresent()) {
					return optionalAdditionalPrivateMail.get().getEmail();
				}
			}
		}
		return null;
	}
}
