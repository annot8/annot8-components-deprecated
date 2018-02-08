package io.annot8.components.sources;

import io.annot8.common.content.Text;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.Annot8Exception;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

// TODO See note in
public class TxtDirectorySource extends DirectorySource {

  @Override
  public boolean accept(final Path p) {
    return p.toString().toLowerCase().endsWith(".txt");
  }

  @Override
  public Item createDataItem(final Path p) throws Annot8Exception {

    final Item item = createItem();
    item.getProperties().set("source", p);
    item.getProperties().set("accessedAt", Instant.now().getEpochSecond());

    try {
      final String data = new String(Files.readAllBytes(p));
      item.create(Text.class)
          .withName("raw")
          .withData(data)
          .withProperty("language", "unknown")
          .save();

      return null;
    } catch (final IOException e) {
      // TODO: Log error
      return null;
    }

  }


}
