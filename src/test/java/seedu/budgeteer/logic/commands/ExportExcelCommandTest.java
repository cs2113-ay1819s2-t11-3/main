//@@author ngkaicong
package seedu.budgeteer.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.budgeteer.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.budgeteer.model.DirectoryPath.HOME_DIRECTORY_STRING;
import static seedu.budgeteer.testutil.TypicalEntrys.getTypicalAddressBook;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import seedu.budgeteer.commons.core.Messages;
import seedu.budgeteer.commons.util.ExcelUtil;
import seedu.budgeteer.logic.CommandHistory;
import seedu.budgeteer.model.DirectoryPath;
import seedu.budgeteer.model.Model;
import seedu.budgeteer.model.ModelManager;
import seedu.budgeteer.model.UserPrefs;
import seedu.budgeteer.model.entry.Date;
import seedu.budgeteer.model.entry.DateIsWithinIntervalPredicate;
import seedu.budgeteer.model.entry.Entry;
import seedu.budgeteer.testutil.TypicalEntrys;




public class ExportExcelCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private CommandHistory commandHistory = new CommandHistory();

    @Test
    public void equals() {
        Date startDate1 = TypicalEntrys.TYPICAL_START_DATE1;
        Date startDate2 = TypicalEntrys.TYPICAL_START_DATE1;
        Date endDate1 = TypicalEntrys.TYPICAL_END_DATE;
        Date endDate2 = TypicalEntrys.TYPICAL_END_DATE1;
        String directoryPath = HOME_DIRECTORY_STRING;
        String directoryPath1 = DirectoryPath.WORKING_DIRECTORY_STRING;

        ExportExcelCommand exportExcelCommand11 = new ExportExcelCommand();
        ExportExcelCommand exportExcelCommand21 = new ExportExcelCommand(directoryPath);
        ExportExcelCommand exportExcelCommand31 = new ExportExcelCommand(startDate1, endDate1);
        ExportExcelCommand exportExcelCommand41 = new ExportExcelCommand(startDate1, endDate1, directoryPath);

        ExportExcelCommand exportExcelCommand22 = new ExportExcelCommand(directoryPath1);
        ExportExcelCommand exportExcelCommand32 = new ExportExcelCommand(startDate2, endDate2);
        ExportExcelCommand exportExcelCommand42 = new ExportExcelCommand(startDate2, endDate2, directoryPath1);

        // same object -> returns true
        Assert.assertTrue(exportExcelCommand11.equals(exportExcelCommand11));
        Assert.assertTrue(exportExcelCommand21.equals(exportExcelCommand21));
        Assert.assertTrue(exportExcelCommand31.equals(exportExcelCommand31));
        Assert.assertTrue(exportExcelCommand41.equals(exportExcelCommand41));

        // same value -> returns true
        ExportExcelCommand exportExcelCommand11Copy = new ExportExcelCommand();
        ExportExcelCommand exportExcelCommand21Copy = new ExportExcelCommand(directoryPath);
        ExportExcelCommand exportExcelCommand31Copy = new ExportExcelCommand(startDate1, endDate1);
        ExportExcelCommand exportExcelCommand41Copy = new ExportExcelCommand(startDate1, endDate1, directoryPath);

        Assert.assertTrue(exportExcelCommand11.equals(exportExcelCommand11Copy));
        Assert.assertTrue(exportExcelCommand21.equals(exportExcelCommand21Copy));
        Assert.assertTrue(exportExcelCommand31.equals(exportExcelCommand31Copy));
        Assert.assertTrue(exportExcelCommand41.equals(exportExcelCommand41Copy));

        //different value -> returns false
        Assert.assertFalse(exportExcelCommand31.equals(exportExcelCommand32));
        Assert.assertFalse(exportExcelCommand41.equals(exportExcelCommand42));

        // different types -> returns false
        Assert.assertFalse(exportExcelCommand41.equals(Arrays.asList(startDate1, endDate1, directoryPath)));

        // null -> returns false
        Assert.assertFalse(exportExcelCommand11.equals(null));
    }

    @Test
    public void execute_zeroRecordFound_noRecordFound() {
        String nameFile = ExcelUtil.setNameExcelFile(
                TypicalEntrys.TYPICAL_START_DATE1, TypicalEntrys.TYPICAL_END_DATE1);
        ExportExcelCommand command = new ExportExcelCommand(
                TypicalEntrys.TYPICAL_START_DATE1, TypicalEntrys.TYPICAL_END_DATE1, HOME_DIRECTORY_STRING);
        String expectedMessage;
        expectedModel.updateFilteredEntryList(new DateIsWithinIntervalPredicate(
                TypicalEntrys.TYPICAL_START_DATE1, TypicalEntrys.TYPICAL_END_DATE1));
        List<Entry> entries = expectedModel.getFilteredEntryList();
        if (entries.size() > 0) {
            expectedMessage = String.format(
                    Messages.MESSAGE_EXCEL_FILE_WRITTEN_SUCCESSFULLY,
                    nameFile, DirectoryPath.HOME_DIRECTORY.getDirectoryPathValue());
        } else {
            expectedMessage = Messages.MESSAGE_EXPORT_COMMAND_ERRORS;
        }
        assertCommandSuccess(command, model, commandHistory, expectedMessage, expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredEntryList());
    }

    @Test
    public void execute_multipleRecordsFound() {
        String nameFile = ExcelUtil.setNameExcelFile(
                TypicalEntrys.TYPICAL_START_FAR_DATE, TypicalEntrys.TYPICAL_END_FAR_DATE);
        String expectedMessage = String.format(
                Messages.MESSAGE_EXCEL_FILE_WRITTEN_SUCCESSFULLY, nameFile, HOME_DIRECTORY_STRING);
        ExportExcelCommand command = new ExportExcelCommand(
                TypicalEntrys.TYPICAL_START_FAR_DATE, TypicalEntrys.TYPICAL_END_FAR_DATE, HOME_DIRECTORY_STRING);
        expectedModel.updateFilteredEntryList(new DateIsWithinIntervalPredicate(
                TypicalEntrys.TYPICAL_START_FAR_DATE, TypicalEntrys.TYPICAL_END_FAR_DATE));
        assertCommandSuccess(command, model, commandHistory, expectedMessage, expectedModel);
    }
}
