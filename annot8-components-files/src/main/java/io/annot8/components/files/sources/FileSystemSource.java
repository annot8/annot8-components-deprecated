package io.annot8.components.files.sources;

import io.annot8.core.components.responses.SourceResponse;
import io.annot8.core.data.ItemFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileSystemSource extends AbstractFileSystemSource {

    private boolean called;

    @Override
    public SourceResponse read(ItemFactory itemFactory) {
        try{
            Path rootFolder = getSettings().getRootFolder();
            Files.walk(rootFolder.toAbsolutePath())
                    .filter(Files::isRegularFile)
                    .forEach(f -> createItem(itemFactory, f));
        } catch (IOException e) {
            return SourceResponse.sourceError();
        }

        return SourceResponse.done();
    }
}
