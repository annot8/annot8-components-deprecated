package io.annot8.components.sources;

import io.annot8.common.content.FileContent;
import io.annot8.core.components.Capabilities;
import io.annot8.core.components.Source;
import io.annot8.core.components.responses.SourceResponse;
import io.annot8.core.context.Context;
import io.annot8.core.data.Item;
import io.annot8.core.data.ItemFactory;
import io.annot8.core.exceptions.Annot8Exception;
import io.annot8.core.settings.SettingsClass;
import io.annot8.defaultimpl.data.SimpleCapabilities;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Objects;
import java.util.stream.Stream;

// TODO: This is not a good implementation.. lacks monitoring etc

@SettingsClass(FileSystemSourceSettings.class)
public class FileSystemSource implements Source {

  private Path rootFolder;

  private boolean called = false;

  @Override
  public void configure(final Context context) {
    final FileSystemSourceSettings settings =
        context.getSettings(FileSystemSourceSettings.class);
    rootFolder = Paths.get(settings.getRootFolder());
  }

  @Override
  public SourceResponse read(ItemFactory itemFactory) {
    if(called) {
      return SourceResponse.done();
    }

    try {
      readFiles(itemFactory, rootFolder);
      return SourceResponse
          .ok();
    } catch (final IOException ioe) {
      ioe.printStackTrace();
      return SourceResponse.sourceError();
    } finally {
      called = true;
    }
  }

  protected void readFiles(ItemFactory itemFactory, Path rootFolder) throws IOException {

    try (Stream<Path> paths = Files.walk(rootFolder.toAbsolutePath())) {
      // TODO: in future should just return everything and the pipeline could filter out directories?
      paths.filter(Files::isRegularFile)
          .forEach(f -> convert(itemFactory, f));
    }
  }


  private Item convert(ItemFactory itemFactory, Path p)  {
    final Item item = itemFactory.create();
    try {
      item.getProperties().set("source", p);
      item.getProperties().set("accessedAt", Instant.now().getEpochSecond());

      item.create(FileContent.class)
          .withName("file")
          .withData(p.toFile())
          .save();
    } catch(Annot8Exception e) {
      item.discard();
    }
    return item;
  }

  @Override
  public Capabilities getCapabilities() {
    return new SimpleCapabilities.Builder()
        .createsContent(FileContent.class)
        .save();
  }
}
