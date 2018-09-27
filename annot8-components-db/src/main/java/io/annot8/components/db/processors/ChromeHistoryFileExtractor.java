package io.annot8.components.db.processors;

import io.annot8.common.data.content.FileContent;
import io.annot8.common.implementations.context.SimpleContext;
import io.annot8.core.capabilities.CreatesContent;
import io.annot8.core.capabilities.ProcessesContent;
import io.annot8.core.components.Processor;
import io.annot8.core.components.responses.ProcessorResponse;
import io.annot8.core.context.Context;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.Annot8Exception;
import io.annot8.core.exceptions.BadConfigurationException;
import io.annot8.core.exceptions.IncompleteException;
import io.annot8.core.exceptions.MissingResourceException;
import io.annot8.core.exceptions.UnsupportedContentException;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * FileContent processor that will use
 */
@ProcessesContent(FileContent.class)
@CreatesContent(URLContent.class)
public class ChromeHistoryFileExtractor extends AbstractJDBCComponent<URL> implements Processor {

  private static final String HISTORY_QUERY = "SELECT * FROM urls";

  @Override
  public void configure(Context context)
      throws BadConfigurationException, MissingResourceException {
    super.configure(context);
    Optional<JDBCSettings> optional = context.getSettings(JDBCSettings.class);
    if(!optional.isPresent()){
      throw new BadConfigurationException("Settings are not present");
    }

    JDBCSettings settings = optional.get();

    // The parent class uses basic JDBC connection checks
    // The following check ensures DB file is an sqlite db
    try (Connection connection = settings.getConnection()) {
      ResultSet set = connection.createStatement().executeQuery("pragma schema_version");
      int schemaVersion = set.getInt("schema_version");
      if(schemaVersion == 0){
        throw new MissingResourceException("File is not an SQLite file");
      }
    } catch (SQLException e) {
      throw new BadConfigurationException("Error validating SQLite file");
    }
  }

  @Override
  protected Optional<URL> processResult(ResultSet result) throws SQLException {
    try {
      String url = result.getString("url");
      return Optional.of(new URL(url));
    } catch (MalformedURLException e) {
      log().error("Failed to create URL from value");
    }
    return Optional.empty();
  }

  @Override
  public ProcessorResponse process(Item item) throws Annot8Exception {
    item.getContents(FileContent.class).collect(Collectors.toList());
    boolean withoutErrors = item.getContents(FileContent.class)
        .map((c) -> extractHistory(c, item))
        .reduce(true, (a, b) -> a && b);

    if (!withoutErrors) {
      return ProcessorResponse.itemError();
    }

    return ProcessorResponse.ok();
  }

  private boolean extractHistory(FileContent fileContent, Item item) {
    File file = fileContent.getData();
    if (!file.exists()) {
      return false;
    }
    String jdbcUrl = "jdbc:sqlite:/" + file.getAbsolutePath();
    JDBCSettings settings = new JDBCSettings(jdbcUrl, HISTORY_QUERY);
    Context context = new SimpleContext(Collections.singleton(settings));
    try {
      this.configure(context);
    }catch(MissingResourceException e){
      // Indicates the file is not an SQLite file, this is not an issue with
      // the item and so process should stop here.
      return true;
    }catch (BadConfigurationException e) {
      // Indicates the processor has not been configured correctly and so
      // the process should error.
      return false;
    }

    List<URL> urls = null;
    try {
      urls = executeQuery();
    } catch (Annot8Exception e) {
      return false;
    }

    return urls.stream()
        .map(u -> createChildItem(item, u)).reduce(true, (a, b) -> a && b);
  }

  private boolean createChildItem(Item item, URL url) {
    Item child = item.createChildItem();
    try {
      child.create(URLContent.class).withData(() -> url).withName("url").save();
    } catch (UnsupportedContentException e) {
      log().error("Error creating content of type URLContent", e);
      return false;
    } catch (IncompleteException e) {
      log().error("Failed to save URLContent", e);
      return false;
    }
    return true;
  }

}
