package io.annot8.components.text.processors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import io.annot8.common.data.content.Text;
import io.annot8.core.data.Content;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.Annot8Exception;
import io.annot8.core.exceptions.IncompleteException;
import io.annot8.core.exceptions.UnsupportedContentException;
import io.annot8.testing.testimpl.TestItem;
import io.annot8.testing.testimpl.content.TestStringContent;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

public class CapitaliseTest {

    @Test
    public void testProcess() {
        Capitalise capitalise = new Capitalise();
        Item item = new TestItem();

        Text lowerCase = null;
        try {
            lowerCase = item.create(TestStringContent.class).withName("lowerCase").withData("test").save();
        } catch (UnsupportedContentException | IncompleteException e) {
            fail("Error is not expected here", e);
        }

        try {
            capitalise.process(item, lowerCase);
        } catch (Annot8Exception e) {
            fail("Error is not expected here", e);
        }

        List<String> collect = item.listNames().collect(Collectors.toList());

        assertTrue(item.hasContentOfName("lowerCase_capitalised"));
        List<Content<?>> capitalised = item.getContentByName("lowerCase_capitalised").collect(Collectors.toList());
        assertEquals(1, capitalised.size());
        assertEquals("TEST", capitalised.get(0).getData());
    }

}