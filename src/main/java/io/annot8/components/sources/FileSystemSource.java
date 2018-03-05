package io.annot8.components.sources;

import io.annot8.common.content.FileContent;
import io.annot8.common.factories.ItemFactory;
import io.annot8.core.components.Capabilities;
import io.annot8.core.components.Source;
import io.annot8.core.components.responses.SourceResponse;
import io.annot8.core.context.Context;
import io.annot8.core.data.Item;
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

// TODO: This is not a good implementation.. it reads everything in one go
// which is going to kill the memory.

@SettingsClass(FileSystemSourceSettings.class)
public abstract class FileSystemSource implements Source {

  private Path rootFolder;
  private ItemFactory itemFactory;

  public FileSystemSource(ItemFactory itemFactory){
    this.itemFactory = itemFactory;
  }

  @Override
  public void configure(final Context context) {
    final FileSystemSourceSettings settings =
        context.getSettings(FileSystemSourceSettings.class);
    rootFolder = Paths.get(settings.getRootFolder());
  }

  protected Item createItem() {
    return itemFactory.create();
  }

  @Override
  public SourceResponse read() {
    try {
      return SourceResponse
          .ok(readFiles(rootFolder));
    } catch (final IOException ioe) {
      ioe.printStackTrace();
      return SourceResponse.sourceError();
    }
  }

  protected Stream<Item> readFiles(Path rootFolder) throws IOException {
    return Files.walk(rootFolder)
        // TODO: in future should just return everything and the pipeline could filter out directories?
        .filter(Files::isRegularFile)
        .map(f -> {
          try {
            return covert(f);
          } catch (final Annot8Exception e) {
            e.printStackTrace();
            return null;
          }
        })
        .filter(Objects::nonNull);
  }


  private Item covert(Path p) throws Annot8Exception {
    final Item item = createItem();
    item.getProperties().set("source", p);
    item.getProperties().set("accessedAt", Instant.now().getEpochSecond());

    item.create(FileContent.class)
        .withName("file")
        .withData(p.toFile())
        .save();

    return item;
  }

  @Override
  public Capabilities getCapabilities() {
    return new SimpleCapabilities.Builder()
        .createsContent(FileContent.class)
        .save();
  }
}
