package com.toxa.testplugin;

import com.intellij.openapi.project.Project;
import com.intellij.tasks.config.BaseRepositoryEditor;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.Consumer;
import com.intellij.util.ui.FormBuilder;
import javax.swing.JComponent;
import javax.swing.JPasswordField;
import org.jetbrains.annotations.Nullable;

public class TargetprocessRepositoryEditor extends BaseRepositoryEditor<TargetprocessRepository> {

  private JPasswordField myAccessTokenField;

  public TargetprocessRepositoryEditor(Project project, TargetprocessRepository repository,
      Consumer<? super TargetprocessRepository> changeListener) {
    super(project, repository, changeListener);

    myAccessTokenField.setText(repository.getAccessToken());

    myPasswordText.setVisible(false);
    myPasswordLabel.setVisible(false);
    myUserNameText.setVisible(false);
    myUsernameLabel.setVisible(false);
  }

  @Override
  @Nullable
  protected JComponent createCustomPanel() {
    myAccessTokenField = new JPasswordField();
    installListener(myAccessTokenField);
    return FormBuilder.createFormBuilder()
        .addLabeledComponent(new JBLabel("Access token:"), myAccessTokenField)
        .getPanel();
  }

  @Override
  protected void afterTestConnection(boolean connectionSuccessful) {
    super.afterTestConnection(connectionSuccessful);
  }

  @Override
  public void apply() {
    super.apply();
    myRepository.setAccessToken(String.valueOf(myAccessTokenField.getPassword()));
  }
}
