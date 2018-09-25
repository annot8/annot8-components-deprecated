package io.annot8.components.db.processors;

import io.annot8.components.base.components.AbstractComponent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractJDBCSource<T> extends AbstractComponent {

  protected List<T> executeQuery(String jdbcUrl, String query) throws SQLException {
    Connection connection = DriverManager.getConnection(jdbcUrl);
    Statement statement = connection.createStatement();
    ResultSet results = statement.executeQuery(query);
    List<T> resultList = new ArrayList<>();
    while(results.next()){
      Optional<T> value = processResult(results);
      if(value.isPresent()){
        resultList.add(value.get());
      }
    }
    connection.close();
    return resultList;
  }

  protected abstract Optional<T> processResult(ResultSet result) throws SQLException;


}
