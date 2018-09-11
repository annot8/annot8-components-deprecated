package io.annot8.components.quantities.processors;

import io.annot8.common.data.content.Text;
import io.annot8.conventions.AnnotationTypes;
import io.annot8.conventions.PropertyKeys;
import io.annot8.core.annotations.Annotation;
import io.annot8.core.components.Processor;
import io.annot8.core.context.Context;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.Annot8Exception;
import io.annot8.core.stores.AnnotationStore;
import io.annot8.testing.testimpl.TestContext;
import io.annot8.testing.testimpl.TestItem;
import io.annot8.testing.testimpl.content.TestStringContent;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AreaTest {

  @Test
  public void testKM2() throws Annot8Exception {
    test("The region measured 800 square kilometres", "800 square kilometres", 8.0E8);
    test("The region measured 800 km^2", "800 km^2", 8.0E8);
  }

  @Test
  public void testM2() throws Annot8Exception {
    test("The field measured 400 square metres", "400 square metres", 400.0);
    test("The field measured 400 m^2", "400 m^2", 400.0);
  }

  @Test
  public void testCM2() throws Annot8Exception {
    test("The table measured 200 square centimetres", "200 square centimetres", 0.02);
    test("The table measured 200 cm^2", "200 cm^2", 0.02);
  }

  @Test
  public void testMM2() throws Annot8Exception {
    test("The chip measured 100 square millimetres", "100 square millimetres", 0.0001);
    test("The chip measured 100 mm^2", "100 mm^2", 0.0001);
  }

  @Test
  public void testMi2() throws Annot8Exception {
    test("The region measured 800 square miles", "800 square miles", 2.07199048E9);
    test("The region measured 800 mi^2", "800 mi^2", 2.07199048E9);
  }

  @Test
  public void testYd2() throws Annot8Exception {
    test("The field measured 400 square yards", "400 square yards", 334.450956);
    test("The field measured 400 yd^2", "400 yd^2", 334.450956);
  }

  @Test
  public void testFt2() throws Annot8Exception {
    test("The table measured 100 square feet", "100 square feet", 9.2903044);
    test("The table measured 100 ft^2", "100 ft^2", 9.2903044);
  }

  @Test
  public void testIn2() throws Annot8Exception {
    test("The chip measured 0.5 square inches", "0.5 square inches", 0.00032258);
    test("The chip measured 0.5 in^2", "0.5 in^2", 0.00032258);
  }

  @Test
  public void testAcre() throws Annot8Exception {
    test("The field measured 400 acres", "400 acres", 1618742.56);
  }

  @Test
  public void testHectare() throws Annot8Exception {
    test("The field measured 400 hectares", "400 hectares", 4000000.0);
  }

  private void test(String text, String expectedMatch, Double expectedValue) throws Annot8Exception {
    try(
        Processor p = new Area()
    ) {
      Item item = new TestItem();
      Context context = new TestContext();

      p.configure(context);

      Text content = item.create(TestStringContent.class).withName("test")
          .withData(text).save();

      p.process(item);

      AnnotationStore store = content.getAnnotations();

      List<Annotation> annotations = store.getAll().collect(Collectors.toList());
      Assertions.assertEquals(1, annotations.size());

      Annotation a = annotations.get(0);
      Assertions.assertEquals(AnnotationTypes.ANNOTATION_TYPE_AREA, a.getType());
      Assertions.assertEquals(content.getId(), a.getContentId());
      Assertions.assertEquals(expectedMatch, a.getBounds().getData(content).get());
      Assertions.assertEquals(2, a.getProperties().getAll().size());
      Assertions.assertEquals("m^2", a.getProperties().get(PropertyKeys.PROPERTY_KEY_UNIT).get());
      Assertions.assertEquals(expectedValue, (Double)a.getProperties().get(PropertyKeys.PROPERTY_KEY_VALUE).get(), 0.000001);
    }
  }

}
