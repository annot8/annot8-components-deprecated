/* Annot8 (annot8.io) - Licensed under Apache-2.0. */
package io.annot8.components.db.processors;

import java.net.URL;
import java.util.function.Supplier;

import io.annot8.common.data.content.URLContent;
import io.annot8.common.implementations.content.AbstractContentBuilder;
import io.annot8.common.implementations.content.AbstractContentBuilderFactory;
import io.annot8.common.implementations.stores.SaveCallback;
import io.annot8.core.data.Content;
import io.annot8.core.data.Item;
import io.annot8.core.properties.ImmutableProperties;
import io.annot8.core.stores.AnnotationStore;
import io.annot8.testing.testimpl.TestAnnotationStore;

public class TestURL implements URLContent {

  private URL data;
  private String id;
  private String name;
  private ImmutableProperties properties;
  private AnnotationStore store;

  public TestURL(String id, String name, ImmutableProperties properties, URL data) {
    this.id = id;
    this.name = name;
    this.properties = properties;
    this.store = new TestAnnotationStore();
    this.data = data;
  }

  @Override
  public URL getData() {
    return data;
  }

  @Override
  public Class<URL> getDataClass() {
    return URL.class;
  }

  @Override
  public Class<? extends Content<URL>> getContentClass() {
    return TestURL.class;
  }

  @Override
  public AnnotationStore getAnnotations() {
    return store;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public ImmutableProperties getProperties() {
    return properties;
  }

  public static class TestURLBuilder extends AbstractContentBuilder<URL, URLContent> {

    public TestURLBuilder(SaveCallback<URLContent, URLContent> callback) {
      super(callback);
    }

    @Override
    protected URLContent create(
        String id, String name, ImmutableProperties properties, Supplier<URL> data) {
      return new TestURL(id, name, properties, data.get());
    }
  }

  public static class TestURLBuilderFactory extends AbstractContentBuilderFactory<URL, URLContent> {

    protected TestURLBuilderFactory() {
      super(URL.class, URLContent.class);
    }

    @Override
    public Builder<URLContent, URL> create(Item item, SaveCallback<URLContent, URLContent> saver) {
      return new TestURLBuilder(saver);
    }
  }
}
