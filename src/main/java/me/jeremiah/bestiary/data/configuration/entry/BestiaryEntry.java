package me.jeremiah.bestiary.data.configuration.entry;

import lombok.Getter;
import me.jeremiah.bestiary.data.configuration.BestiaryCategory;
import me.jeremiah.bestiary.gui.configuration.GUITemplate;
import org.bukkit.entity.LivingEntity;

@Getter
public class BestiaryEntry {

  private final BestiaryCategory category;
  private final String id;
  private final String displayName;
  private final EntityFilter entityFilter;

  private final LevelController levelController;
  private final GUITemplate guiTemplate = null;

  public BestiaryEntry(BestiaryCategory category, String id, String displayName, LevelController levelController, EntityFilter entityFilter) {
    this.category = category;
    this.id = id;
    this.displayName = displayName;
    this.levelController = levelController;
    this.entityFilter = entityFilter;
  }

  public boolean matchesFilter(LivingEntity entity) {
    return entityFilter.matches(entity);
  }

}
