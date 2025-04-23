package org.stablerpg.stablebestiary.data.configuration.entry;

import lombok.Getter;
import org.bukkit.entity.LivingEntity;
import org.stablerpg.stablebestiary.data.configuration.BestiaryCategory;
import org.stablerpg.stablebestiary.gui.configuration.GUITemplate;

@Getter
public class BestiaryEntry {

  private final BestiaryCategory category;
  private final String id;
  private final String displayName;
  private final EntityFilter entityFilter;

  private final LevelController levelController;
  private final GUITemplate guiTemplate = null;

  public BestiaryEntry(BestiaryCategory category, String id, String displayName, EntityFilter entityFilter, LevelController levelController) {
    this.category = category;
    this.id = id;
    this.displayName = displayName;
    this.entityFilter = entityFilter;
    this.levelController = levelController;
  }

  public boolean matchesFilter(LivingEntity entity) {
    return entityFilter.matches(entity);
  }

}
