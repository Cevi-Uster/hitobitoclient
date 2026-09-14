package ch.cevi.db.client.communication;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;

import ch.cevi.db.client.api.invoker.ApiClient;

/**
 * Thin JSON:API query layer on top of the generated {@link ApiClient}.
 * <p>
 * The generated {@code *Api} classes are impractical to call directly (~250
 * positional filter parameters per method, no pagination support) and their
 * {@code included} handling is untyped ({@code JsonapiResource.getAttributes()}
 * is hard-coded to the wrong model class regardless of the actual resource
 * type). This class instead builds requests with a readable query-parameter
 * map and returns the raw JSON:API document as an {@link JSONObject}, which
 * callers navigate themselves (this is also how {@code included} resources
 * and their relationships/attributes are read, correctly, everywhere in the
 * data layer).
 * <p>
 * Requests are made via {@link ApiClient#invokeAPI} so they reuse its base
 * path, default headers/cookies (in particular the {@code _session_id}
 * cookie set at login) and retry configuration; the response is requested as
 * a plain {@code String} (read by Spring's built-in {@code
 * StringHttpMessageConverter}, which accepts any content type) and parsed
 * with {@code org.json}, avoiding any dependency on Jackson's handling of the
 * {@code application/vnd.api+json} media type.
 */
