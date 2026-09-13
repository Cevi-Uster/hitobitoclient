package ch.cevi.db.client.communication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import ch.cevi.db.client.business.LoadingListner;
import ch.cevi.db.client.business.entities.Person;
import ch.cevi.db.client.business.entities.Role;
import ch.cevi.db.client.business.entities.YGroup;
import ch.cevi.db.client.configuration.Texts;

/**
 * Facade for all business operations on the Hitobito JSON:API.
 * {@link ISession#login()} must be called before any business operations may
 * be called.
 * <p>
 * The generated {@code GroupsApi}/{@code PeopleApi}/{@code RolesApi} classes
 * are not used here: their methods take upward of 250 positional filter
 * parameters (one per filterable field per comparator) and cannot paginate,
 * since the spec declares no {@code page[]} parameters at all. Instead, every
 * call goes through {@link JsonApiClient}, which builds requests from a
 * readable query map and hands back the parsed JSON:API document.
 *
 * @author developer
 */
public class ServerFacade {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServerFacade.class.getName());

	/** Max ids per batched {@code filter[id][eq]=1,2,3} people lookup. */
	private static final int PEOPLE_BATCH_SIZE = 50;

	private final ISession session;

	private final Map<String, YGroup> groupCache = Collections.synchronizedMap(new HashMap<String, YGroup>());

	private final Map<String, Person> personCache = Collections.synchronizedMap(new HashMap<String, Person>());

	public ServerFacade(ISession session) {
		this.session = session;
	}

	private JsonApiClient jsonApi() {
		return session.getJsonApiClient();
	}

	/**
	 * Loads the group with id {@code groupId}, without children or members.
	 */
	public YGroup loadGroup(String groupId) throws Exception {
		LOGGER.info("loadGroup for groupId " + groupId);
		YGroup cached = groupCache.get(groupId);
		if (cached != null) {
			return cached;
		}
		synchronized (this.groupCache) {
			cached = groupCache.get(groupId);
			if (cached != null) {
				return cached;
			}
			YGroup group = new YGroup();
			JSONObject resource = jsonApi().get("/groups/" + groupId).optJSONObject("data");
			group.initBasicDataFromJSONObject(resource);
			group.initDetailDataFromJSONObject(resource);
			groupCache.put(groupId, group);
			return group;
		}
	}

	/**
	 * Loads the root {@link YGroup}.
	 */
	public YGroup loadMainGroupWithDetails() throws Exception {
		return loadGroupWithDetails(session.getMainGroupId(), null);
	}

	/**
	 * Loads all missing children of the {@link YGroup} <code>parentGroup</code>
	 * and adds them to the group.
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
	 * Loads the group, its direct children and its members (with roles) for
	 * the group with id {@code groupId}.
	 * <p>
	 * The new API has no {@code children} or {@code roles} include on groups
	 * (unlike the legacy API's compound documents), so this issues three
	 * separate JSON:API calls: the group itself, {@code
	 * groups?filter[parent_id][eq]=groupId} for children, and {@code
	 * roles?filter[group_id][eq]=groupId}, batch-resolved against {@code
	 * people?filter[id][eq]=...} for members.
	 *
	 * @param groupId       Id of the group to load
	 * @param parentGroupId parent of the group which should be loaded. Can be
	 *                      null.
	 * @return the loaded {@link YGroup}
	 */
	public YGroup loadGroupWithDetails(String groupId, String parentGroupId) throws Exception {
		LOGGER.info("loadGroupWithDetails for groupId " + groupId);

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
		if (group.isFullyLoaded()) {
			return group;
		}

		JSONObject resource = jsonApi().get("/groups/" + groupId).optJSONObject("data");
		group.initBasicDataFromJSONObject(resource);
		group.initDetailDataFromJSONObject(resource);
		group.setParentGroupId(parentGroupId);
		group.setParentYGroup(groupCache.get(parentGroupId));

		group.setChildren(loadChildGroups(group));
		group.setGroupRoles(loadGroupRoles(group));
		group.setFullyLoaded(true);

		return group;
	}

	public void completeNotFullyLoadedGroup(YGroup group) throws Exception {
		if (group.isFullyLoaded()) {
			return;
		}
		group.initFromOtherGroup(this.loadGroupWithDetails(group.getId(), group.getParentYGroupId()));
	}

	private List<YGroup> loadChildGroups(YGroup group) throws Exception {
		MultiValueMap<String, String> query = new LinkedMultiValueMap<>();
		query.add("filter[parent_id][eq]", group.getId());
		List<JSONObject> pages = jsonApi().getAllPages("/groups", query);

		List<YGroup> children = new ArrayList<>();
		for (JSONObject page : pages) {
			for (JSONObject childResource : dataArrayOf(page)) {
				String childId = childResource.optString("id", null);
				if (childId == null) {
					continue;
				}
				YGroup childGroup = groupCache.computeIfAbsent(childId, id -> new YGroup());
				childGroup.initBasicDataFromJSONObject(childResource);
				childGroup.initDetailDataFromJSONObject(childResource);
				childGroup.setParentYGroup(group);
				childGroup.setParentGroupId(group.getId());
				children.add(childGroup);
			}
		}
		return children;
	}

	/**
	 * Loads every role in this group (i.e. its members) together with the
	 * person each role belongs to, wiring the Person&lt;-&gt;Role&lt;-&gt;Group
	 * graph in both directions: after this call, {@code role.getPerson()},
	 * {@code person.getRoles()} and {@code group.getGroupRoles()} are all
	 * populated and refer to the same {@link Role}/{@link Person} instances.
	 */
	private List<Role> loadGroupRoles(YGroup group) throws Exception {
		MultiValueMap<String, String> query = new LinkedMultiValueMap<>();
		query.add("filter[group_id][eq]", group.getId());
		List<JSONObject> pages = jsonApi().getAllPages("/roles", query);

		List<Role> roles = new ArrayList<>();
		Map<Role, String> personIdByRole = new LinkedHashMap<>();
		Set<String> personIds = new LinkedHashSet<>();
		for (JSONObject page : pages) {
			for (JSONObject roleResource : dataArrayOf(page)) {
				Role role = new Role();
				role.initFromJSONObject(roleResource);
				role.setyGroup(group);
				String personId = attributeOf(roleResource, "person_id");
				if (personId != null) {
					personIdByRole.put(role, personId);
					personIds.add(personId);
				}
				roles.add(role);
			}
		}

		Map<String, Person> peopleById = loadPeopleWithContactDetails(personIds);
		for (Role role : roles) {
			Person person = peopleById.get(personIdByRole.get(role));
			if (person != null) {
				role.setPerson(person);
				person.addRole(role);
			}
		}
		return roles;
	}

	/**
	 * Loads all persons that hold a role in the group with the supplied
	 * groupId, with their phone numbers, social accounts and additional
	 * emails.
	 */
	public List<Person> loadPersonList(final String groupId) throws Exception {
		LOGGER.info("loadPersonList for groupId " + groupId);
		Set<String> personIds = new LinkedHashSet<>();
		MultiValueMap<String, String> query = new LinkedMultiValueMap<>();
		query.add("filter[group_id][eq]", groupId);
		for (JSONObject page : jsonApi().getAllPages("/roles", query)) {
			for (JSONObject roleResource : dataArrayOf(page)) {
				String personId = attributeOf(roleResource, "person_id");
				if (personId != null) {
					personIds.add(personId);
				}
			}
		}
		return new ArrayList<>(loadPeopleWithContactDetails(personIds).values());
	}

	/**
	 * Loads (and caches) the given people, with their phone numbers, social
	 * accounts and additional emails, in batches of {@link
	 * #PEOPLE_BATCH_SIZE} ids per request (the spec declares filter
	 * parameters as comma-joined arrays, so this replaces what used to be one
	 * HTTP request per person).
	 */
	private Map<String, Person> loadPeopleWithContactDetails(Set<String> personIds) throws Exception {
		Map<String, Person> result = new LinkedHashMap<>();
		List<String> toFetch = new ArrayList<>();
		for (String personId : personIds) {
			Person cached = personCache.get(personId);
			if (cached != null) {
				result.put(personId, cached);
			} else {
				toFetch.add(personId);
			}
		}
		for (int start = 0; start < toFetch.size(); start += PEOPLE_BATCH_SIZE) {
			List<String> batch = toFetch.subList(start, Math.min(start + PEOPLE_BATCH_SIZE, toFetch.size()));
			MultiValueMap<String, String> query = new LinkedMultiValueMap<>();
			query.add("filter[id][eq]", String.join(",", batch));
			query.add("include", "phone_numbers,social_accounts,additional_emails");
			JsonApiClient.Document peopleDocument = jsonApi().getAllPagesMerged("/people", query);
			for (JSONObject personResource : peopleDocument.data()) {
				Person person = new Person();
				person.initFromJsonApiResource(personResource, peopleDocument.included());
				personCache.put(person.getId(), person);
				result.put(person.getId(), person);
			}
		}
		return result;
	}

	private static List<JSONObject> dataArrayOf(JSONObject document) {
		List<JSONObject> resources = new ArrayList<>();
		JSONArray dataArray = document.optJSONArray("data");
		if (dataArray != null) {
			for (int i = 0; i < dataArray.length(); i++) {
				JSONObject resource = dataArray.optJSONObject(i);
				if (resource != null) {
					resources.add(resource);
				}
			}
		}
		return resources;
	}

	private static String attributeOf(JSONObject resource, String attributeName) {
		JSONObject attributes = resource.optJSONObject("attributes");
		if (attributes == null) {
			return null;
		}
		String value = attributes.optString(attributeName, null);
		return (value == null || value.isEmpty()) ? null : value;
	}
}
