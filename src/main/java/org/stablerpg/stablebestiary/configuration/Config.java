package org.stablerpg.stablebestiary.configuration;

import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stablerpg.stablebestiary.data.configuration.BestiaryCategory;
import org.stablerpg.stablebestiary.data.configuration.BestiarySubcategory;
import org.stablerpg.stablebestiary.data.configuration.entry.BestiaryEntry;
import org.stablerpg.stablebestiary.data.configuration.entry.EntityFilter;
import org.stablerpg.stablebestiary.data.configuration.entry.LevelController;
import org.stablerpg.stablebestiary.data.configuration.entry.rewards.ExpReward;
import org.stablerpg.stablebestiary.data.configuration.entry.rewards.LevelReward;
import org.stablerpg.stablebestiary.data.configuration.entry.rewards.MoneyReward;
import org.stablerpg.stablebestiary.gui.configuration.GUITemplate;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
public final class Config extends AbstractConfig {

  private BestiaryCategory mainCategory;

  private Map<String, BestiaryCategory> categoriesMap;
  private Map<String, BestiaryEntry> entryMap;

  public Config(@NotNull JavaPlugin plugin) {
    super(plugin,"config.yml");
    categoriesMap = new HashMap<>();
    entryMap = new HashMap<>();
  }

  @Override
  public void load() {
    super.load();
    categoriesMap.clear();
    entryMap.clear();
    saveResourceDirectory("/categories/");
    saveResourceDirectory("/entries/");
    mainCategory = loadCategory(null, "main");
  }

  @SneakyThrows
  private void saveResourceDirectory(String dir) {
    File resourceDir = new File(getPlugin().getDataFolder(), dir);
    if (resourceDir.exists())
      return;
    resourceDir.mkdirs();

    String resourcePath = dir.startsWith("/") ? dir.substring(1) : dir;

    try {
      URL jarUrl = getPlugin().getClass().getProtectionDomain().getCodeSource().getLocation();
      if (jarUrl == null) {
        getLogger().warning("Could not locate JAR file to extract resources");
        return;
      }

      Map<String, String> env = new HashMap<>();
      env.put("create", "false");

      URI jarUri = new URI("jar:" + jarUrl.toURI());
      try (FileSystem zipfs = FileSystems.newFileSystem(jarUri, env)) {
        Path dirPath = zipfs.getPath(resourcePath);

        if (Files.exists(dirPath) && Files.isDirectory(dirPath)) {
          Files.walk(dirPath, 1)
            .filter(path -> !path.equals(dirPath))
            .forEach(path -> {
              String resourceName = resourcePath + path.getFileName();
              getPlugin().saveResource(resourceName, false);
            });
        } else {
          getLogger().warning("Directory " + resourcePath + " not found in JAR");
        }
      }
    } catch (Exception e) {
      getLogger().severe("Failed to extract directory " + dir + ": " + e.getMessage());
      e.printStackTrace();
    }
  }

  private BestiaryCategory loadCategory(@Nullable BestiaryCategory superCategory, @NotNull String categoryId) {
    File categoryFile = new File(getPlugin().getDataFolder(), "/categories/" + categoryId + ".yml");
    if (!categoryFile.exists()) {
      getLogger().warning("Category file " + categoryId + ".yml does not exist.");
      return null;
    }
    YamlConfiguration configuration = YamlConfiguration.loadConfiguration(categoryFile);
    String id = configuration.getString("category.id", categoryId);

    ConfigurationSection guiSection = configuration.getConfigurationSection("category.gui");
    if (guiSection == null) {
      getLogger().warning("GUI configuration not found for %s category.".formatted(categoryId));
      return null;
    }

    GUITemplate guiTemplate = Deserializer.deserializeGUITemplate(guiSection);

    BestiaryCategory category;
    if (superCategory == null)
      category = new BestiaryCategory(id, guiTemplate);
    else
      category = new BestiarySubcategory(superCategory, id, guiTemplate);

    List<String> subcategoryIds = configuration.getStringList("category.subcategories");
    for (String subcategoryId : subcategoryIds) {
      BestiarySubcategory subcategory = (BestiarySubcategory) loadCategory(category, subcategoryId);
      if (subcategory != null)
        category.addSubcategory(subcategory);
      else
        getLogger().warning("Failed to load subcategory with id " + subcategoryId + " for " + categoryId + " category.");
    }

    List<String> entryIds = configuration.getStringList("category.entries");
    for (String entryId : entryIds) {
      BestiaryEntry entry = loadEntry(category, entryId);
      if (entry != null)
        category.addEntry(entry);
      else
        getLogger().warning("Failed to load subcategory with id " + entryId + " for " + categoryId + " category.");
    }

    categoriesMap.put(categoryId, category);
    return category;
  }