public class JsonApiClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(JsonApiClient.class.getName());

	private static final int DEFAULT_PAGE_SIZE = 100;

	private static final List<MediaType> JSON_API_ACCEPT = List.of(MediaType.valueOf("application/vnd.api+json"));

	private final ApiClient apiClient;

	private final HttpClient httpClient;

	private final Consumer<HttpRequest.Builder> authDecorator;

	/**
	 * @param apiClient     generated client, already configured with base path
	 *                      and auth (cookie or header) default(s)
	 * @param httpClient    used only to follow {@code links.next} URLs (resolved
	 *                      against {@code apiClient}'s base path if relative),
	 *                      which {@link ApiClient}'s base-path composition
	 *                      cannot be pointed at directly
	 * @param authDecorator applies whatever auth {@code ApiClient} was given
	 *                      (currently the {@code _session_id} cookie) to a
	 *                      request built directly against {@code httpClient}
	 */
	public JsonApiClient(ApiClient apiClient, HttpClient httpClient, Consumer<HttpRequest.Builder> authDecorator) {
		this.apiClient = apiClient;
		this.httpClient = httpClient;
		this.authDecorator = authDecorator;
	}

	/**
	 * GETs a single JSON:API document, e.g. {@code get("/groups/1",
	 * query.with("include", "layer_group"))}.
	 */
	public JSONObject get(String path, MultiValueMap<String, String> query) throws Exception {
		try {
			HttpHeaders headers = new HttpHeaders();
			org.springframework.http.ResponseEntity<String> response = apiClient.invokeAPI(
					path,
					HttpMethod.GET,
					new HashMap<>(),
					query != null ? query : new LinkedMultiValueMap<>(),
					null,
					headers,
					new LinkedMultiValueMap<>(),
					new LinkedMultiValueMap<>(),
					JSON_API_ACCEPT,
					null,
					new String[0],
					new org.springframework.core.ParameterizedTypeReference<String>() {
					});
			return new JSONObject(response.getBody());
		} catch (RestClientException e) {
			throw new Exception("JSON:API request to " + path + " failed: " + e.getMessage(), e);
		}
	}

	public JSONObject get(String path) throws Exception {
		return get(path, new LinkedMultiValueMap<>());
	}

	/**
	 * GETs every page of a collection endpoint, following {@code links.next}
	 * until it is absent. The first request gets a {@code page[size]} added if
	 * the caller didn't specify one; the spec doesn't declare pagination
	 * parameters at all, but Graphiti (the JSON:API resource layer Hitobito is
	 * built on) honors them regardless and always returns {@code links.next}.
	 */
	public List<JSONObject> getAllPages(String path, MultiValueMap<String, String> query) throws Exception {
		List<JSONObject> pages = new ArrayList<>();
		MultiValueMap<String, String> firstQuery = new LinkedMultiValueMap<>(query != null ? query : Map.of());
		if (!firstQuery.containsKey("page[size]")) {
			firstQuery.add("page[size]", String.valueOf(DEFAULT_PAGE_SIZE));
		}
		JSONObject page = get(path, firstQuery);
		pages.add(page);
		String next = nextLink(page);
		while (next != null) {
			page = getAbsoluteUrl(next);
			pages.add(page);
			next = nextLink(page);
		}
		LOGGER.debug("Fetched {} page(s) for {}", pages.size(), path);
		return pages;
	}

	/**
	 * Merges the {@code data} arrays of every page returned by {@link
	 * #getAllPages} into one list, and merges all {@code included} arrays into
	 * one type+id-indexed map via {@link #indexIncluded}.
	 */
	public record Document(List<JSONObject> data, Map<String, JSONObject> included) {
	}

	public Document getAllPagesMerged(String path, MultiValueMap<String, String> query) throws Exception {
		List<JSONObject> pages = getAllPages(path, query);
		List<JSONObject> data = new ArrayList<>();
		Map<String, JSONObject> included = new HashMap<>();
		for (JSONObject page : pages) {
			JSONArray dataArray = page.optJSONArray("data");
			if (dataArray != null) {
				for (int i = 0; i < dataArray.length(); i++) {
					JSONObject resource = dataArray.optJSONObject(i);
					if (resource != null) {
						data.add(resource);
					}
				}
			}
			included.putAll(indexIncluded(page));
		}
		return new Document(data, included);
	}

	private JSONObject getAbsoluteUrl(String url) throws Exception {
		try {
			URI uri = URI.create(url);
			if (!uri.isAbsolute()) {
				uri = URI.create(apiClient.getBasePath()).resolve(uri);
			}
			HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
					.uri(uri)
					.header("Accept", "application/vnd.api+json")
					.GET();
			authDecorator.accept(requestBuilder);
			HttpResponse<String> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() != 200) {
				throw new Exception("JSON:API request to " + url + " failed with status " + response.statusCode());
			}
			return new JSONObject(response.body());
		} catch (IOException | InterruptedException e) {
			throw new Exception("JSON:API request to " + url + " failed", e);
		}
	}

	private static String nextLink(JSONObject document) {
		JSONObject links = document.optJSONObject("links");
		if (links == null) {
			return null;
		}
		Object next = links.opt("next");
		if (next instanceof String s && !s.isBlank()) {
			return s;
		}
		if (next instanceof JSONObject o) {
			String href = o.optString("href", null);
			return (href != null && !href.isBlank()) ? href : null;
		}
		return null;
	}

	/**
	 * Indexes a JSON:API document's {@code included} array by {@code type:id},
	 * so relationship data ({@code {"type": "...", "id": "..."}}) can be
	 * resolved to the full included resource.
	 */
	public static Map<String, JSONObject> indexIncluded(JSONObject document) {
		Map<String, JSONObject> byTypeAndId = new HashMap<>();
		JSONArray included = document.optJSONArray("included");
		if (included != null) {
			for (int i = 0; i < included.length(); i++) {
				JSONObject resource = included.optJSONObject(i);
				if (resource != null) {
					byTypeAndId.put(key(resource.optString("type"), resource.optString("id")), resource);
				}
			}
		}
		return byTypeAndId;
	}

	public static String key(String type, String id) {
		return type + ":" + id;
	}

	/**
	 * Resolves a single relationship's linkage object ({@code
	 * relationships.<name>.data}) against an included-resource index.
	 */
	public static JSONObject resolveToOne(JSONObject resource, String relationshipName, Map<String, JSONObject> included) {
		JSONObject relationships = resource.optJSONObject("relationships");
		if (relationships == null) {
			return null;
		}
		JSONObject relationship = relationships.optJSONObject(relationshipName);
		if (relationship == null) {
			return null;
		}
		JSONObject data = relationship.optJSONObject("data");
		if (data == null) {
			return null;
		}
		return included.get(key(data.optString("type"), data.optString("id")));
	}

	/**
	 * Resolves a to-many relationship's linkage array ({@code
	 * relationships.<name>.data}, a JSON:API array of {@code {type, id}}
	 * pairs) against an included-resource index. Linkages whose target
	 * wasn't fetched via {@code include=...} (so isn't in {@code included})
	 * are silently skipped rather than failing the whole resource.
	 */
	public static List<JSONObject> resolveToMany(JSONObject resource, String relationshipName, Map<String, JSONObject> included) {
		List<JSONObject> result = new ArrayList<>();
		JSONObject relationships = resource.optJSONObject("relationships");
		if (relationships == null) {
			return result;
		}
		JSONObject relationship = relationships.optJSONObject(relationshipName);
		if (relationship == null) {
			return result;
		}
		JSONArray dataArray = relationship.optJSONArray("data");
		if (dataArray == null) {
			return result;
		}
		for (int i = 0; i < dataArray.length(); i++) {
			JSONObject linkage = dataArray.optJSONObject(i);
			if (linkage != null) {
				JSONObject resolved = included.get(key(linkage.optString("type"), linkage.optString("id")));
				if (resolved != null) {
					result.add(resolved);
				}
			}
		}
		return result;
	}
}
