package com.toxa.testplugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.tasks.TaskRepository;
import com.intellij.tasks.config.TaskRepositoryEditor;
import com.intellij.tasks.impl.BaseRepositoryType;
import com.intellij.util.Consumer;
import javax.swing.Icon;
import org.jetbrains.annotations.NotNull;

/**
 * @author khomyackov
 */
public class TargetprocessRepositoryType extends BaseRepositoryType<TargetprocessRepository> {

  @NotNull
  @Override
  public String getName() {
    return "Targetprocess";
  }

  @NotNull
  @Override
  public Icon getIcon() {
    return IconLoader.getIcon("/icons/tp.png", TargetprocessRepositoryType.class);
  }

  @NotNull
  @Override
  public TaskRepository createRepository() {
    return new TargetprocessRepository(this);
  }

  @Override
  public @NotNull TaskRepositoryEditor createEditor(TargetprocessRepository repository,
      Project project, Consumer<? super TargetprocessRepository> changeListener) {
    return new TargetprocessRepositoryEditor(project, repository, changeListener);
  }

  @Override
  public Class<TargetprocessRepository> getRepositoryClass() {
    return TargetprocessRepository.class;
  }
}
