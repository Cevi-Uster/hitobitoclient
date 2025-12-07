package ch.cevi.db.client.communication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cevi.db.client.api.GroupsApi;
import ch.cevi.db.client.api.PeopleApi;
import ch.cevi.db.client.api.model.GroupsSingle;
import ch.cevi.db.client.api.model.People;
import ch.cevi.db.client.api.model.PeopleCollection;
import ch.cevi.db.client.api.model.PeopleSingle;
import ch.cevi.db.client.api.model.PeopleReadableAttribute;
import ch.cevi.db.client.api.model.PeopleResource;
import ch.cevi.db.client.api.model.GroupsReadableAttribute;
import ch.cevi.db.client.api.model.RolesReadableAttribute;
import ch.cevi.db.client.api.model.PhoneNumbersReadableAttribute;
import ch.cevi.db.client.api.model.SocialAccountsReadableAttribute;
import ch.cevi.db.client.api.model.AdditionalEmailsReadableAttribute;
import ch.cevi.db.client.business.LoadingListner;
import ch.cevi.db.client.business.entities.Person;
import ch.cevi.db.client.business.entities.Role;
import ch.cevi.db.client.business.entities.YGroup;
import ch.cevi.db.client.configuration.Texts;
import org.springframework.web.client.RestClientException;

/**
 * Facade for all business operations on the hitobito server.
 * {@link ISession#login(String, String)} must be called before any business
 * operations may be called!
 * 
 * @author developer
 *
 */
