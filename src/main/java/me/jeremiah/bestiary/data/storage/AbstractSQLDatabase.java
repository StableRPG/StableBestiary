package me.jeremiah.bestiary.data.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.jeremiah.bestiary.BestiaryPlatform;
import me.jeremiah.bestiary.data.BestiaryPlayer;
import me.jeremiah.bestiary.data.SQLStatementHandler;
import me.jeremiah.bestiary.data.configuration.DatabaseInfo;
import me.jeremiah.bestiary.util.ByteArrayWrapper;
import me.jeremiah.bestiary.util.DataUtils;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public abstract class AbstractSQLDatabase extends AbstractDatabase {

  private final SQLStatementHandler statementHandler;

  private final HikariDataSource dataSource;

  protected AbstractSQLDatabase(@NotNull Class<? extends Driver> driver, SQLStatementHandler statementHandler, @NotNull BestiaryPlatform platform) {
    super(platform);
    if (DriverManager.drivers().noneMatch(driver::isInstance))
      try {
        DriverManager.registerDriver(driver.getDeclaredConstructor().newInstance());
      } catch (Exception exception) {
        throw new RuntimeException("Failed to register SQL driver: " + driver.getName(), exception);
      }
    this.statementHandler = statementHandler;

    HikariConfig hikariConfig = new HikariConfig();

    processConfig(hikariConfig, platform.getDatabaseInfo());

    dataSource = new HikariDataSource(hikariConfig);

    setup();
  }

  protected abstract void processConfig(@NotNull HikariConfig hikariConfig, @NotNull DatabaseInfo databaseInfo);

  @Override
  protected int lookupEntryCount() {
    try (Connection connection = dataSource.getConnection()) {
      return statementHandler.handleEntryCountLookup(connection);
    } catch (SQLException exception) {
      throw new RuntimeException("Failed to lookup entry count from SQL database.", exception);
    }
  }

  @Override
  protected void setup() {
    try (Connection connection = dataSource.getConnection();
         Statement statement = connection.createStatement()) {
      statementHandler.executeCreatePlayerTableStatement(statement);
      statementHandler.executeCreateEntryTableStatement(statement);
    } catch (SQLException exception) {
      throw new RuntimeException("Failed to setup SQL database.", exception);
    }
    super.setup();
  }

  @Override
  protected void load() {
    try (Connection connection = dataSource.getConnection();
         Statement statement = connection.createStatement()) {
      HashMap<ByteArrayWrapper, HashMap<String, Integer>> entries = new HashMap<>();

      try (ResultSet rawEntries = statement.executeQuery("SELECT * FROM entries;")) {
        while (rawEntries.next()) {
          ByteArrayWrapper rawUniqueId = new ByteArrayWrapper(rawEntries.getBytes("uniqueId"));

          if (!entries.containsKey(rawUniqueId))
            entries.put(rawUniqueId, new HashMap<>());

          String entry = rawEntries.getString("entry");
          int kills = rawEntries.getInt("kills");

          entries.get(rawUniqueId).compute(entry, (key, value) -> value == null ? kills : value + kills);
        }
      }

      try (ResultSet rawPlayerEntries = statement.executeQuery("SELECT * FROM players;")) {
        while (rawPlayerEntries.next()) {
          ByteArrayWrapper rawUniqueId = new ByteArrayWrapper(rawPlayerEntries.getBytes("uniqueId"));

          add(new BestiaryPlayer(
            rawUniqueId.toUUID(),
            rawPlayerEntries.getString("username"),
            entries.getOrDefault(rawUniqueId, new HashMap<>())
          ));
        }
      }

    } catch (SQLException exception) {
      getPlatform().getLogger().log(Level.SEVERE, "Failed to load data from SQL database.", exception);
    }
  }

  @Override
  protected void save() {
    try (Connection connection = dataSource.getConnection()) {
      try (PreparedStatement playerStatement = statementHandler.getSavePlayerStatement(connection);
           PreparedStatement entryStatement = statementHandler.getSaveEntryStatement(connection)) {

        for (BestiaryPlayer bPlayer : entries) {

          try {
            byte[] uniqueId = DataUtils.uuidToBytes(bPlayer.getUniqueId());

            playerStatement.setBytes(1, uniqueId);
            playerStatement.setString(2, bPlayer.getUsername());
            playerStatement.addBatch();

            for (Map.Entry<String, Integer> entry : bPlayer.getEntryChanges().entrySet()) {
              entryStatement.setBytes(1, uniqueId);
              entryStatement.setString(2, entry.getKey());
              entryStatement.setDouble(3, entry.getValue());
              entryStatement.addBatch();
            }

            bPlayer.saveUpdates();
          } catch (SQLException exception) {
            getPlatform().getLogger().log(Level.SEVERE, "Failed to save data for %s (%s)".formatted(bPlayer.getUsername(), bPlayer.getUniqueId()), exception);
          }
        }

        playerStatement.executeBatch();
        entryStatement.executeBatch();

        connection.commit();
      } catch (SQLException exception) {
        connection.rollback();
        throw exception;
      }
    } catch (SQLException exception) {
      getPlatform().getLogger().log(Level.SEVERE, "Failed to save data to SQL database.", exception);
    }
  }

  @Override
  public void close() {
    super.close();
    dataSource.close();
  }

}
