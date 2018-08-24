package io.annot8.components.mongo.components;

import com.google.common.base.Strings;
import io.annot8.components.base.components.AbstractComponent;
import io.annot8.components.mongo.resources.Mongo;
import io.annot8.components.mongo.resources.MongoConnection;
import io.annot8.components.mongo.resources.MongoConnectionSettings;
import io.annot8.components.mongo.resources.MongoFactory;
import io.annot8.core.capabilities.UsesResource;
import io.annot8.core.context.Context;
import io.annot8.core.exceptions.BadConfigurationException;
import io.annot8.core.exceptions.MissingResourceException;
import io.annot8.core.settings.SettingsClass;
import java.util.Optional;

/**
 * Base class for Mongo components which simplfies configuration
 *
 * THis can be configured to us a named Mongo resource (see {@link MongoResourceSettings}) or
 * using a non-named Mongo resource, or using settings (see {@link MongoConnectionSettings}).
 *
 * Implement configure(context, connection) to set up using the best connection.
 */
@SettingsClass(value=MongoResourceSettings.class, optional = true)
@SettingsClass(value=MongoConnectionSettings.class, optional = true)
@UsesResource(value = Mongo.class, optional = true)
@UsesResource(value = MongoFactory.class, optional = true)
public abstract class AbstractMongoComponent extends AbstractComponent {

  private MongoConnection connection = null;

  @Override
  public void configure(Context context) throws BadConfigurationException, MissingResourceException {
    super.configure(context);

    Optional<MongoResourceSettings> optionalMongoResource = context.getSettings(MongoResourceSettings.class);
    Optional<MongoConnectionSettings> optionalMongoConnection = context.getSettings(MongoConnectionSettings.class);

    if(optionalMongoResource.isPresent() && !Strings.isNullOrEmpty(optionalMongoResource.get().getMongo())) {
      // Use the resource first

      String key = optionalMongoResource.get().getMongo();
      Optional<Mongo> optionalConnection = context.getResource(key, Mongo.class);

      if(!optionalConnection.isPresent()) {
        throw new MissingResourceException(String.format("Named Mongo {} is missing", key));
      }

      connection = optionalConnection.get();

    } else if(optionalMongoConnection.isPresent()) {
      // Create a new connection

      Optional<MongoFactory> mongoFactory = context.getResource(MongoFactory.class);

      if(!mongoFactory.isPresent()) {
        throw new MissingResourceException("Missing MongoFactory needed for Mongo connection");
      }

      MongoConnectionSettings mergedSettings = mongoFactory.get()
          .mergeWithDefaultSettings(optionalMongoConnection);
      Optional<MongoConnection> optionalConnection = mongoFactory.get()
          .buildMongo(Optional.of(mergedSettings));


      if(!optionalConnection.isPresent()) {
        throw new BadConfigurationException("Unable to create Mongo connection from settings");
      }

      connection = optionalConnection.get();
    } else {
      // Fall back to default Mongo
      Optional<Mongo> optionalConnection = context.getResource(Mongo.class);

      if(!optionalConnection.isPresent()) {
        throw new MissingResourceException("No Mongo resource available");
      }

      connection = optionalConnection.get();
    }


    if(connection != null) {
      configure(context, connection);
    } else {
      log().error("Unable to create connection to Mongo");
      throw new BadConfigurationException("No Mongo connection");
    }
  }

  protected abstract void configure(Context context, MongoConnection connection) throws BadConfigurationException, MissingResourceException;

  @Override
  public void close() {
    if(connection != null) {
      connection.disconnect();
    }
  }
}
