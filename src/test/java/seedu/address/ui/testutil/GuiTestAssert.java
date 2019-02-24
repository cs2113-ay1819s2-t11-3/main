package seedu.address.ui.testutil;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.stream.Collectors;

import guitests.guihandles.EntryCardHandle;
import guitests.guihandles.EntryListPanelHandle;
import guitests.guihandles.ResultDisplayHandle;
import seedu.address.model.entry.Entry;

/**
 * A set of assertion methods useful for writing GUI tests.
 */
public class GuiTestAssert {
    /**
     * Asserts that {@code actualCard} displays the same values as {@code expectedCard}.
     */
    public static void assertCardEquals(EntryCardHandle expectedCard, EntryCardHandle actualCard) {
        assertEquals(expectedCard.getId(), actualCard.getId());
        assertEquals(expectedCard.getAddress(), actualCard.getAddress());
        assertEquals(expectedCard.getEmail(), actualCard.getEmail());
        assertEquals(expectedCard.getName(), actualCard.getName());
        assertEquals(expectedCard.getPhone(), actualCard.getPhone());
        assertEquals(expectedCard.getTags(), actualCard.getTags());
    }

    /**
     * Asserts that {@code actualCard} displays the details of {@code expectedEntry}.
     */
    public static void assertCardDisplaysEntry(Entry expectedEntry, EntryCardHandle actualCard) {
        assertEquals(expectedEntry.getName().fullName, actualCard.getName());
        assertEquals(expectedEntry.getPhone().value, actualCard.getPhone());
        assertEquals(expectedEntry.getEmail().value, actualCard.getEmail());
        assertEquals(expectedEntry.getAddress().value, actualCard.getAddress());
        assertEquals(expectedEntry.getTags().stream().map(tag -> tag.tagName).collect(Collectors.toList()),
                actualCard.getTags());
    }

    /**
     * Asserts that the list in {@code entryListPanelHandle} displays the details of {@code entrys} correctly and
     * in the correct order.
     */
    public static void assertListMatching(EntryListPanelHandle entryListPanelHandle, Entry... entrys) {
        for (int i = 0; i < entrys.length; i++) {
            entryListPanelHandle.navigateToCard(i);
            assertCardDisplaysEntry(entrys[i], entryListPanelHandle.getEntryCardHandle(i));
        }
    }

    /**
     * Asserts that the list in {@code entryListPanelHandle} displays the details of {@code entrys} correctly and
     * in the correct order.
     */
    public static void assertListMatching(EntryListPanelHandle entryListPanelHandle, List<Entry> entrys) {
        assertListMatching(entryListPanelHandle, entrys.toArray(new Entry[0]));
    }

    /**
     * Asserts the size of the list in {@code entryListPanelHandle} equals to {@code size}.
     */
    public static void assertListSize(EntryListPanelHandle entryListPanelHandle, int size) {
        int numberOfPeople = entryListPanelHandle.getListSize();
        assertEquals(size, numberOfPeople);
    }

    /**
     * Asserts the message shown in {@code resultDisplayHandle} equals to {@code expected}.
     */
    public static void assertResultMessage(ResultDisplayHandle resultDisplayHandle, String expected) {
        assertEquals(expected, resultDisplayHandle.getText());
    }
}
