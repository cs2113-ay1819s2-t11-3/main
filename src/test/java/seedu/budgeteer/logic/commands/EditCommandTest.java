package seedu.budgeteer.logic.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static seedu.budgeteer.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.budgeteer.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.budgeteer.logic.commands.CommandTestUtil.VALID_DATE_BOB;
import static seedu.budgeteer.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.budgeteer.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.budgeteer.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.budgeteer.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.budgeteer.logic.commands.CommandTestUtil.showEntryAtIndex;
import static seedu.budgeteer.testutil.TypicalEntrys.getTypicalAddressBook;
import static seedu.budgeteer.testutil.TypicalIndexes.INDEX_FIRST_ENTRY;
import static seedu.budgeteer.testutil.TypicalIndexes.INDEX_SECOND_ENTRY;

import org.junit.Test;

import seedu.budgeteer.commons.core.Messages;
import seedu.budgeteer.commons.core.index.Index;
import seedu.budgeteer.logic.CommandHistory;
import seedu.budgeteer.logic.commands.EditCommand.EditEntryDescriptor;
import seedu.budgeteer.model.EntriesBook;
import seedu.budgeteer.model.Model;
import seedu.budgeteer.model.ModelManager;
import seedu.budgeteer.model.UserPrefs;
import seedu.budgeteer.model.entry.Entry;
import seedu.budgeteer.testutil.EditEntryDescriptorBuilder;
import seedu.budgeteer.testutil.EntryBuilder;

/**
 * Contains integration tests (interaction with the Model, UndoCommand and RedoCommand) and unit tests for EditCommand.
 */
