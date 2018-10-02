///* Annot8 (annot8.io) - Licensed under Apache-2.0. */
//package io.annot8.components.db.processors;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.junit.jupiter.api.Assertions.fail;
//
//import java.net.URL;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//
//import org.hsqldb.server.Server;
//import org.junit.jupiter.api.Test;
//
//public class JDBCSettingsTest {
//
//  private static final String JDBC_TEST_URL = "jdbc:hsqldb:hsql://localhost:9001/test";
//  private static final String USER = "TEST";
//  private static final String PASS = "TEST";
//
//  @Test
//  public void testValidate() {
//    URL db = JDBCSettings.class.getClassLoader().getResource("ChromeHistory");
//    JDBCSettings settings = new JDBCSettings("jdbc:sqlite:/" + db.getPath(), "");
//    assertTrue(settings.validate());
//  }
//
//  @Test
//  public void testNonValidSettings() {
//    JDBCSettings settings = new JDBCSettings("badURL", "");
//    assertFalse(settings.validate());
//  }
//
//  @Test
//  public void testGetConnection() {
//    URL db = JDBCSettings.class.getClassLoader().getResource("ChromeHistory");
//    JDBCSettings settings = new JDBCSettings("jdbc:sqlite:/" + db.getPath(), "");
//
//    try (Connection connection = settings.getConnection()) {
//      assertNotNull(connection);
//      assertTrue(connection.isValid(1000));
//    } catch (SQLException e) {
//      fail("Test should not error here");
//    }
//  }
//
//  @Test
//  public void testGetConnectionBadURL() {
//    JDBCSettings settings = new JDBCSettings("badURL", "");
//    assertThrows(SQLException.class, () -> settings.getConnection());
//  }
//
//  @Test
//  public void testGetQuery() {
//    String query = "SELECT * FROM url";
//    URL db = JDBCSettings.class.getClassLoader().getResource("ChromeHistory");
//    JDBCSettings settings = new JDBCSettings("jdbc:sqlite:/" + db.getPath(), query);
//    assertEquals(query, settings.getQuery());
//  }
//
//  @Test
//  public void testGetConnectionWithCredentials() {
//    Server server = createServer();
//    server.start();
//
//    JDBCSettings settings = new JDBCSettings(JDBC_TEST_URL, "", USER, PASS);
//    try (Connection connection = settings.getConnection()) {
//      assertNotNull(connection);
//      assertTrue(connection.isValid(1000));
//    } catch (SQLException e) {
//      fail("Test should not fail here", e);
//    }
//
//    shutdownServer(server);
//  }
//
//  @Test
//  public void testValidateWithCredentials() {
//    Server server = createServer();
//
//    JDBCSettings settings = new JDBCSettings(JDBC_TEST_URL, "", USER, PASS);
//
//    assertTrue(settings.validate());
//    shutdownServer(server);
//  }
//
//  @Test
//  public void testValidateInvalidCredentials() {
//    Server server = createServer();
//    server.start();
//
//    JDBCSettings badCreds = new JDBCSettings(JDBC_TEST_URL, "", "bad", "bad");
//    assertFalse(badCreds.validate());
//
//    shutdownServer(server);
//  }
//
//  private Server createServer() {
//    Server server = new Server();
//    server.setDatabaseName(0, "test");
//    server.setDatabasePath(0, "mem:test");
//    server.start();
//    try (Connection conn = DriverManager.getConnection(JDBC_TEST_URL)) {
//      conn.createStatement().executeQuery("CREATE USER " + USER + " PASSWORD " + PASS);
//    } catch (SQLException e) {
//      fail("Test should not fail here", e);
//    }
//    return server;
//  }
//
//  private void shutdownServer(Server server) {
//    try (Connection conn = DriverManager.getConnection(JDBC_TEST_URL)) {
//      conn.createStatement().executeQuery("DROP USER " + USER);
//    } catch (SQLException e) {
//      fail("Test should not fail here", e);
//    }
//    server.shutdown();
//  }
//}
