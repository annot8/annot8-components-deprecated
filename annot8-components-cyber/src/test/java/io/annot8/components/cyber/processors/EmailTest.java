package io.annot8.components.cyber.processors;

import io.annot8.common.data.content.Text;
import io.annot8.conventions.AnnotationTypes;
import io.annot8.core.annotations.Annotation;
import io.annot8.core.components.Processor;
import io.annot8.core.context.Context;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.Annot8Exception;
import io.annot8.core.settings.EmptySettings;
import io.annot8.core.settings.SettingsClass;
import io.annot8.core.stores.AnnotationStore;
import io.annot8.test.TestContext;
import io.annot8.test.TestItem;
import io.annot8.test.content.TestStringContent;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EmailTest {

  @Test
  public void testSettings() {
    SettingsClass annotation = Email.class.getAnnotation(SettingsClass.class);
    Assertions.assertEquals(EmptySettings.class, annotation.value());
  }

  @Test
  public void testEmail() throws Annot8Exception {
    try(
        Processor p = new Email()
    ) {
      Item item = new TestItem();
      Context context = new TestContext();

      p.configure(context);

      Text content = item.create(TestStringContent.class).withName("test")
          .withData("Her e-mail address was sally@example.com").save();

      p.process(item);

      AnnotationStore store = item.getContent("test").get().getAnnotations();

      List<Annotation> annotations = store.getAll().collect(Collectors.toList());
      Assertions.assertEquals(1, annotations.size());

      Annotation a = annotations.get(0);
      Assertions.assertEquals(AnnotationTypes.ANNOTATION_TYPE_EMAIL, a.getType());
      Assertions.assertEquals(content.getName(), a.getContentName());
      Assertions.assertEquals("sally@example.com", a.getBounds().getData(content).get());
      Assertions.assertEquals(0, a.getProperties().getAll().size());
    }
  }
}
