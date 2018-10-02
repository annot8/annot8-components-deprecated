/* Annot8 (annot8.io) - Licensed under Apache-2.0. */
package io.annot8.components.db.processors;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.annot8.core.settings.Settings;

public class JDBCSettings implements Settings {

  public static final int TIMEOUT = 1000;

  private final String jdbcUrl;
  private final String user;
  private final String password;
  private final String query;

  public JDBCSettings(String jdbcUrl, String query) {
    this(jdbcUrl, query, null, null);
  }

  public JDBCSettings(String jdbcUrl, String query, String user, String password) {
    this.jdbcUrl = jdbcUrl;
    this.query = query;
    this.user = user;
    this.password = password;
  }

  @Override
  public boolean validate() {
    if (jdbcUrl == null || jdbcUrl.isEmpty()) {
      return false;
    }

    //  Testing the JDBC connection is valid
    if (user == null || user.isEmpty()) {
      try (Connection conn = DriverManager.getConnection(jdbcUrl)) {
        return conn.isValid(1000);
      } catch (SQLException e) {
        return false;
      }
    } else {
      try (Connection conn = DriverManager.getConnection(jdbcUrl, user, password)) {
        return conn.isValid(1000);
      } catch (SQLException e) {
        return false;
      }
    }
  }

  public Connection getConnection() throws SQLException {
    if (user == null || user.isEmpty()) {
      return DriverManager.getConnection(jdbcUrl);
    }
    return DriverManager.getConnection(jdbcUrl, user, password);
  }

  public String getQuery() {
    return query;
  }
}
