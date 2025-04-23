package org.stablerpg.stablebestiary.data.configuration;

import lombok.Getter;
import org.stablerpg.stablebestiary.gui.configuration.GUITemplate;

@Getter
public class BestiarySubcategory extends BestiaryCategory {

  private final BestiaryCategory superCategory;

  public BestiarySubcategory(BestiaryCategory superCategory, String id, GUITemplate GUITemplate) {
    super(id, GUITemplate);
    this.superCategory = superCategory;
  }

}