public class ServerFacade {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServerFacade.class.getName());

	private ISession session;

	private Map<String, YGroup> groupCache = Collections.synchronizedMap(new HashMap<String, YGroup>());

	private Map<String, Person> personCache = Collections.synchronizedMap(new HashMap<String, Person>());

	public ServerFacade(ISession session) {
		this.session = session;
	}

	/**
	 * Load the group with Id groupId using the new API.
	 */
	public YGroup loadGroup(String groupId) throws Exception {
		LOGGER.info("loadGroup for groupId" + groupId);
		YGroup group = groupCache.get(groupId);
		if (group != null) {
			return group;
		} else {
			synchronized (this.groupCache) {
				if (this.groupCache.get(groupId) == null) {
					try {
						GroupsApi groupsApi = new GroupsApi(((Session)session).getApiClient());
						GroupsSingle groupSingle = groupsApi.getGroup(
							groupId,
							null, // include
							null, // sort
							null, // fieldsGroups
							null, // extraFieldsGroups
							null, null, null, null, null, null, // filterId*
							null, null, null, null, null, null, null, null, null, null, // filterName*
							null, null, null, null, null, null, null, null, null, null, // filterShortName*
							null, null, null, null, null, null, null, null, null, null, // filterDisplayName*
							null, null, null, null, null, null, null, null, null, null, // filterDescription*
							null, // filterLayerEq
							null, null, null, null, null, null, // filterParentId*
							null, null, null, null, null, null, // filterLayerGroupId*
							null, null, null, null, null, null, null, null, null, null, // filterType*
							null, null, null, null, null, null, null, null, null, null, // filterEmail*
							null, null, null, null, null, null, null, null, null, null, // filterAddress*
							null, null, null, null, null, null, // filterZipCode*
							null, null, null, null, null, null, null, null, null, null, // filterTown*
							null, null, null, null, null, null, null, null, null, null, // filterCountry*
							null, // filterRequirePersonAddRequestsEq
							null, null, null, null, null, null, null, null, null, null, // filterSelfRegistrationUrl*
							null, null, null, null, null, null, // filterArchivedAt*
							null, null, null, null, null, null, // filterCreatedAt*
							null, null, null, null, null, null, // filterUpdatedAt*
							null, null, null, null, null, null, // filterDeletedAt*
							null, // filterWithDeletedEq
							null, // filterWithArchivedEq
							null, // fieldsPeople
							null, // fieldsPhoneNumbers
							null, // fieldsSocialAccounts
							null  // fieldsAdditionalEmails
						);
						group = new YGroup();
						// Map groupSingle to YGroup
						group.initBasicDataFromJSONObject(new JSONObject(groupSingle.toString()));
						group.initDetailDataFromJSONObject(new JSONObject(groupSingle.toString()));
						this.groupCache.put(groupId, group);
					} catch (RestClientException e) {
						LOGGER.error("API error loading group: " + e.getMessage());
						throw e;
					}
				} else {
					group = this.groupCache.get(groupId);
				}
			}
			return group;
		}
	}

	/**
	 * Load the root {@link YGroup}.
	 * 
	 * @return
	 * @throws Exception
	 */
	public YGroup loadMainGroupWithDetails() throws Exception {
		return loadGroupWithDetails(session.getMainGroupId(), null);
	}

	/**
	 * Load all missing children of the {@link YGroup} <code>parentGroup</code>
	 * and add them to the group.
	 * 
	 * @param parentGroup
	 * @param loadingListener
	 * @throws Exception
	 */
	public void loadAllChildren(YGroup parentGroup, LoadingListner<Object> loadingListener) throws Exception {
		if (loadingListener != null) {
			loadingListener.displayCurrentAction(String.format(Texts.getTranslatedText(Texts.HIERARCHICAL_CSV_EXPORT_PROGRESS_STRING), parentGroup.getName()));
		}
		for (YGroup childGroupToComplete : parentGroup.getChildren()) {
			completeNotFullyLoadedGroup(childGroupToComplete);
			loadAllChildren(childGroupToComplete, loadingListener);
		}
	}

	/**
	 * Load the group and details of the group with Id groupId
	 * 
	 * @param groupId
	 *            Id of the group to load
	 * @param parentGroupId
	 *            parent of the group which should be loaded. Can be null.
	 * @return the loaded {@link YGroup}
	 * @throws Exception
	 */
	public YGroup loadGroupWithDetails(String groupId, String parentGroupId) throws Exception {
		LOGGER.info("loadGroupDetails for groupId" + groupId);

        // Try to get group from cache
        YGroup group = groupCache.get(groupId);
        if (group == null) {
            synchronized (this.groupCache) {
                group = groupCache.get(groupId);
                if (group == null) {
                    group = new YGroup();
                    groupCache.put(groupId, group);
                }
            }
        }
        if (!group.isFullyLoaded()) {
            try {
                GroupsApi groupsApi = new GroupsApi(((Session)session).getApiClient());
                Set<String> include = new HashSet<>();
                include.add("children");
                include.add("roles");
                GroupsSingle groupSingle = groupsApi.getGroup(
                    groupId,
                    include, // include children and roles
                    null, // sort
                    null, // fieldsGroups
                    null, // extraFieldsGroups
                    null, null, null, null, null, null, // filterId*
                    null, null, null, null, null, null, null, null, null, null, // filterName*
                    null, null, null, null, null, null, null, null, null, null, // filterShortName*
                    null, null, null, null, null, null, null, null, null, null, // filterDisplayName*
                    null, null, null, null, null, null, null, null, null, null, // filterDescription*
                    null, // filterLayerEq
                    null, null, null, null, null, null, // filterParentId*
                    null, null, null, null, null, null, // filterLayerGroupId*
                    null, null, null, null, null, null, null, null, null, null, // filterType*
                    null, null, null, null, null, null, null, null, null, null, // filterEmail*
                    null, null, null, null, null, null, null, null, null, null, // filterAddress*
                    null, null, null, null, null, null, // filterZipCode*
                    null, null, null, null, null, null, null, null, null, null, // filterTown*
                    null, null, null, null, null, null, null, null, null, null, // filterCountry*
                    null, // filterRequirePersonAddRequestsEq
                    null, null, null, null, null, null, null, null, null, null, // filterSelfRegistrationUrl*
                    null, null, null, null, null, null, // filterArchivedAt*
                    null, null, null, null, null, null, // filterCreatedAt*
                    null, null, null, null, null, null, // filterUpdatedAt*
                    null, null, null, null, null, null, // filterDeletedAt*
                    null, // filterWithDeletedEq
                    null, // filterWithArchivedEq
                    null, // fieldsPeople
                    null, // fieldsPhoneNumbers
                    null, // fieldsSocialAccounts
                    null  // fieldsAdditionalEmails
                );
                // Map groupSingle to YGroup
                group.initBasicDataFromJSONObject(new JSONObject(groupSingle.toString()));
                group.initDetailDataFromJSONObject(new JSONObject(groupSingle.toString()));
                group.setParentGroupId(parentGroupId);
                group.setParentYGroup(groupCache.get(parentGroupId));
                // Children
                List<YGroup> children = new ArrayList<>();
                if (groupSingle.getIncluded() != null) {
                    for (Object includedObj : groupSingle.getIncluded()) {
                        JSONObject includedJson = new JSONObject(includedObj.toString());
                        if ("group".equalsIgnoreCase(includedJson.optString("type"))) {
                            YGroup childGroup = new YGroup();
                            childGroup.initBasicDataFromJSONObject(includedJson);
                            childGroup.setParentYGroup(group);
                            childGroup.setParentGroupId(group.getId());
                            children.add(childGroup);
                            groupCache.put(childGroup.getId(), childGroup);
                        }
                    }
                }
                group.setChildren(children);
                // Roles
                List<Role> groupRoles = new ArrayList<>();
                if (groupSingle.getIncluded() != null) {
                    for (Object includedObj : groupSingle.getIncluded()) {
                        JSONObject includedJson = new JSONObject(includedObj.toString());
                        if ("role".equalsIgnoreCase(includedJson.optString("type"))) {
                            // Map role as needed
                            Role role = new Role();
                            // TODO: Map fields from includedJson to Role
                            role.setyGroup(group);
                            groupRoles.add(role);
                        }
                    }
                }
                group.setGroupRoles(groupRoles);
                group.setFullyLoaded(true);
            } catch (Exception e) {
                LOGGER.error("API error loading group details: " + e.getMessage());
                throw e;
            }
        }
        return group;
	}

	public void completeNotFullyLoadedGroup(YGroup group) throws Exception {
		if (group.isFullyLoaded()) {
			return;
		}
		// Instead of calling initFromOtherGroup, reload the group details using the API
		YGroup loadedGroup = loadGroupWithDetails(group.getId(), group.getParentYGroupId());
		// Copy all relevant fields from loadedGroup to the current group
		group.initFromOtherGroup(loadedGroup);
	}

	/**
	 * Load all persons of the group with the supplied groupId using the new API.
	 */
	public List<Person> loadPersonList(final String groupId) throws Exception {
        LOGGER.info("loadPersonList for groupId" + groupId);
        final List<Person> personResultList = Collections.synchronizedList(new ArrayList<Person>());
        try {
            GroupsApi groupsApi = new GroupsApi(session.getApiClient());
            // Fetch group with roles included
            Set<String> include = new HashSet<>();
            include.add("roles");
            GroupsSingle groupSingle = groupsApi.getGroup(
                groupId,
                include, // include roles
                null, // sort
                null, // fieldsGroups
                null, // extraFieldsGroups
                null, null, null, null, null, null, // filterId*
                null, null, null, null, null, null, null, null, null, null, // filterName*
                null, null, null, null, null, null, null, null, null, null, // filterShortName*
                null, null, null, null, null, null, null, null, null, null, // filterDisplayName*
                null, null, null, null, null, null, null, null, null, null, // filterDescription*
                null, // filterLayerEq
                null, null, null, null, null, null, // filterParentId*
                null, null, null, null, null, null, // filterLayerGroupId*
                null, null, null, null, null, null, null, null, null, null, // filterType*
                null, null, null, null, null, null, null, null, null, null, // filterEmail*
                null, null, null, null, null, null, null, null, null, null, // filterAddress*
                null, null, null, null, null, null, // filterZipCode*
                null, null, null, null, null, null, null, null, null, null, // filterTown*
                null, null, null, null, null, null, null, null, null, null, // filterCountry*
                null, // filterRequirePersonAddRequestsEq
                null, null, null, null, null, null, null, null, null, null, // filterSelfRegistrationUrl*
                null, null, null, null, null, null, // filterArchivedAt*
                null, null, null, null, null, null, // filterCreatedAt*
                null, null, null, null, null, null, // filterUpdatedAt*
                null, null, null, null, null, null, // filterDeletedAt*
                null, // filterWithDeletedEq
                null, // filterWithArchivedEq
                null, // fieldsPeople
                null, // fieldsPhoneNumbers
                null, // fieldsSocialAccounts
                null  // fieldsAdditionalEmails
            );
            // Extract person IDs from roles
            Set<String> personIds = new HashSet<>();
            // Assuming groupSingle has a method getIncluded() or similar to access included roles
            if (groupSingle.getIncluded() != null) {
                for (Object includedObj : groupSingle.getIncluded()) {
                    JSONObject includedJson = new JSONObject(includedObj.toString());
                    if ("role".equalsIgnoreCase(includedJson.optString("type"))) {
                        JSONObject relationships = includedJson.optJSONObject("relationships");
                        if (relationships != null) {
                            JSONObject personRel = relationships.optJSONObject("person");
                            if (personRel != null) {
                                JSONObject data = personRel.optJSONObject("data");
                                if (data != null) {
                                    String personId = data.optString("id");
                                    if (personId != null && !personId.isEmpty()) {
                                        personIds.add(personId);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // Fetch each person by ID
            PeopleApi peopleApi = new PeopleApi(session.getApiClient());
            for (String personId : personIds) {
                try {
                    // Fetch person details
                    PeopleSingle personSingle = peopleApi.getPerson(
                        personId,
                        Collections.emptySet(), // include
                        Collections.emptySet(), // sort
                        Collections.emptySet(), // fieldsPeople
                        Collections.emptyList(), // filterIdEq
                        Collections.emptyList(), // filterIdNotEq
                        Collections.emptyList(), // filterIdGt
                        Collections.emptyList(), // filterIdGte
                        Collections.emptyList(), // filterIdLt
                        Collections.emptyList(), // filterIdLte
                        Collections.emptyList(), // filterFirstNameEq
                        Collections.emptyList(), // filterFirstNameNotEq
                        Collections.emptyList(), // filterFirstNameEql
                        Collections.emptyList(), // filterFirstNameNotEql
                        Collections.emptyList(), // filterFirstNamePrefix
                        Collections.emptyList(), // filterFirstNameNotPrefix
                        Collections.emptyList(), // filterFirstNameSuffix
                        Collections.emptyList(), // filterFirstNameNotSuffix
                        Collections.emptyList(), // filterFirstNameMatch
                        Collections.emptyList(), // filterFirstNameNotMatch
                        Collections.emptyList(), // filterLastNameEq
                        Collections.emptyList(), // filterLastNameNotEq
                        Collections.emptyList(), // filterLastNameEql
                        Collections.emptyList(), // filterLastNameNotEql
                        Collections.emptyList(), // filterLastNamePrefix
                        Collections.emptyList(), // filterLastNameNotPrefix
                        Collections.emptyList(), // filterLastNameSuffix
                        Collections.emptyList(), // filterLastNameNotSuffix
                        Collections.emptyList(), // filterLastNameMatch
                        Collections.emptyList(), // filterLastNameNotMatch
                        Collections.emptyList(), // filterNicknameEq
                        Collections.emptyList(), // filterNicknameNotEq
                        Collections.emptyList(), // filterNicknameEql
                        Collections.emptyList(), // filterNicknameNotEql
                        Collections.emptyList(), // filterNicknamePrefix
                        Collections.emptyList(), // filterNicknameNotPrefix
                        Collections.emptyList(), // filterNicknameSuffix
                        Collections.emptyList(), // filterNicknameNotSuffix
                        Collections.emptyList(), // filterNicknameMatch
                        Collections.emptyList(), // filterNicknameNotMatch
                        Collections.emptyList(), // filterCompanyNameEq
                        Collections.emptyList(), // filterCompanyNameNotEq
                        Collections.emptyList(), // filterCompanyNameEql
                        Collections.emptyList(), // filterCompanyNameNotEql
                        Collections.emptyList(), // filterCompanyNamePrefix
                        Collections.emptyList(), // filterCompanyNameNotPrefix
                        Collections.emptyList(), // filterCompanyNameSuffix
                        Collections.emptyList(), // filterCompanyNameNotSuffix
                        Collections.emptyList(), // filterCompanyNameMatch
                        Collections.emptyList(), // filterCompanyNameNotMatch
                        null, // filterCompanyEq
                        Collections.emptyList(), // filterEmailEq
                        Collections.emptyList(), // filterEmailNotEq
                        Collections.emptyList(), // filterEmailEql
                        Collections.emptyList(), // filterEmailNotEql
                        Collections.emptyList(), // filterEmailPrefix
                        Collections.emptyList(), // filterEmailNotPrefix
                        Collections.emptyList(), // filterEmailSuffix
                        Collections.emptyList(), // filterEmailNotSuffix
                        Collections.emptyList(), // filterEmailMatch
                        Collections.emptyList(), // filterEmailNotMatch
                        Collections.emptyList(), // filterAddressEq
                        Collections.emptyList(), // filterAddressNotEq
                        Collections.emptyList(), // filterAddressEql
                        Collections.emptyList(), // filterAddressNotEql
                        Collections.emptyList(), // filterAddressPrefix
                        Collections.emptyList(), // filterAddressNotPrefix
                        Collections.emptyList(), // filterAddressSuffix
                        Collections.emptyList(), // filterAddressNotSuffix
                        Collections.emptyList(), // filterAddressMatch
                        Collections.emptyList(), // filterAddressNotMatch
                        Collections.emptyList(), // filterAddressCareOfEq
                        Collections.emptyList(), // filterAddressCareOfNotEq
                        Collections.emptyList(), // filterAddressCareOfEql
                        Collections.emptyList(), // filterAddressCareOfNotEql
                        Collections.emptyList(), // filterAddressCareOfPrefix
                        Collections.emptyList(), // filterAddressCareOfNotPrefix
                        Collections.emptyList(), // filterAddressCareOfSuffix
                        Collections.emptyList(), // filterAddressCareOfNotSuffix
                        Collections.emptyList(), // filterAddressCareOfMatch
                        Collections.emptyList(), // filterAddressCareOfNotMatch
                        Collections.emptyList(), // filterStreetEq
                        Collections.emptyList(), // filterStreetNotEq
                        Collections.emptyList(), // filterStreetEql
                        Collections.emptyList(), // filterStreetNotEql
                        Collections.emptyList(), // filterStreetPrefix
                        Collections.emptyList(), // filterStreetNotPrefix
                        Collections.emptyList(), // filterStreetSuffix
                        Collections.emptyList(), // filterStreetNotSuffix
                        Collections.emptyList(), // filterStreetMatch
                        Collections.emptyList(), // filterStreetNotMatch
                        Collections.emptyList(), // filterHousenumberEq
                        Collections.emptyList(), // filterHousenumberNotEq
                        Collections.emptyList(), // filterHousenumberEql
                        Collections.emptyList(), // filterHousenumberNotEql
                        Collections.emptyList(), // filterHousenumberPrefix
                        Collections.emptyList(), // filterHousenumberNotPrefix
                        Collections.emptyList(), // filterHousenumberSuffix
                        Collections.emptyList(), // filterHousenumberNotSuffix
                        Collections.emptyList(), // filterHousenumberMatch
                        Collections.emptyList(), // filterHousenumberNotMatch
                        Collections.emptyList(), // filterPostboxEq
                        Collections.emptyList(), // filterPostboxNotEq
                        Collections.emptyList(), // filterPostboxEql
                        Collections.emptyList(), // filterPostboxNotEql
                        Collections.emptyList(), // filterPostboxPrefix
                        Collections.emptyList(), // filterPostboxNotPrefix
                        Collections.emptyList(), // filterPostboxSuffix
                        Collections.emptyList(), // filterPostboxNotSuffix
                        Collections.emptyList(), // filterPostboxMatch
                        Collections.emptyList(), // filterPostboxNotMatch
                        Collections.emptyList(), // filterZipCodeEq
                        Collections.emptyList(), // filterZipCodeNotEq
                        Collections.emptyList(), // filterZipCodeEql
                        Collections.emptyList(), // filterZipCodeNotEql
                        Collections.emptyList(), // filterZipCodePrefix
                        Collections.emptyList(), // filterZipCodeNotPrefix
                        Collections.emptyList(), // filterZipCodeSuffix
                        Collections.emptyList(), // filterZipCodeNotSuffix
                        Collections.emptyList(), // filterZipCodeMatch
                        Collections.emptyList(), // filterZipCodeNotMatch
                        Collections.emptyList(), // filterTownEq
                        Collections.emptyList(), // filterTownNotEq
                        Collections.emptyList(), // filterTownEql
                        Collections.emptyList(), // filterTownNotEql
                        Collections.emptyList(), // filterTownPrefix
                        Collections.emptyList(), // filterTownNotPrefix
                        Collections.emptyList(), // filterTownSuffix
                        Collections.emptyList(), // filterTownNotSuffix
                        Collections.emptyList(), // filterTownMatch
                        Collections.emptyList(), // filterTownNotMatch
                        Collections.emptyList(), // filterCountryEq
                        Collections.emptyList(), // filterCountryNotEq
                        Collections.emptyList(), // filterCountryEql
                        Collections.emptyList(), // filterCountryNotEql
                        Collections.emptyList(), // filterCountryPrefix
                        Collections.emptyList(), // filterCountryNotPrefix
                        Collections.emptyList(), // filterCountrySuffix
                        Collections.emptyList(), // filterCountryNotSuffix
                        Collections.emptyList(), // filterCountryMatch
                        Collections.emptyList(), // filterCountryNotMatch
                        Collections.emptyList(), // filterPrimaryGroupIdEq
                        Collections.emptyList(), // filterPrimaryGroupIdNotEq
                        Collections.emptyList(), // filterPrimaryGroupIdGt
                        Collections.emptyList(), // filterPrimaryGroupIdGte
                        Collections.emptyList(), // filterPrimaryGroupIdLt
                        Collections.emptyList(), // filterPrimaryGroupIdLte
                        Collections.emptyList(), // filterGenderEq
                        Collections.emptyList(), // filterGenderNotEq
                        Collections.emptyList(), // filterGenderEql
                        Collections.emptyList(), // filterGenderNotEql
                        Collections.emptyList(), // filterGenderPrefix
                        Collections.emptyList(), // filterGenderNotPrefix
                        Collections.emptyList(), // filterGenderSuffix
                        Collections.emptyList(), // filterGenderNotSuffix
                        Collections.emptyList(), // filterGenderMatch
                        Collections.emptyList(), // filterGenderNotMatch
                        Collections.emptyList(), // filterBirthdayEq (LocalDate)
                        Collections.emptyList(), // filterBirthdayNotEq (LocalDate)
                        Collections.emptyList(), // filterBirthdayGt (LocalDate)
                        Collections.emptyList(), // filterBirthdayGte (LocalDate)
                        Collections.emptyList(), // filterBirthdayLt (LocalDate)
                        Collections.emptyList(), // filterBirthdayLte (LocalDate)
                        Collections.emptyList(), // filterPictureEq
                        Collections.emptyList(), // filterPictureNotEq
                        Collections.emptyList(), // filterPictureEql
                        Collections.emptyList(), // filterPictureNotEql
                        Collections.emptyList(), // filterPicturePrefix
                        Collections.emptyList(), // filterPictureNotPrefix
                        Collections.emptyList(), // filterPictureSuffix
                        Collections.emptyList(), // filterPictureNotSuffix
                        Collections.emptyList(), // filterPictureMatch
                        Collections.emptyList(), // filterPictureNotMatch
                        Collections.emptyList(), // filterUpdatedAtEq (OffsetDateTime)
                        Collections.emptyList(), // filterUpdatedAtNotEq (OffsetDateTime)
                        Collections.emptyList(), // filterUpdatedAtGt (OffsetDateTime)
                        Collections.emptyList(), // filterUpdatedAtGte (OffsetDateTime)
                        Collections.emptyList(), // filterUpdatedAtLt (OffsetDateTime)
                        Collections.emptyList(), // filterUpdatedAtLte (OffsetDateTime)
                        Collections.emptyList(), // filterAdditionalInformationEq
                        Collections.emptyList(), // filterAdditionalInformationNotEq
                        Collections.emptyList(), // filterAdditionalInformationEql
                        Collections.emptyList(), // filterAdditionalInformationNotEql
                        Collections.emptyList(), // filterAdditionalInformationPrefix
                        Collections.emptyList(), // filterAdditionalInformationNotPrefix
                        Collections.emptyList(), // filterAdditionalInformationSuffix
                        Collections.emptyList(), // filterAdditionalInformationNotSuffix
                        Collections.emptyList(), // filterAdditionalInformationMatch
                        Collections.emptyList(), // filterAdditionalInformationNotMatch
                        Collections.<GroupsReadableAttribute>emptySet(), // fieldsGroups
                        Collections.<RolesReadableAttribute>emptySet(), // fieldsRoles
                        Collections.<PhoneNumbersReadableAttribute>emptySet(), // fieldsPhoneNumbers
                        Collections.<SocialAccountsReadableAttribute>emptySet(), // fieldsSocialAccounts
                        Collections.<AdditionalEmailsReadableAttribute>emptySet()  // fieldsAdditionalEmails
                    );
                    // Map personSingle to Person and add to personResultList
                    if (personSingle != null && personSingle.getData() != null && personSingle.getData().getAttributes() != null) {
                        PeopleResource resource = personSingle.getData();
                        People attributes = resource.getAttributes();
                        Person person = new Person();
                        person.setId(resource.getId());
                        person.setType(resource.getType() != null ? resource.getType().getValue() : null);
                        person.setFirstName(attributes.getFirstName());
                        person.setLastName(attributes.getLastName());
                        person.setNickname(attributes.getNickname());
                        person.setCompanyName(attributes.getCompanyName());
                        person.setCompany(Boolean.TRUE.equals(attributes.getCompany()));
                        person.setGender(attributes.getGender());
                        person.setEmail(attributes.getEmail());
                        person.setAddress(attributes.getAddress());
                        person.setZipCode(attributes.getZipCode());
                        person.setTown(attributes.getTown());
                        person.setCountry(attributes.getCountry());
                        person.setAdditionalInformation(attributes.getAdditionalInformation());
                        person.setPictureUrl(attributes.getPicture());
                        // Map more fields as needed
                        personResultList.add(person);
                    }
                } catch (RestClientException e) {
					LOGGER.error("API error loading person with ID " + personId + ": " + e.getMessage());
					throw e;
				}
            }
        } catch (Exception e) {
            LOGGER.error("API error loading person list: " + e.getMessage());
            throw e;
        }
        return personResultList;
    }

    /**
     * Load a person by email using a manual HTTP request to the API.
     */
    public Person loadPersonByEmail(String email) throws Exception {
        String apiUrl = ((Session) session).getBaseUrl() + "/api/people?filter[email][eq]=" + java.net.URLEncoder.encode(email, java.nio.charset.StandardCharsets.UTF_8);
        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create(apiUrl))
            .header("User-Agent", "Java 11 HttpClient Bot")
            .header("Content-Type", "application/json")
            .GET()
            .build();
        java.net.http.HttpResponse<String> response = ((Session) session).getHttpClient().send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new Exception("Failed to fetch person by email: " + response.body());
        }
        JSONObject json = new JSONObject(response.body());
        JSONArray data = json.optJSONArray("data");
        if (data == null || data.length() == 0) {
            return null;
        }
        JSONObject personObj = data.getJSONObject(0);
        JSONObject attributes = personObj.optJSONObject("attributes");
        if (attributes == null) {
            return null;
        }
        Person person = new Person();
        person.setId(personObj.optString("id"));
        person.setType(personObj.optString("type"));
        person.setFirstName(attributes.optString("first_name"));
        person.setLastName(attributes.optString("last_name"));
        person.setNickname(attributes.optString("nickname"));
        person.setCompanyName(attributes.optString("company_name"));
        person.setCompany(attributes.optBoolean("company"));
        person.setGender(attributes.optString("gender"));
        person.setEmail(attributes.optString("email"));
        person.setAddress(attributes.optString("address"));
        person.setZipCode(attributes.optString("zip_code"));
        person.setTown(attributes.optString("town"));
        person.setCountry(attributes.optString("country"));
        person.setAdditionalInformation(attributes.optString("additional_information"));
        person.setPictureUrl(attributes.optString("picture"));
        // Map more fields as needed
        return person;
    }
}
