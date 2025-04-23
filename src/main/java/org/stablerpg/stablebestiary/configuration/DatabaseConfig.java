package org.stablerpg.stablebestiary.configuration;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.stablerpg.stablebestiary.data.configuration.DatabaseInfo;

@Getter
public final class DatabaseConfig extends AbstractConfig {

  private DatabaseInfo databaseInfo;

  public DatabaseConfig(@NotNull JavaPlugin plugin) {
    super(plugin,"database.yml");
  }

  @Override
  public void load() {
    super.load();

    databaseInfo = new DatabaseInfo(
      getConfig().getString("database.type", "h2"),
      getConfig().getString("database.address", "localhost"),
      getConfig().getInt("database.port", 3306),
      getConfig().getString("database.name", "bestiary"),
      getConfig().getString("database.username", "root"),
      getConfig().getString("database.password", "root")
    );

    databaseInfo.setAutoSaveInterval(getConfig().getLong("database.auto-save-interval", 5));
  }

}
