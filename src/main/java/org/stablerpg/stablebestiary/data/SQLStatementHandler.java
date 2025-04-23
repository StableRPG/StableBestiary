package org.stablerpg.stablebestiary.data;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public record SQLStatementHandler(@NotNull String createPlayerTableStatement,
                                  @NotNull String createEntryTableStatement,
                                  @NotNull String entryCountLookupStatement,
                                  @NotNull String loadPlayersStatement,
                                  @NotNull String loadEntriesStatement,
                                  @NotNull String savePlayerStatement,
                                  @NotNull String saveEntryStatement) {

  public void executeCreatePlayerTableStatement(@NotNull Statement statement) throws SQLException {
    statement.execute(createPlayerTableStatement);
  }

  public void executeCreateEntryTableStatement(@NotNull Statement statement) throws SQLException {
    statement.execute(createEntryTableStatement);
  }

  public int handleEntryCountLookup(@NotNull Connection connection) throws SQLException {
    try (ResultSet resultSet = connection.createStatement().executeQuery(entryCountLookupStatement)) {
      return resultSet.next() ? resultSet.getInt(1) : 1024;
    }
  }

  public ResultSet handleLoadPlayerStatement(@NotNull Connection connection) throws SQLException {
    return connection.prepareStatement(loadPlayersStatement).executeQuery();
  }

  public ResultSet handleLoadEntriesStatement(@NotNull Connection connection, byte[] uniqueId) throws SQLException {
    var statement = connection.prepareStatement(loadEntriesStatement);
    statement.setBytes(1, uniqueId);
    return statement.executeQuery();
  }

  public PreparedStatement getSavePlayerStatement(@NotNull Connection connection) throws SQLException {
    return connection.prepareStatement(savePlayerStatement);
  }

  public PreparedStatement getSaveEntryStatement(@NotNull Connection connection) throws SQLException {
    return connection.prepareStatement(saveEntryStatement);
  }

}
