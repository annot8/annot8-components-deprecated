package io.annot8.components.grouping.processors;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import io.annot8.common.data.content.Text;
import io.annot8.components.base.processors.AbstractTextAnnotator;
import io.annot8.core.annotations.Annotation;
import io.annot8.core.annotations.Group;
import io.annot8.core.capabilities.CreatesGroup;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.IncompleteException;
import io.annot8.core.stores.GroupStore;
import java.util.Optional;

@CreatesGroup(GroupByTypeAndValueAnnotator.TYPE)
public class GroupByTypeAndValueAnnotator extends AbstractTextAnnotator {

  public static final String TYPE = "exactMatches";
  private static final String ROLE = "as";


  @Override
  protected void process(Item item, Text content) {

    SetMultimap<String, Annotation> map = HashMultimap.create();

    // Collate up all the annotations which have the same
    // TODO: Doing this over all bounds (but it could just be done over spanbounds)

    content.getAnnotations().getAll()
        .forEach(a -> {
          Optional<String> optional = content.getText(a);
          optional.ifPresent(covered -> {
            String key = toKey(a.getType(), covered);
            map.put(key, a);
          });
        });

    // Create a group for things which are the same
    GroupStore groupStore = item.getGroups();
    map.asMap().values().forEach(annotations -> {
      Group.Builder builder = groupStore.create()
          .withType(TYPE);

      annotations.forEach(a -> builder.withAnnotation(ROLE, a));

      // TODO: I'm not sure about this incomplete exception... perhasp move to move runtime stuff
      // as basically we'll be catching Annot8Exception at the pipeline level anyway?
      try {
        builder.save();
      } catch (IncompleteException e) {
        e.printStackTrace();
      }
    });
  }

  private String toKey(String type, String covered) {
    return type + ":" + covered;
  }

}
