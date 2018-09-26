package io.annot8.components.db.processors;

import io.annot8.common.data.content.FileContent;
import io.annot8.core.capabilities.ProcessesContent;
import io.annot8.core.components.Processor;
import io.annot8.core.components.responses.ProcessorResponse;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.Annot8Exception;
import io.annot8.core.exceptions.IncompleteException;
import io.annot8.core.exceptions.UnsupportedContentException;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ProcessesContent(FileContent.class)
public class ChromeHistoryFileExtractor extends AbstractJDBCSource<URL> implements Processor {

  private static final String HISTORY_QUERY = "SELECT * FROM urls";

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
        .reduce(true, (a,b) -> a && b);

    if(!withoutErrors){
      return ProcessorResponse.itemError();
    }

    return ProcessorResponse.ok();
  }

  private boolean extractHistory(FileContent fileContent, Item item){
    File file = fileContent.getData();
    if(!file.exists()){
      return false;
    }
    String jdbcUrl = "jdbc:sqlite:/" + file.getAbsolutePath();

    List<URL> urls = null;
    try {
      urls = executeQuery(jdbcUrl, HISTORY_QUERY);
    } catch (SQLException e) {
      log().error("Error processing SQL query to retrieve history", e);
      // Halt the process of this particular content but do not fail processing
      // A FileContent may not represent an SQLite DB and this is fine
      return true;
    }

    return urls.stream()
        .map(u -> createChildItem(item, u)).reduce(true, (a,b) -> a && b);
  }

  private boolean createChildItem(Item item, URL url){
    Item child = item.createChildItem();
    try {
      child.create(URLContent.class).withData(() -> url).withName("url").save();
    } catch (UnsupportedContentException e) {
      log().error("Error creating content of type URLContent", e);
      return false;
    } catch (IncompleteException e){
      log().error("Failed to save URLContent", e);
      return false;
    }
    return true;
  }

}
