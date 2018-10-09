/* Annot8 (annot8.io) - Licensed under Apache-2.0. */
package io.annot8.components.db.processors;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import io.annot8.components.base.components.AbstractComponent;

public abstract class AbstractJDBCComponent<T> extends AbstractComponent {

  private static final int TIMEOUT = 1000;

  protected Connection openConnection(JDBCSettings settings) throws SQLException {
    String user = settings.getUser();
    String jdbcUrl = settings.getJdbcUrl();
    String password = settings.getPassword();

    if (user == null || user.isEmpty()) {
      return DriverManager.getConnection(jdbcUrl);
    }

    return DriverManager.getConnection(jdbcUrl, user, password);
  }

  protected boolean testConnection(Connection connection) {
    try {
      if (connection == null) {
        return false;
      }

      return connection.isValid(TIMEOUT);
    } catch (Exception e) {
      log().warn("Connection is not valid", e);
      return false;
    }
  }

  protected void closeConnection(Connection connection) {
    try {
      if (connection != null) {
        connection.close();
      }
    } catch (Exception e) {
      // Silently ignore
    }
  }

  protected boolean runInConnection(JDBCSettings settings, Function<Connection, Boolean> consumer) {
    try (Connection connection = openConnection(settings)) {

      if (connection == null || !testConnection(connection)) {
        return false;
      }

      return consumer.apply(connection);

    } catch (Exception e) {
      log().warn("Run to run sql", e);
      return false;
    }
  }

  protected List<T> executeQuery(
      Connection connection, String query, Function<ResultSet, Optional<T>> mapper) {
    List<T> resultList = new ArrayList<>();
    try (Statement s = connection.createStatement()) {
      try (ResultSet results = s.executeQuery(query)) {
        while (results.next()) {
          Optional<T> value = mapper.apply(results);
          if (value.isPresent()) {
            resultList.add(value.get());
          }
        }
      }
    } catch (SQLException e) {
      log().error("Error processing SQL query", e);
    }
    return resultList;
  }
}
