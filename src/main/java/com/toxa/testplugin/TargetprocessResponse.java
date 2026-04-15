package com.toxa.testplugin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TargetprocessResponse {

  private final Assignable[] items;

  @JsonCreator
  public TargetprocessResponse(@JsonProperty("items") Assignable[] items) {
    this.items = items;
  }

  public Assignable[] getItems() {
    return items;
  }

}
