package io.annot8.components.sources;

import io.annot8.common.content.Text;
import io.annot8.core.components.Capabilities;
import io.annot8.core.components.Source;
import io.annot8.core.components.responses.SourceResponse;
import io.annot8.core.context.Context;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.Annot8Exception;
import io.annot8.core.settings.Settings;
import io.annot8.core.settings.SettingsClass;
import io.annot8.defaultimpl.data.SimpleCapabilities;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

// TODO: This is not a good implementation.. it reads everything in one go
// which is going to kill the memory.

// At any rate I suspect the right way to implement in annot8 is:
// this is called FilesystemReader
// it outputs a Item with a FileContent type (which is just a path)
// another processor(s) will look at that filecontent process it to something else (eg text)
// At some point we we discard that filecontent (with a processor)
//
@SettingsClass(DirectorySourceSettings.class)
public abstract class DirectorySource implements Source {

  private Path rootFolder;
  private Context context;

  @Override
  public void configure(final Context context) {
    final DirectorySourceSettings settings =
        context.getSettings(DirectorySourceSettings.class);
    rootFolder = Paths.get(settings.getRootFolder());
    this.context = context;
  }

  protected Item createItem() {
    return context.createItem();
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
        .filter(Files::isRegularFile)
        .filter(this::accept)
        .map(f -> {
          try {
            return createDataItem(f);
          } catch (final Annot8Exception e) {
            e.printStackTrace();
            return null;
          }
        })
        .filter(Objects::nonNull);
  }

  public abstract boolean accept(Path p);

  public abstract Item createDataItem(Path p)
      throws Annot8Exception;

  @Override
  public Capabilities getCapabilities(Settings settings) {
    return new SimpleCapabilities.Builder()
        .createsContent(Text.class)
        .save();
  }
}
