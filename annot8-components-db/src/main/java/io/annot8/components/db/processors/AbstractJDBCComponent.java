/* Annot8 (annot8.io) - Licensed under Apache-2.0. */
package io.annot8.components.db.processors;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.annot8.components.base.components.AbstractComponent;
import io.annot8.core.context.Context;
import io.annot8.core.exceptions.Annot8Exception;
import io.annot8.core.exceptions.BadConfigurationException;
import io.annot8.core.exceptions.MissingResourceException;
import io.annot8.core.settings.SettingsClass;

@SettingsClass(JDBCSettings.class)
public abstract class AbstractJDBCComponent<T> extends AbstractComponent {

  private JDBCSettings settings;

  @Override
  public void configure(Context context)
      throws BadConfigurationException, MissingResourceException {
    super.configure(context);

    Optional<JDBCSettings> optional = context.getSettings(JDBCSettings.class);
    if (!optional.isPresent()) {
      throw new BadConfigurationException("No settings are provided for this component");
    }

    JDBCSettings jdbcSettings = optional.get();
    if (!jdbcSettings.validate()) {
      throw new BadConfigurationException("Failed to validate JDBC settings");
    }

    this.settings = jdbcSettings;
  }

  protected List<T> executeQuery() throws Annot8Exception {
    List<T> resultList = new ArrayList<>();
    try (Connection connection = settings.getConnection()) {
      Statement statement = connection.createStatement();
      ResultSet results = statement.executeQuery(settings.getQuery());
      while (results.next()) {
        Optional<T> value = processResult(results);
        if (value.isPresent()) {
          resultList.add(value.get());
        }
      }
    } catch (SQLException e) {
      log().error("Error processing SQL query", e);
      throw new Annot8Exception("Failed to process SQL query", e);
    }
    return resultList;
  }

  protected abstract Optional<T> processResult(ResultSet result) throws SQLException;
}
