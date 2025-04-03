package me.jeremiah.bestiary.data.configuration;

import lombok.Getter;
import me.jeremiah.bestiary.gui.configuration.GUITemplate;

@Getter
public class BestiarySubcategory extends BestiaryCategory {

  private final BestiaryCategory superCategory;

  public BestiarySubcategory(BestiaryCategory superCategory, String id, GUITemplate GUITemplate) {
    super(id, GUITemplate);
    this.superCategory = superCategory;
  }

  @Override
  public String getId() {
    return super.getId();
  }

}
