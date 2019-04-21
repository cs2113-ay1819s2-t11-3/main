package seedu.budgeteer.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.budgeteer.commons.util.ExcelUtil.setPathFile;
import static seedu.budgeteer.model.Model.PREDICATE_SHOW_ALL_ENTRYS;

import java.util.List;
import java.util.function.Predicate;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import seedu.budgeteer.commons.core.Messages;
import seedu.budgeteer.commons.util.ExcelUtil;
import seedu.budgeteer.logic.CommandHistory;
import seedu.budgeteer.model.DirectoryPath;
import seedu.budgeteer.model.Model;
import seedu.budgeteer.model.entry.Date;
import seedu.budgeteer.model.entry.DateIsWithinIntervalPredicate;
import seedu.budgeteer.model.entry.Entry;
import seedu.budgeteer.model.summary.SummaryByDateList;
import seedu.budgeteer.ui.SummaryEntry;



/**
 * Export the data of the records within specific period.
 */
public class ExportExcelCommand extends Command {
    public static final String COMMAND_WORD = "export";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Exports the records within specific period or all records in the Budgeteer into Excel file .\n"
            + "Parameters: START_DATE END_DATE DIRECTORY_PATH,"
            + "START_DATE should be equal to or smaller than END_DATE.\n"
            + "You can specifically type what you want to conform. Date/period start with d/ "
            + "and Directory path start with dir/.\n"
            + "Example 1: You want to export all the records in the Budgeter to Excel file"
            + "and store them in the Working directory: " + DirectoryPath.WORKING_DIRECTORY_STRING
            + "Example 1: You want to set Directory: "
            + COMMAND_WORD + " dir/" + DirectoryPath.WORKING_DIRECTORY_STRING + "\n"
            + "Example 2: You want to set records have only 1 date: " + COMMAND_WORD + " d/31-3-1999\n"
            + "Example 3: You want to set records whose date lies within the period: "
            + COMMAND_WORD + "d/31-3-1999 31-3-2019\n"
            + "Example 4: You want to export all the records in the Budgeter: " + COMMAND_WORD + "\n"
            + "Example 5: You want to export records lies within the period and set specific Directory: "
            + COMMAND_WORD + " d/31-3-1999 31-3-2019" + " dir/" + DirectoryPath.WORKING_DIRECTORY_STRING + "\n";
    public static final int SINGLE_MODE = 1;
    public static final int DUO_MODE = 2;
    public static final int FIRST_ELEMENT = 0;
    public static final int SECOND_ELEMENT = 1;

    private static final int EMPTY_RECORD_LIST_SIZE_EXPORT = 0;

    private Date startDate;
    private Date endDate;
    private String directoryPath;
    private Predicate<Entry> predicate;

    public ExportExcelCommand() {
        this.startDate = null;
        this.endDate = null;
        this.directoryPath = DirectoryPath.WORKING_DIRECTORY_STRING;
        this.predicate = PREDICATE_SHOW_ALL_ENTRYS;

    }

    public ExportExcelCommand(String directoryPath) {
        this.startDate = null;
        this.endDate = null;
        this.directoryPath = directoryPath;
        this.predicate = PREDICATE_SHOW_ALL_ENTRYS;
    }

    public ExportExcelCommand(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.directoryPath = DirectoryPath.WORKING_DIRECTORY_STRING;
        this.predicate = new DateIsWithinIntervalPredicate(startDate, endDate);
    }

    public ExportExcelCommand(Date startDate, Date endDate, String directoryPath) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.directoryPath = directoryPath;
        this.predicate = new DateIsWithinIntervalPredicate(startDate, endDate);
    }

    @Override
    public CommandResult execute(Model model, CommandHistory commandHistory) {
        requireNonNull(this);
        model.updateFilteredEntryList(predicate);
        SummaryByDateList summaryList = new SummaryByDateList(model.getFilteredEntryList());
        List<Entry> entryList = model.getFilteredEntryList();
        List<SummaryEntry> daySummaryEntryList = summaryList.getSummaryList();
        String nameFile = ExcelUtil.setNameExcelFile(startDate, endDate);
        String message;
        String filePath = setPathFile(nameFile, directoryPath);

        if (exportDataIntoExcelSheetWithGivenRecords(entryList, daySummaryEntryList, filePath)) {
            message = String.format(Messages.MESSAGE_EXCEL_FILE_WRITTEN_SUCCESSFULLY, nameFile, directoryPath);
        } else {
            message = Messages.MESSAGE_EXPORT_COMMAND_ERRORS;
        }
        return new CommandResult(message);
    }

    /**
     * Export the records into Excel File.
     */
    private static Boolean exportDataIntoExcelSheetWithGivenRecords(
            List<Entry> entryList, List<SummaryEntry> daySummaryEntryList, String filePath) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet recordData = workbook.createSheet("ENTRIES DATA");
        XSSFSheet summaryData = workbook.createSheet("SUMMARY DATA");
        if (entryList.size() > 0) {
            ExcelUtil.writeExcelSheetIntoDirectory(
                    entryList, daySummaryEntryList, recordData, summaryData, workbook, filePath);
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ExportExcelCommand // instanceof handles nulls
                && predicate.equals(((ExportExcelCommand) other).predicate)
                && directoryPath.equals(((ExportExcelCommand) other).directoryPath)); // state check
    }
}
