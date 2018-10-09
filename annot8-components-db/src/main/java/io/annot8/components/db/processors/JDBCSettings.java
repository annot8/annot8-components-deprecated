/* Annot8 (annot8.io) - Licensed under Apache-2.0. */
package io.annot8.components.db.processors;

import com.google.common.base.Strings;
import io.annot8.core.settings.Settings;

public class JDBCSettings implements Settings {

  private String jdbcUrl;
  private String user;
  private String password;

  public JDBCSettings() {
    this(null);
  }

  public JDBCSettings(String jdbcUrl) {
    this(jdbcUrl, null, null);
  }

  public JDBCSettings(String jdbcUrl, String user, String password) {
    this.jdbcUrl = jdbcUrl;
    this.user = user;
    this.password = password;
  }

  public void setJdbcUrl(String jdbcUrl) {
    this.jdbcUrl = jdbcUrl;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getJdbcUrl() {
    return jdbcUrl;
  }

  public String getPassword() {
    return password;
  }

  public String getUser() {
    return user;
  }

  @Override
  public boolean validate() {
    return !Strings.isNullOrEmpty(jdbcUrl);
  }
}