public class EditCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private CommandHistory commandHistory = new CommandHistory();

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Entry editedEntry = new EntryBuilder().build();
        EditEntryDescriptor descriptor = new EditEntryDescriptorBuilder(editedEntry).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_ENTRY, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_ENTRY_SUCCESS, editedEntry);

        Model expectedModel = new ModelManager(new EntriesBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setEntry(model.getFilteredEntryList().get(0), editedEntry);
        expectedModel.commitAddressBook();

        assertCommandSuccess(editCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Index indexLastEntry = Index.fromOneBased(model.getFilteredEntryList().size());
        Entry lastEntry = model.getFilteredEntryList().get(indexLastEntry.getZeroBased());

        EntryBuilder entryInList = new EntryBuilder(lastEntry);
        Entry editedEntry = entryInList.withName(VALID_NAME_BOB).withDate(VALID_DATE_BOB)
                .withTags(VALID_TAG_HUSBAND).build();

        EditEntryDescriptor descriptor = new EditEntryDescriptorBuilder().withName(VALID_NAME_BOB)
                .withDate(VALID_DATE_BOB).withTags(VALID_TAG_HUSBAND).build();
        EditCommand editCommand = new EditCommand(indexLastEntry, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_ENTRY_SUCCESS, editedEntry);

        Model expectedModel = new ModelManager(new EntriesBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setEntry(lastEntry, editedEntry);
        expectedModel.commitAddressBook();

        assertCommandSuccess(editCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        EditCommand editCommand = new EditCommand(INDEX_FIRST_ENTRY, new EditEntryDescriptor());
        Entry editedEntry = model.getFilteredEntryList().get(INDEX_FIRST_ENTRY.getZeroBased());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_ENTRY_SUCCESS, editedEntry);

        Model expectedModel = new ModelManager(new EntriesBook(model.getAddressBook()), new UserPrefs());
        expectedModel.commitAddressBook();

        assertCommandSuccess(editCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        showEntryAtIndex(model, INDEX_FIRST_ENTRY);

        Entry entryInFilteredList = model.getFilteredEntryList().get(INDEX_FIRST_ENTRY.getZeroBased());
        Entry editedEntry = new EntryBuilder(entryInFilteredList).withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_ENTRY,
                new EditEntryDescriptorBuilder().withName(VALID_NAME_BOB).build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_ENTRY_SUCCESS, editedEntry);

        Model expectedModel = new ModelManager(new EntriesBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setEntry(model.getFilteredEntryList().get(0), editedEntry);
        expectedModel.commitAddressBook();

        assertCommandSuccess(editCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicateEntryFilteredList_failure() {
        showEntryAtIndex(model, INDEX_FIRST_ENTRY);

        // edit entry in filtered list into a duplicate in budgeteer book
        Entry entryInList = model.getAddressBook().getEntryList().get(INDEX_SECOND_ENTRY.getZeroBased());
        EditCommand editCommand = new EditCommand(INDEX_FIRST_ENTRY,
                new EditEntryDescriptorBuilder(entryInList).build());

        assertCommandFailure(editCommand, model, commandHistory, EditCommand.MESSAGE_DUPLICATE_ENTRY);
    }

    @Test
    public void execute_invalidEntryIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredEntryList().size() + 1);
        EditEntryDescriptor descriptor = new EditEntryDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(outOfBoundIndex, descriptor);

        assertCommandFailure(editCommand, model, commandHistory, Messages.MESSAGE_INVALID_ENTRY_DISPLAYED_INDEX);
    }

    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of budgeteer book
     */
    @Test
    public void execute_invalidEntryIndexFilteredList_failure() {
        showEntryAtIndex(model, INDEX_FIRST_ENTRY);
        Index outOfBoundIndex = INDEX_SECOND_ENTRY;
        // ensures that outOfBoundIndex is still in bounds of budgeteer book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getEntryList().size());

        EditCommand editCommand = new EditCommand(outOfBoundIndex,
                new EditEntryDescriptorBuilder().withName(VALID_NAME_BOB).build());

        assertCommandFailure(editCommand, model, commandHistory, Messages.MESSAGE_INVALID_ENTRY_DISPLAYED_INDEX);
    }

    @Test
    public void executeUndoRedo_validIndexUnfilteredList_success() throws Exception {
        Entry editedEntry = new EntryBuilder().build();
        Entry entryToEdit = model.getFilteredEntryList().get(INDEX_FIRST_ENTRY.getZeroBased());
        EditEntryDescriptor descriptor = new EditEntryDescriptorBuilder(editedEntry).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_ENTRY, descriptor);
        Model expectedModel = new ModelManager(new EntriesBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setEntry(entryToEdit, editedEntry);
        expectedModel.commitAddressBook();

        // edit -> first entry edited
        editCommand.execute(model, commandHistory);

        // undo -> reverts addressbook back to previous state and filtered entry list to show all entrys
        expectedModel.undoAddressBook();
        assertCommandSuccess(new UndoCommand(), model, commandHistory, UndoCommand.MESSAGE_SUCCESS, expectedModel);

        // redo -> same first entry edited again
        expectedModel.redoAddressBook();
        assertCommandSuccess(new RedoCommand(), model, commandHistory, RedoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void executeUndoRedo_invalidIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredEntryList().size() + 1);
        EditEntryDescriptor descriptor = new EditEntryDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(outOfBoundIndex, descriptor);

        // execution failed -> budgeteer book state not added into model
        assertCommandFailure(editCommand, model, commandHistory, Messages.MESSAGE_INVALID_ENTRY_DISPLAYED_INDEX);

        // single budgeteer book state in model -> undoCommand and redoCommand fail
        assertCommandFailure(new UndoCommand(), model, commandHistory, UndoCommand.MESSAGE_FAILURE);
        assertCommandFailure(new RedoCommand(), model, commandHistory, RedoCommand.MESSAGE_FAILURE);
    }

    /**
     * 1. Edits a {@code Entry} from a filtered list.
     * 2. Undo the edit.
     * 3. The unfiltered list should be shown now. Verify that the index of the previously edited entry in the
     * unfiltered list is different from the index at the filtered list.
     * 4. Redo the edit. This ensures {@code RedoCommand} edits the entry object regardless of indexing.
     */
    @Test
    public void executeUndoRedo_validIndexFilteredList_sameEntryEdited() throws Exception {
        Entry editedEntry = new EntryBuilder().build();
        EditEntryDescriptor descriptor = new EditEntryDescriptorBuilder(editedEntry).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_ENTRY, descriptor);
        Model expectedModel = new ModelManager(new EntriesBook(model.getAddressBook()), new UserPrefs());

        showEntryAtIndex(model, INDEX_SECOND_ENTRY);
        Entry entryToEdit = model.getFilteredEntryList().get(INDEX_FIRST_ENTRY.getZeroBased());
        expectedModel.setEntry(entryToEdit, editedEntry);
        expectedModel.commitAddressBook();

        // edit -> edits second entry in unfiltered entry list / first entry in filtered entry list
        editCommand.execute(model, commandHistory);

        // undo -> reverts addressbook back to previous state and filtered entry list to show all entrys
        expectedModel.undoAddressBook();
        assertCommandSuccess(new UndoCommand(), model, commandHistory, UndoCommand.MESSAGE_SUCCESS, expectedModel);

        assertNotEquals(model.getFilteredEntryList().get(INDEX_FIRST_ENTRY.getZeroBased()), entryToEdit);
        // redo -> edits same second entry in unfiltered entry list
        expectedModel.redoAddressBook();
        assertCommandSuccess(new RedoCommand(), model, commandHistory, RedoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void equals() {
        final EditCommand standardCommand = new EditCommand(INDEX_FIRST_ENTRY, DESC_AMY);

        // same values -> returns true
        EditEntryDescriptor copyDescriptor = new EditEntryDescriptor(DESC_AMY);
        EditCommand commandWithSameValues = new EditCommand(INDEX_FIRST_ENTRY, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_SECOND_ENTRY, DESC_AMY)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_FIRST_ENTRY, DESC_BOB)));
    }

}
