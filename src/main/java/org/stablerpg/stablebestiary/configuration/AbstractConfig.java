package org.stablerpg.stablebestiary.configuration;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractConfig {

  private final @NotNull JavaPlugin plugin;
  private final @NotNull File file;
  private final @NotNull YamlConfiguration config = new YamlConfiguration();

  public AbstractConfig(@NotNull JavaPlugin plugin, @NotNull String fileName) {
    this.plugin = plugin;
    this.file = new File(plugin.getDataFolder(), fileName);
  }

  public @NotNull JavaPlugin getPlugin() {
    return plugin;
  }

  public @NotNull YamlConfiguration getConfig() {
    return config;
  }

  public void load() {
    createFile();
    loadFile();
  }

  private void createFile() {
    if (!file.exists())
      plugin.saveResource(file.getName(), false);
  }

  private void loadFile() {
    try {
      config.load(file);
    } catch (InvalidConfigurationException exception) {
      getLogger().log(Level.SEVERE, "Failed to load %s due to an invalid configuration".formatted(file.getName()), exception);
    } catch (IOException exception) {
      getLogger().log(Level.SEVERE, "Failed to load " + file.getName(), exception);
    }
  }

  public @NotNull Logger getLogger() {
    return getPlugin().getLogger();
  }

}