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
					group = new YGroup();
					String url = String.format("%s/api/groups/%s", session.getBaseUrl(), groupId);
					String groupDetailsString = session.callServerMethod(url);
					JSONObject groupResultObject = new JSONObject(groupDetailsString);
					JSONObject groupObject = groupResultObject.optJSONObject("data");
					if (groupObject != null) {
						group.initBasicDataFromJSONObject(groupObject);
						group.initDetailDataFromJSONObject(groupObject);
						// Layer group and other relationships can be parsed from 'relationships' field if present
						JSONObject relationships = groupObject.optJSONObject("relationships");
						if (relationships != null && relationships.has("layer_group")) {
							group.setLayerGroupId(relationships.optJSONObject("layer_group").optString("id"));
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

				int loadPersonListRetryCount = 0;
				while (loadPersonListRetryCount < 3) {
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
						break;
					} catch (Exception e) {
						LOGGER.info("Loading groupMembers failed on retry number: " + loadPersonListRetryCount +" Detail message: " + e.getMessage());
						loadPersonListRetryCount ++;
						if (loadPersonListRetryCount >=3) {
							LOGGER.info("Retry count exeeded. Give up loading group member for group: " + groupId);
						}
					}	
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
	 * Load all persons of the group with the supplied groupId using the new API.
	 */
	public List<Person> loadPersonList(final String groupId) throws Exception {
		LOGGER.info("loadPersonList for groupId" + groupId);
		final List<Person> personResultList = Collections.synchronizedList(new ArrayList<Person>());
		final String personListUrl = String.format("%s/api/groups/%s/people", session.getBaseUrl(), groupId);
		try {
			final String personListString = session.callServerMethod(personListUrl);
			JSONObject personListResultSetObject = new JSONObject(personListString);
			JSONArray personListArray = personListResultSetObject.optJSONArray("data");
			if (personListArray != null && personListArray.length() > 0) {
				for (int personIndex = 0; personIndex < personListArray.length(); personIndex++) {
					JSONObject personObject = personListArray.optJSONObject(personIndex);
					loadPersonDetails(groupId, personResultList, personObject);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Could not load person list from URL: " + personListUrl);
			throw e;
		}
		return personResultList;
	}

	private void loadPersonDetails(String groupId, List<Person> personList, JSONObject personObject) throws Exception {
		if (personObject != null) {
			Person person = new Person();
			person.initFromJSONObject(personObject);
			Person personFromCache = personCache.get(person.getId());
			if (personFromCache != null) {
				person = personFromCache;
			} else {
				String personUrl = String.format("%s/api/groups/%s/people/%s", session.getBaseUrl(), groupId, person.getId());
				String personString = session.callServerMethod(personUrl);
				JSONObject personResultSetObject = new JSONObject(personString);
				JSONObject detailedPersonObject = personResultSetObject.optJSONObject("data");
				person.initFromJSONObject(detailedPersonObject);
				// Relationships and included data can be parsed from 'relationships' or 'included' fields if present
				personCache.put(person.getId(), person);
			}
			personList.add(person);
		}
	}
}