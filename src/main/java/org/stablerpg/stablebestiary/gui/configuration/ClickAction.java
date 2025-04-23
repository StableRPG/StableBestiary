package org.stablerpg.stablebestiary.gui.configuration;

import lombok.Getter;

@Getter
public class ClickAction {

  private final Type type;

  public ClickAction(Type type) {
    this.type = type;
  }

  public enum Type {
    NONE,
    CLOSE,
    BACK,
    PREVIOUS_PAGE,
    NEXT_PAGE,
  }

}
