package ch.cevi.db.client.communication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cevi.db.client.business.LoadingListner;
import ch.cevi.db.client.business.entities.Person;
import ch.cevi.db.client.business.entities.Role;
import ch.cevi.db.client.business.entities.YGroup;
import ch.cevi.db.client.configuration.Texts;

/**
 * Facade for all business operations on the hitobito server.
 * {@link Session#login(String, String)} must be called before any business
 * operations may be called!
 * 
 * @author developer
 *
 */
public class ServerFacade {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServerFacade.class.getName());

	private Session session;

	private Map<String, YGroup> groupCache = Collections.synchronizedMap(new HashMap<String, YGroup>());

	private Map<String, Person> personCache = Collections.synchronizedMap(new HashMap<String, Person>());

	public ServerFacade(Session session) {
		this.session = session;
	}

	/**
	 * Load the group with Id groupId
	 * 
	 * @param groupId
	 *            Id of the group to load
	 * @return the loaded {@link YGroup}
	 * @throws Exception
	 */
	public YGroup loadGroup(String groupId) throws Exception {
		LOGGER.info("loadGroup for groupId" + groupId);

		// Try to get group from cache
		YGroup group = groupCache.get(groupId);
		if (group != null) {
			return group;
		} else {
			synchronized (this.groupCache) {
				if (this.groupCache.get(groupId) == null) {
					group = new YGroup();
					String groupDetailsString = null;
					Person loggedInUser = session.getLoggedInUser();
					String url = String.format("%s/groups/%s.json?user_email=%s&user_token=%s", session.getBaseUrl(), groupId, loggedInUser.getEmail(), loggedInUser.getAuthenticationToken());
					groupDetailsString = session.callServerMethod(url);
					JSONObject groupeResultObject = new JSONObject(groupDetailsString);
					JSONArray groupsArray = groupeResultObject.optJSONArray("groups");
					JSONObject groupObject = groupsArray.optJSONObject(0);
					group.initBasicDataFromJSONObject(groupObject);
					group.initDetailDataFromJSONObject(groupObject);
					if (groupsArray != null && groupsArray.length() > 0) {
						JSONObject linkedObjects = groupeResultObject.getJSONObject("linked");
						if (linkedObjects != null) {
							group.setLayerGroupId(linkedObjects.optString("layer_group"));
						}
					}
					this.groupCache.put(groupId, group);
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

			// Group was loaded without details -> reinit it!
			String groupDetailsString = null;
			Person loggedInUser = session.getLoggedInUser();
			String url = String.format("%s/groups/%s.json?user_email=%s&user_token=%s", session.getBaseUrl(), groupId, loggedInUser.getEmail(), loggedInUser.getAuthenticationToken());
			groupDetailsString = session.callServerMethod(url);
			JSONObject groupResultObject = new JSONObject(groupDetailsString);
			JSONArray groupsArray = groupResultObject.optJSONArray("groups");
			JSONObject groupObject = groupsArray.optJSONObject(0);

			group.initBasicDataFromJSONObject(groupObject);
			group.initDetailDataFromJSONObject(groupObject);

			group.setParentGroupId(parentGroupId);
			group.setParentYGroup(groupCache.get(parentGroupId));

			if (groupObject != null) {
				JSONObject linkedObjects = groupResultObject.getJSONObject("linked");
				JSONObject links = groupObject.optJSONObject("links");
				JSONArray children = links != null ? links.optJSONArray("children") : null;
				List<String> childGroupIds = new ArrayList<String>();
				if (children != null) {
					for (int childGroupIndex = 0; childGroupIndex < children.length(); childGroupIndex++) {
						childGroupIds.add(children.getString(childGroupIndex));
					}
				}
				if (linkedObjects != null) {
					JSONArray childGroups = linkedObjects.optJSONArray("groups");
					if (childGroups != null) {
						for (int i = 0; i < childGroups.length(); i++) {
							JSONObject childGroupObject = childGroups.optJSONObject(i);
							YGroup childGroup = new YGroup();

							childGroup.initBasicDataFromJSONObject(childGroupObject);
							if (childGroupIds.contains(childGroup.getId())) {
								childGroup.setParentYGroup(group);
								childGroup.setParentGroupId(group.getId());
								group.addChild(childGroup);
								groupCache.put(childGroup.getId(), childGroup);
							}
						}
					}
					group.setLayerGroupId(linkedObjects.optString("layer_group"));
				}

				List<Role> groupRoles = new ArrayList<Role>();

				// loading the groupMembers is probably not allowed...
				try {
					List<Person> groupMembers = loadPersonList(groupId);
					for (Person p : groupMembers) {
						for (Role role : p.getRoles()) {
							if (group.getId().equals(role.getGroupId())) {
								role.setyGroup(group);
								groupRoles.add(role);
							}
						}
					}
				} catch (Exception e) {
					LOGGER.info("Loading groupMembers failed. Detail message: " + e.getMessage());
				}

				group.setGroupRoles(groupRoles);
				group.setFullyLoaded(true);
			}
		}

		return group;
	}

	public void completeNotFullyLoadedGroup(YGroup group) throws Exception {
		if (group.isFullyLoaded()) {
			return;
		}
		group.initFromOtherGroup(this.loadGroupWithDetails(group.getId(), group.getParentYGroupId()));
	}

	/**
	 * Load all persons of the group with the supplied groupId
	 * 
	 * @param groupId
	 *            Id of the group of which the persons should be loaded
	 * @return List of loaded {@link Person}
	 * @throws Exception
	 */

	public List<Person> loadPersonList(final String groupId) throws Exception {
		LOGGER.info("loadPersonList for groupId" + groupId);
		final List<Person> personList = Collections.synchronizedList(new ArrayList<Person>());
		final List<Exception> exceptionList = Collections.synchronizedList(new ArrayList<Exception>());
		final Person loggedInUser = session.getLoggedInUser();
		// /groups/1/people/1
		final String personListUrl = String.format("%s/groups/%s/people.json?user_email=%s&user_token=%s", session.getBaseUrl(), groupId, loggedInUser.getEmail(),
				loggedInUser.getAuthenticationToken());
		final String personListString = session.callServerMethod(personListUrl);

		JSONObject personListResultSetObject = new JSONObject(personListString);
		final JSONArray personListArray = personListResultSetObject.optJSONArray("people");
		if (personListArray.length() > 0) {
			if (personListArray != null) {
				ExecutorService detailLoaderService = Executors.newFixedThreadPool(20);
				for (int personIndex = 0; personIndex < personListArray.length(); personIndex++) {
					final int currentPersonIndex = personIndex;
					Runnable detailLoader = new Runnable() {
						@Override
						public void run() {
							try {
								loadPersonDetails(groupId, personList, loggedInUser, personListArray, currentPersonIndex);
							} catch (Exception e) {
								LOGGER.error(e.getMessage());
								exceptionList.add(e);
							}
						}
					};
					detailLoaderService.execute(detailLoader);
				}
				detailLoaderService.shutdown();
				detailLoaderService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
				if (!exceptionList.isEmpty()) {
					throw new Exception("Error while loading person details", exceptionList.get(0));
				}
			}
		}
		return personList;
	}

	private void loadPersonDetails(String groupId, List<Person> personList, Person loggedInUser, JSONArray personListArray, int personIndex) throws Exception {
		JSONObject personObject = personListArray.optJSONObject(personIndex);
		if (personObject != null) {
			Person person = new Person();
			person.initFromJSONObject(personObject);

			Person personFromCache = personCache.get(person.getId());
			if (personFromCache != null) {
				person = personFromCache;
			} else {
				String personUrl = String.format("%s/groups/%s/people/%s.json?user_email=%s&user_token=%s", session.getBaseUrl(), groupId, person.getId(), loggedInUser.getEmail(),
						loggedInUser.getAuthenticationToken());
				String personString = session.callServerMethod(personUrl);
				JSONObject personResultSetObject = new JSONObject(personString);
				JSONArray personArray = personResultSetObject.optJSONArray("people");
				JSONObject detailedPersonObject = personArray.optJSONObject(0);
				person.initFromJSONObject(detailedPersonObject);

				JSONObject linkedObject = personResultSetObject.optJSONObject("linked");
				JSONArray phoneNumbersArray = linkedObject.optJSONArray("phone_numbers");
				JSONArray rolesArray = linkedObject.optJSONArray("roles");
				JSONArray additionalMailsArray = linkedObject.optJSONArray("additional_emails");
				JSONArray socialAccountsArray = linkedObject.optJSONArray("social_accounts");

				JSONObject linksObject = detailedPersonObject.optJSONObject("links");
				if (linksObject != null) {
					person.addPhoneNumbers(linksObject, phoneNumbersArray);
					person.addRoles(linksObject, rolesArray);
					person.addAdditionalMails(linksObject, additionalMailsArray);
					person.addSocialAccounts(linksObject, socialAccountsArray);
				}
				// person.setLocalDepartments(findLocalDepartmentsForPerson(person));
				personCache.put(person.getId(), person);
			}
			personList.add(person);
		}
	}
}
