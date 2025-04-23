package org.stablerpg.stablebestiary.data.storage;

import com.zaxxer.hikari.HikariConfig;
import org.jetbrains.annotations.NotNull;
import org.stablerpg.stablebestiary.BestiaryPlatform;
import org.stablerpg.stablebestiary.data.SQLStatementHandler;
import org.stablerpg.stablebestiary.data.configuration.DatabaseInfo;

public class H2 extends AbstractSQLDatabase {

  private static final SQLStatementHandler HANDLER = new SQLStatementHandler(
    "CREATE TABLE IF NOT EXISTS players(uniqueId BINARY(16) PRIMARY KEY, username VARCHAR(16));",
    "CREATE TABLE IF NOT EXISTS entries(uniqueId BINARY(16), entry VARCHAR(255), kills INT, FOREIGN KEY(uniqueId) REFERENCES players(uniqueId), UNIQUE(uniqueId, entry));",
    "SELECT COUNT(*) FROM players;",
    "SELECT * FROM players;",
    "SELECT * FROM entries WHERE uniqueId = ?;",
    "INSERT INTO players(uniqueId, username) VALUES(?, ?) ON DUPLICATE KEY UPDATE username = VALUES(username);",
    "INSERT INTO entries(uniqueId, entry, kills) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE kills = entries.kills + VALUES(kills);"
  );

  public H2(@NotNull BestiaryPlatform platform) {
    super(org.h2.Driver.class, HANDLER, platform);
  }

  @Override
  protected void processConfig(@NotNull HikariConfig hikariConfig, @NotNull DatabaseInfo databaseInfo) {
    hikariConfig.setJdbcUrl("jdbc:h2:./plugins/Bestiary/bestiary;MODE=MariaDB;DATABASE_TO_UPPER=FALSE");
  }

}
