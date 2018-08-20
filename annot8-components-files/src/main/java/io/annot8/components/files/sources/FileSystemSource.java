package io.annot8.components.files.sources;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import io.annot8.common.data.content.FileContent;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.annot8.components.base.components.AbstractComponent;
import io.annot8.core.capabilities.CreatesContent;
import io.annot8.core.components.Source;
import io.annot8.core.components.responses.SourceResponse;
import io.annot8.core.context.Context;
import io.annot8.core.data.Item;
import io.annot8.core.data.ItemFactory;
import io.annot8.core.exceptions.Annot8Exception;
import io.annot8.core.exceptions.Annot8RuntimeException;
import io.annot8.core.exceptions.BadConfigurationException;

@CreatesContent(FileContent.class)
public class FileSystemSource extends AbstractComponent implements Source {

  private final WatchService watchService;

  private Set<WatchKey> watchKeys = new HashSet<>();

  private Set<Pattern> acceptedFilePatterns = Collections.emptySet();

  private Set<Path> initialFiles = new HashSet<>();

  public FileSystemSource(){
    try {
      watchService = FileSystems.getDefault().newWatchService();
    } catch (IOException e) {
      throw new Annot8RuntimeException("Unable to initialize WatchService", e);
    }
  }

  @Override
  public void configure(final Context context) throws BadConfigurationException {
    final FileSystemSourceSettings settings = context.getSettings(FileSystemSourceSettings.class);
    acceptedFilePatterns = settings.getAcceptedFileNamePatterns();

    //Unregister existing watchers
    watchKeys.forEach(WatchKey::cancel);
    watchKeys.clear();

    initialFiles.clear();

    try {
      Path p = settings.getRootFolder();

      if (settings.isRecursive()) {
        Files.walkFileTree(
            p,
            new SimpleFileVisitor<>() {
              @Override
              public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attr)
                  throws IOException {
                registerDirectory(settings, dir);
                return FileVisitResult.CONTINUE;
              }
            });
      } else {
        registerDirectory(settings, p);
      }

      addFilesFromDir(settings, p.toFile());
    } catch (IOException ioe) {
      throw new BadConfigurationException("Unable to register folder or sub-folder with watch service", ioe);
    }
  }

  private void registerDirectory(FileSystemSourceSettings settings, Path path) throws IOException {
    WatchKey key;
    if (settings.isReprocessOnModify()) {
      key = path.register(watchService, ENTRY_CREATE, ENTRY_MODIFY);
    } else {
      key = path.register(watchService, ENTRY_CREATE);
    }

    watchKeys.add(key);
  }

  private void addFilesFromDir(FileSystemSourceSettings settings, File dir) {
    File[] files = dir.listFiles();

    if (files == null) {
      return;
    }

    for (File file : files) {
      if (!file.isDirectory()) {
        Path path = file.toPath();

        if (acceptedFilePatterns.isEmpty()) {
          initialFiles.add(path);
        } else {
          for (Pattern p : acceptedFilePatterns) {
            Matcher m = p.matcher(path.getFileName().toString());
            if (m.matches()) {
              initialFiles.add(path);
              break;
            }
          }
        }
      } else if (settings.isRecursive()) {
        addFilesFromDir(settings, file);
      }
    }
  }

  private boolean createItem(ItemFactory itemFactory, Path path) {
    boolean include = false;

    if (acceptedFilePatterns.isEmpty()) {
      include = true;
    } else {
      for (Pattern p : acceptedFilePatterns) {
        Matcher m = p.matcher(path.getFileName().toString());
        if (m.matches()) {
          include = true;
          break;
        }
      }
    }

    if(include){
      final Item item = itemFactory.create();
      try {
        item.getProperties().set("source", path);
        item.getProperties().set("accessedAt", Instant.now().getEpochSecond());

        item.create(FileContent.class)
            .withName("file")
            .withData(path.toFile())
            .save();

        return true;
      } catch(Annot8Exception e) {
        item.discard();
      }
    }

    return false;
  }

  @Override
  public SourceResponse read(ItemFactory itemFactory) {
    if(!initialFiles.isEmpty()){
      initialFiles.forEach(path -> createItem(itemFactory, path));
      initialFiles.clear();
    }

    boolean read = false;
    WatchKey key;
    while ((key = watchService.poll()) != null) {
      for (WatchEvent<?> event : key.pollEvents()) {
        if(createItem(itemFactory, ((WatchEvent<Path>)event).context()))
          read = true;
      }

      key.reset();
    }

    if(read)
      return SourceResponse.ok();

    return SourceResponse.empty();
  }

}
