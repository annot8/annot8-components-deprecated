package io.annot8.components.mongo.sources;

import com.mongodb.client.MongoCursor;
import io.annot8.components.base.components.AbstractComponent;
import io.annot8.components.mongo.data.MongoDocument;
import io.annot8.components.mongo.resources.Mongo;
import io.annot8.conventions.PropertyKeys;
import io.annot8.core.capabilities.CreatesContent;
import io.annot8.core.components.Source;
import io.annot8.core.components.responses.SourceResponse;
import io.annot8.core.context.Context;
import io.annot8.core.data.Item;
import io.annot8.core.data.ItemFactory;
import io.annot8.core.exceptions.BadConfigurationException;
import io.annot8.core.exceptions.IncompleteException;
import io.annot8.core.exceptions.MissingResourceException;
import io.annot8.core.exceptions.UnsupportedContentException;
import io.annot8.core.settings.SettingsClass;
import java.time.Instant;
import java.util.Optional;
import org.bson.Document;

/**
 * Reads the contents of a Mongo collection into items.
 *
 * Note that this source will only run the query once, and once it has exhausted those results
 * it will return SourceResponse.done().
 */
@SettingsClass(MongoSourceSettings.class)
@CreatesContent(MongoDocument.class)
public class MongoSource extends AbstractComponent implements Source {

  private MongoSourceSettings settings = null;
  private Mongo connection = null;

  private MongoCursor<Document> cursor = null;

  @Override
  public void configure(Context context) throws BadConfigurationException, MissingResourceException {
    super.configure(context);

    settings = context.getSettings(MongoSourceSettings.class);
    if(settings == null)
      throw new BadConfigurationException("No configuration provided");

    if(settings.hasConnectionResourceKey()){
      Optional<Mongo> optConnection = context.getResource(settings.getConnectionResourceKey(), Mongo.class);
      if(optConnection.isPresent()){
        connection = optConnection.get();
      }else{
        throw new MissingResourceException("Can't find Mongo resource with key "+settings.getConnectionResourceKey());
      }
    }else{
      Optional<Mongo> optConnection = context.getResource(Mongo.class);
      if(optConnection.isPresent()){
        connection = optConnection.get();
      }else{
        throw new MissingResourceException("Can't find Mongo resource");
      }
    }

    cursor = connection.getDatabase().getCollection(settings.getCollection()).find().iterator();
  }

  @Override
  public SourceResponse read(ItemFactory itemFactory) {
    if(!cursor.hasNext()) {
      cursor.close();
      return SourceResponse.done();
    }

    Document doc = cursor.next();

    Item item = itemFactory.create();

    try {
      //TODO: Add source here, but how do we get that?
      item.getProperties().set(PropertyKeys.PROPERTY_KEY_ACCESSEDAT, Instant.now().getEpochSecond());

      item.create(MongoDocument.class)
          .withName("document")
          .withData(doc)
          .save();
    } catch (UnsupportedContentException | IncompleteException e) {
      log().warn("Couldn't create item", e);
      item.discard();
    }

    return SourceResponse.ok();
  }
}