  private @Nullable BestiaryEntry loadEntry(BestiaryCategory category, String entryId) {
    File entryFile = new File(getPlugin().getDataFolder(), "/entries/" + entryId + ".yml");
    if (!entryFile.exists()) {
      getLogger().warning("Category file " + entryFile + ".yml does not exist.");
      return null;
    }
    YamlConfiguration configuration = YamlConfiguration.loadConfiguration(entryFile);
    String displayName = configuration.getString("entry.display-name", "<red>Undefined</red>");

    ConfigurationSection levelsSection = configuration.getConfigurationSection("entry.levels");
    if (levelsSection == null) {
      getLogger().warning("No levels section found for entry " + entryId);
      return null;
    }
    Set<String> levels = levelsSection.getKeys(false);
    LevelController.Builder lcBuilder = new LevelController.Builder();
    for (String level : levels) {
      ConfigurationSection section = levelsSection.getConfigurationSection(level);
      int killsRequired = section.getInt("kills");
      if (killsRequired <= 0) {
        getLogger().warning("Invalid kills required for level %s in entry %s".formatted(level, entryId));
        continue;
      }
      LevelReward[] rewards;
      ConfigurationSection rewardsSection = section.getConfigurationSection("rewards");
      if (rewardsSection == null) {
        lcBuilder.addLevel(Integer.parseInt(level), killsRequired, new LevelReward[0]);
        continue;
      }
      List<String> rewardTypes = rewardsSection.getStringList("type");
      rewards = new LevelReward[rewardTypes.size()];
      for (int i = 0; i < rewardTypes.size(); i++) {
        String rewardType = rewardTypes.get(i);
        switch (rewardType.toLowerCase()) {
          case "exp" -> {
            int expAmount = rewardsSection.getInt("amount", 0);
            rewards[i] = new ExpReward(expAmount);
          }
          case "money" -> {
            double moneyAmount = rewardsSection.getDouble("amount", 0.0);
            rewards[i] = new MoneyReward(moneyAmount);
          }
          default -> {
            getLogger().warning("Unknown reward type '%s' found in entry %s".formatted(rewardType, entryId));
            rewards[i] = null;
          }
        }
      }
      lcBuilder.addLevel(Integer.parseInt(level), killsRequired, rewards);
    }
    LevelController levelController = lcBuilder.build();

    EntityFilter entityFilter = new EntityFilter();

    List<String> rawEntityTypes = configuration.getStringList("entry.entity-filter.entity-types");

    if (rawEntityTypes.isEmpty()) {
      String singularEntityType = configuration.getString("entry.entity-filter.entity-type");
      if (singularEntityType != null)
        rawEntityTypes.add(singularEntityType);
    }

    if (!rawEntityTypes.isEmpty()) {
      try {
        EntityType[] entityTypes = rawEntityTypes.stream()
          .map(String::toUpperCase)
          .map(EntityType::valueOf)
          .toArray(EntityType[]::new);
        entityFilter.setEntityTypes(entityTypes);
      } catch (IllegalArgumentException e) {
        getLogger().warning("Invalid entity type '%s' found in entry %s".formatted(String.join(", ", rawEntityTypes), entryId));
      }
    } else
      getLogger().warning("No entity types found in entry %s".formatted(entryId));

    BestiaryEntry entry = new BestiaryEntry(category, entryId, displayName, entityFilter, levelController);
    entryMap.put(entryId, entry);
    return entry;
  }

}
