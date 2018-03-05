package io.annot8.components.processors.regex;

import io.annot8.common.content.Text;
import io.annot8.common.factories.ItemFactory;
import io.annot8.components.processors.regex.Regex.RegexSettings;
import io.annot8.core.components.Processor;
import io.annot8.core.context.Context;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.Annot8Exception;
import io.annot8.core.stores.AnnotationStore;
import io.annot8.defaultimpl.content.SimpleText;
import io.annot8.defaultimpl.context.SimpleContext;
import io.annot8.defaultimpl.data.SimpleItem;
import io.annot8.defaultimpl.factories.SimpleContentBuilderFactoryRegistry;
import io.annot8.defaultimpl.factories.SimpleItemFactory;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

public class RegexTest {

  @Test
  public void testRegex() throws Annot8Exception{
    Processor p = new Regex();

    RegexSettings rs = new RegexSettings(Pattern.compile("[0-9]+"), 0, "number");

    //TODO: These should be replaced by Test* rather than using Simple*
    //TODO: Provide some abstract test base classes that provide this common functionality
    SimpleContentBuilderFactoryRegistry contentBuilderFactoryRegistry = new SimpleContentBuilderFactoryRegistry();
    contentBuilderFactoryRegistry.register(Text.class, new SimpleText.BuilderFactory());
    ItemFactory itemFactory = new SimpleItemFactory(contentBuilderFactoryRegistry);
    Context context = new SimpleContext(itemFactory, rs);

    p.configure(context);

    Item item = new SimpleItem(itemFactory, contentBuilderFactoryRegistry);
    item.create(Text.class).withName("test").withData("37 + 5 = 42").save();

    p.process(item);

    AnnotationStore store = item.getContent("test").get().getAnnotations();
    store.getAll().forEach(System.out::println);
  }

}
