/* Annot8 (annot8.io) - Licensed under Apache-2.0. */
package io.annot8.components.db.processors;

import io.annot8.core.data.BaseItemFactory;
import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import io.annot8.common.data.content.FileContent;
import io.annot8.common.data.content.URLContent;
import io.annot8.core.capabilities.CreatesContent;
import io.annot8.core.capabilities.ProcessesContent;
import io.annot8.core.components.Processor;
import io.annot8.core.components.responses.ProcessorResponse;
import io.annot8.core.context.Context;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.Annot8Exception;
import io.annot8.core.exceptions.BadConfigurationException;
import io.annot8.core.exceptions.MissingResourceException;

/** FileContent processor that will use */
@ProcessesContent(FileContent.class)
@CreatesContent(URLContent.class)
public class ChromeHistoryFileExtractor extends AbstractJDBCComponent<URL> implements Processor {

  private static final String HISTORY_QUERY = "SELECT * FROM urls";
  private BaseItemFactory itemFactory;


  @Override
  public ProcessorResponse process(Item item) throws Annot8Exception {
    boolean withoutErrors =
        item.getContents(FileContent.class)
            .map((c) -> openAsDatabase(c, item))
            .reduce(true, (a, b) -> a && b);

    if (!withoutErrors) {
      return ProcessorResponse.itemError();
    }

    return ProcessorResponse.ok();
  }

  public boolean openAsDatabase(FileContent fileContent, Item item) {
    File file = fileContent.getData();
    if (!file.exists()) {
      return false;
    }
    String jdbcUrl = "jdbc:sqlite:/" + file.getAbsolutePath();
    JDBCSettings settings = new JDBCSettings(jdbcUrl);

    return runInConnection(settings, connection -> extractHistory(connection, item));
  }

  private boolean extractHistory(Connection connection, Item item)  {

    // If it's not sqlite its still ok
    if(!isSqlite(connection)) {
      return true;
    }

    try {

      // Confirmed as SQLite db so check specific table exists
      DatabaseMetaData metadata = connection.getMetaData();
      ResultSet rs = metadata.getTables(null, null, "urls", null);
      boolean hasUrls = false;
      while (rs.next()) {
        if (rs.getString("TABLE_NAME").equals("urls")) {
          hasUrls = true;
          break;
        }
      }
      if (!hasUrls) {
        return false;
      }

      List<URL> urls = executeQuery(connection, HISTORY_QUERY, this::extractUrl);

      return urls.stream().map(u -> createChildItem(item, u)).reduce(true, (a, b) -> a && b);
    } catch (SQLException e) {
      log().warn("Sql error occured", e);
      return false;
    }
  }

  private boolean isSqlite(Connection connection)  {

    // The parent class uses basic JDBC connection checks
    // The following check ensures DB file is an sqlite db
    try (ResultSet set = connection.createStatement().executeQuery("pragma schema_version")) {
      int schemaVersion = set.getInt("schema_version");
      if (schemaVersion == 0) {
        log().info("File is not an SQLite file");
        return false;
      }
      return true;
    } catch (SQLException e) {
      log().info("Error validating SQLite file", e);
      return false;
    }
  }

  protected Optional<URL> extractUrl(ResultSet result) {
    try {
      String url = result.getString("url");
      return Optional.of(new URL(url));
    } catch (Exception e) {
      log().error("Failed to create URL from value");
      return Optional.empty();
    }
  }

  private boolean createChildItem(Item item, URL url) {
    Item child = item.create();

    try {
      child.create(URLContent.class).withData(() -> url).withName("url").save();
      return true;
    } catch (Annot8Exception e) {
      log().error("Error creating URL content", e);
      // If we failed to create it properly, discard it
      child.discard();
      return false;
    }
  }
}
