package com.toxa.testplugin;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.tasks.Task;
import com.intellij.tasks.TaskRepositoryType;
import com.intellij.tasks.impl.BaseRepository;
import com.intellij.tasks.impl.httpclient.NewBaseRepositoryImpl;
import com.intellij.util.xmlb.annotations.Tag;
import groovy.util.logging.Slf4j;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Slf4j
@Tag("Targetprocess")
public class TargetprocessRepository extends NewBaseRepositoryImpl {

  public static final String API_PATH = "/api/v2/assignable";
  private static final Logger LOG = Logger.getInstance(TargetprocessRepository.class);
  private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
                                                              .configure(
                                                                  DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                                                                  false)
                                                              .build();

  public static final String FIELDS = "id,name,description,createDate,entityType:entityType.name";

  private String accessToken = "";

  @SuppressWarnings("UnusedDeclaration")
  public TargetprocessRepository() {
    setUseHttpAuthentication(false);
  }

  public TargetprocessRepository(TaskRepositoryType type) {
    super(type);
    setUseHttpAuthentication(false);
  }

  public TargetprocessRepository(TargetprocessRepository other) {
    super(other);
    accessToken = other.getAccessToken();
  }

  @Override
  public Task[] getIssues(@Nullable String query, int offset, int limit, boolean withClosed,
                          @NotNull ProgressIndicator cancelled) throws Exception {
    Assignable.setServerUrl(getUrl());
    URI requestUrl = uriBuilder(query, offset, limit).build();
    return getIssues(requestUrl);
  }

  private Task[] getIssues(URI requestUrl) throws Exception {
    HttpClient client = getHttpClient();
    HttpGet request = new HttpGet(requestUrl);

    try {
      ResponseHandler<String> responseHandler = new BasicResponseHandler();
      String responseString = client.execute(request, responseHandler);

      TargetprocessResponse response = OBJECT_MAPPER.readValue(responseString, TargetprocessResponse.class);
      Assignable[] assignables = response.getItems();
      LOG.info("Received " + assignables.length + " entities");
      return assignables;
    } catch (Exception e) {
      LOG.error("Error reading response", e);
      throw e;
    } finally {
      request.releaseConnection();
    }
  }

  @Override
  @NotNull
  public String getRestApiPathPrefix() {
    return API_PATH;
  }

  @Nullable
  @Override
  public CancellableConnection createCancellableConnection() {
    String testUrl = uriBuilder(null, 0, 5).toString();
    return new HttpTestConnection(new HttpGet(testUrl));
  }

  @Nullable
  @Override
  public Task findTask(@NotNull String id) throws Exception {
    LOG.info("Find asi with id '" + id + "'");
    if (StringUtil.isEmpty(id)) {
      return null;
    }

    URI requestUrl = getUri(id);
    Task[] tasks = getIssues(requestUrl);
    return switch (tasks.length) {
      case 0 -> null;
      case 1 -> tasks[0];
      default -> {
        LOG.warn("Expected unique entity for id '" + id + "', got " + tasks.length + " instead. Using the first one.");
        yield tasks[0];
      }
    };
  }

  @Nullable
  @Override
  public String extractId(@NotNull String taskName) {
    String id = taskName.replaceAll("TP-", "");
    LOG.info("Extract id: entity '" + taskName + "' -> id '" + id + "'");
    return id;
  }

  @NotNull
  @Override
  public BaseRepository clone() {
    return new TargetprocessRepository(this);
  }

  @Override
  protected int getFeatures() {
    return super.getFeatures() | NATIVE_SEARCH;
  }

  @NotNull
  public URI getUri(@NotNull String id) throws URISyntaxException {
    return uriBuilder(List.of("(id==" + id + ")")).build();
  }

  @NotNull
  public URIBuilder uriBuilder(@Nullable String query, int offset, int limit) {
    List<String> where = new ArrayList<>();
    if (StringUtil.isNotEmpty(query)) {
      where.add("(name.contains('" + query + "') or id.ToString().contains('" + query + "'))");
    } else {
      where.add("(entityState.isFinal==false)");
    }

    URIBuilder url = uriBuilder(where);
    if (offset != 0 || limit != 0) {
      url.addParameter("take", String.valueOf(limit));
      url.addParameter("skip", String.valueOf(offset));
    }

    return url;
  }

  @NotNull
  private URIBuilder uriBuilder(List<String> where) {
    try {
      URIBuilder uriBuilder = new URIBuilder(getUrl() + API_PATH);

      if (StringUtil.isNotEmpty(accessToken)) {
        uriBuilder.addParameter("access_token", accessToken);
      }
      uriBuilder.addParameter("select", "{" + FIELDS + "}");
      uriBuilder.addParameter("where", String.join("and", where));
      uriBuilder.addParameter("filter", "?AssignedUser.Where(it is Me)");
      uriBuilder.addParameter("dateFormat", "iso");
      uriBuilder.addParameter("orderBy", "id desc");

      return uriBuilder;
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    TargetprocessRepository that = (TargetprocessRepository) o;
    return accessToken != null ? accessToken.equals(that.accessToken) : that.accessToken != null;
  }
}
