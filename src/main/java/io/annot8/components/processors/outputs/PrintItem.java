package io.annot8.components.processors.outputs;

import static java.util.stream.Collectors.toList;

import com.google.common.base.Joiner;
import io.annot8.core.annotations.Annotation;
import io.annot8.core.components.Capabilities;
import io.annot8.core.components.Processor;
import io.annot8.core.components.responses.ProcessorResponse;
import io.annot8.core.data.Content;
import io.annot8.core.data.Item;
import io.annot8.core.data.Tags;
import io.annot8.core.exceptions.Annot8Exception;
import io.annot8.core.properties.ImmutableProperties;
import io.annot8.core.properties.MutableProperties;
import io.annot8.core.properties.Properties;
import io.annot8.core.stores.AnnotationStore;

public class PrintItem implements Processor {

  @Override
  public ProcessorResponse process(Item item) throws Annot8Exception {

    println("---", 0);
    println("Properties:", 0);
    print(item.getProperties(), 1);
    println("Content:", 0);
    item.getContents().forEach(this::print);

    return ProcessorResponse.ok();
  }


  private void print(Content<?> content) {
    println(content.getName(), 1);
    print(content.getTags(), 1);
    println("Properties:", 1);
    print(content.getProperties(), 2);
    println("Annotations:", 1);
    print(content.getAnnotations(), 2);
  }

  private void print(Tags tags, int indent) {
    println("Tags: " + Joiner.on(", ").join(tags.get().collect(toList())), indent);
  }

  private void print(Properties properties, int indent) {
    properties.getAll().entrySet().stream()
        .forEach( e ->
        println(String.format("%s: %s", e.getKey(), e.getValue()), indent));
  }


  private void print(AnnotationStore annotations, int indent) {
    annotations.getAll().forEach(a -> print(a, indent));
  }

  private void print(Annotation annotation, int indent) {
    println(annotation.toString(), indent);
  }


  private void println(String s, int indent) {
    StringBuilder sb = new StringBuilder();
    for(int i=0; i<indent; i++) {
      sb.append(" ");
    }
    sb.append(s);

    System.out.println(sb.toString());
  }

  @Override
  public Capabilities getCapabilities() {
    return null;
  }
}
