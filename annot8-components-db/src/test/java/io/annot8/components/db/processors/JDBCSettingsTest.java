/* Annot8 (annot8.io) - Licensed under Apache-2.0. */
package io.annot8.components.db.processors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class JDBCSettingsTest {

  @Test
  public void test() {
    JDBCSettings settings = new JDBCSettings();

    settings.setJdbcUrl("jdbc");
    settings.setPassword("pass");
    settings.setUser("user");

    assertThat(settings.getJdbcUrl()).isEqualTo("jdbc");
    assertThat(settings.getPassword()).isEqualTo("pass");
    assertThat(settings.getUser()).isEqualTo("user");

    assertThat(settings.validate()).isTrue();
  }
}
