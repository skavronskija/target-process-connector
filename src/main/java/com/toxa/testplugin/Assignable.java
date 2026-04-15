package com.toxa.testplugin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellij.openapi.util.IconLoader;
import com.intellij.tasks.Comment;
import com.intellij.tasks.Task;
import com.intellij.tasks.TaskType;
import java.util.Date;
import javax.swing.Icon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Assignable extends Task {

  public static final Icon BUG = IconLoader.getIcon("/icons/bug.svg", Assignable.class);

  public static final Icon FEATURE = IconLoader.getIcon("/icons/feature.svg", Assignable.class);

  public static final Icon OTHER = IconLoader.getIcon("/icons/other.svg", Assignable.class);

  private static String serverUrl;

  private final long id;

  private final String summary;

  private final String description;

  private final String entityType;

  private final Date createDate;

  @JsonCreator
  public Assignable(@JsonProperty("id") long id, @JsonProperty("name") String summary,
                    @JsonProperty("description") String description, @JsonProperty("entityType") String entityType,
                    @JsonProperty("createDate") Date createDate) {
    this.id = id;
    this.summary = summary;
    this.description = description;
    this.entityType = entityType;
    this.createDate = createDate;
  }

  @NotNull
  @Override
  public String getId() {
    return String.valueOf(id);
  }

  @NotNull
  @Override
  public String getPresentableId() {
    return "TP-" + id;
  }

  @NotNull
  @Override
  public String getNumber() {
    return getId();
  }

  @NotNull
  @Override
  public String getSummary() {
    return summary;
  }

  @Nullable
  @Override
  public String getDescription() {
    return description;
  }

  @NotNull
  @Override
  public Comment[] getComments() {
    return new Comment[0];
  }

  @NotNull
  @Override
  public Icon getIcon() {
    return switch (getType()) {
      case BUG -> BUG;
      case FEATURE -> FEATURE;
      default -> OTHER;
    };
  }

  @NotNull
  @Override
  public TaskType getType() {
    return switch (entityType) {
      case "Bug" -> TaskType.BUG;
      case "UserStory" -> TaskType.FEATURE;
      default -> TaskType.OTHER;
    };
  }

  @Nullable
  @Override
  public Date getUpdated() {
    return null;
  }

  @Nullable
  @Override
  public Date getCreated() {
    return createDate;
  }

  @Override
  public boolean isClosed() {
    return false;
  }

  @Override
  public boolean isIssue() {
    return true;
  }

  @Nullable
  @Override
  public String getIssueUrl() {
    return serverUrl + "/entity/" + getId();
  }

  public static void setServerUrl(String serverUrl) {
    Assignable.serverUrl = serverUrl;
  }

}
