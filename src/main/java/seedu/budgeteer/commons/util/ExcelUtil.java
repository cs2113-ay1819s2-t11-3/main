package seedu.budgeteer.commons.util;

import static seedu.budgeteer.logic.parser.CliSyntax.PREFIX_TAG;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Chart;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.charts.AxisCrosses;
import org.apache.poi.ss.usermodel.charts.AxisPosition;
import org.apache.poi.ss.usermodel.charts.ChartAxis;
import org.apache.poi.ss.usermodel.charts.ChartDataSource;
import org.apache.poi.ss.usermodel.charts.ChartLegend;
import org.apache.poi.ss.usermodel.charts.DataSources;
import org.apache.poi.ss.usermodel.charts.LegendPosition;
import org.apache.poi.ss.usermodel.charts.LineChartData;
import org.apache.poi.ss.usermodel.charts.LineChartSeries;
import org.apache.poi.ss.usermodel.charts.ValueAxis;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import seedu.budgeteer.commons.core.LogsCenter;
import seedu.budgeteer.commons.core.Messages;
import seedu.budgeteer.logic.parser.ArgumentMultimap;
import seedu.budgeteer.logic.parser.ArgumentTokenizer;
import seedu.budgeteer.logic.parser.ParserUtil;
import seedu.budgeteer.logic.parser.exceptions.ParseException;
import seedu.budgeteer.model.DirectoryPath;
import seedu.budgeteer.model.entry.CashFlow;
import seedu.budgeteer.model.entry.Date;
import seedu.budgeteer.model.entry.Entry;
import seedu.budgeteer.model.entry.Name;
import seedu.budgeteer.model.tag.Tag;
import seedu.budgeteer.ui.SummaryEntry;

/**
 * Transfer data into Excel file utilities.
 */
public class ExcelUtil {
    private static final int FIRST_COLUMN = 0;
    private static final int SECOND_COLUMN = 1;
    private static final int THIRD_COLUMN = 2;
    private static final int FOURTH_COLUMN = 3;
    private static final int FIRST_ROW = 0;
    private static final int SECOND_ROW = 1;
    private static final int THIRD_ROW = 2;
    private static final int FOURTH_ROW = 3;
    private static final int MAXIMUM_GAP_BETWEEN_COLUMN = 4;
    private static final int LEFT_OUT_CHARACTER = 4;
    private static final int STARTING_INDEX = 0;
    private static final int STARTING_SHEET = 0;
    private static final int RECORD_EMPTY = 0;
    private static final int STARTING_CURRENCY = 2;
    private static final char MINUS_SIGN_CHAR = '-';
    private static final char PLUS_SIGN_CHAR = '+';
    private static final char CURRENCY_CHAR = '$';
    private static final Double CHANGE_TO_DOUBLE = 1.0;
    private static final String PLUS_SIGN_STRING = "+";
    private static final String MINUS_SIGN_STRING = "-";
    private static final String CURRENCY_STRING = "$";
    private static final String WHITE_SPACE = " ";
    private static final String NAME_TITLE = "NAME";
    private static final String DATE_TITLE = "DATE";
    private static final String MONEY_TITLE = "MONEY";
    private static final String TAG_TITLE = "TAGS";
    private static final String INCOME_TITLE = "INCOME";
    private static final String OUTCOME_TITLE = "EXPENSES";
    private static final String TOTAL_MONEY = "NET";
    private static final String TAG_SEPARATOR = "  ... ";

    private static Logger logger = LogsCenter.getLogger(ExcelUtil.class);

    //==========================================MAIN METHOD============================================================

    /**
     * Write the excel sheet into Directory.
     */
    public static void writeExcelSheetIntoDirectory (List<Entry> entryList,
                                                     List<SummaryEntry> daySummaryEntryList,
                                                     XSSFSheet recordDataSheet, XSSFSheet summaryDataSheet,
                                                     XSSFWorkbook workbook, String filePath) {
        try {
            writeDataIntoExcelSheetRecord(entryList, recordDataSheet);
            writeDataIntoExcelSheetSummary(daySummaryEntryList, summaryDataSheet);
            //Write the workbook in file system
            FileOutputStream out = new FileOutputStream(filePath, false);
            workbook.write(out);
            out.close();
            drawChart(summaryDataSheet, filePath, workbook);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Draw the line chart.
     */
    public static void drawChart (XSSFSheet sheet, String filePath, XSSFWorkbook workbook)
                                                                                        throws FileNotFoundException {
        try {
            final int firstRowSheet = sheet.getFirstRowNum() + SECOND_ROW;
            final int lastRowSheet = sheet.getLastRowNum();
            final int firstColumnSheet = sheet.getRow(firstRowSheet).getFirstCellNum();
            final int lastColumnSheet = firstColumnSheet;
            final int firstColumnIncome = firstColumnSheet + SECOND_COLUMN;
            final int lastColumnIncome = firstColumnIncome;
            final int firstColumnOutcome = firstColumnSheet + THIRD_COLUMN;
            final int lastColumnOutcome = firstColumnOutcome;
            final int firstColumnNet = firstColumnSheet + FOURTH_COLUMN;
            final int lastColumnNet = firstColumnNet;
            final int widthChart = 20;
            final int heightChart = 30;

            if (!DirectoryPath.isValidFilePath(filePath)) {
                throw new ParseException(Messages.MESSAGE_UNREALISTIC_DIRECTORY);
            }

            Drawing drawing = sheet.createDrawingPatriarch();
            ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0,
                    firstColumnSheet + FOURTH_COLUMN + THIRD_COLUMN, firstRowSheet,
                    lastColumnSheet + widthChart, firstRowSheet + heightChart);

            Chart chart = drawing.createChart(anchor);
            ChartLegend legend = chart.getOrCreateLegend();
            legend.setPosition(LegendPosition.TOP_RIGHT);

            LineChartData data = chart.getChartDataFactory().createLineChartData();

            // Use a category axis for the bottom axis.
            ChartAxis bottomAxis = chart.getChartAxisFactory().createCategoryAxis(AxisPosition.BOTTOM);
            ValueAxis leftAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);
            leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

            ChartDataSource<String> xDate = DataSources.fromStringCellRange(
                    sheet, new CellRangeAddress(firstRowSheet, lastRowSheet, firstColumnSheet, lastColumnSheet));
            ChartDataSource<Number> yIncome = DataSources.fromNumericCellRange(
                    sheet, new CellRangeAddress(firstRowSheet, lastRowSheet, firstColumnIncome, lastColumnIncome));
            ChartDataSource<Number> yOutcome = DataSources.fromNumericCellRange(
                    sheet, new CellRangeAddress(firstRowSheet, lastRowSheet, firstColumnOutcome, lastColumnOutcome));
            ChartDataSource<Number> yNet = DataSources.fromNumericCellRange(
                    sheet, new CellRangeAddress(firstRowSheet, lastRowSheet, firstColumnNet, lastColumnNet));

            LineChartSeries series1 = data.addSeries(xDate, yIncome);
            series1.setTitle("Income");
            LineChartSeries series2 = data.addSeries(xDate, yOutcome);
            series2.setTitle("Outcome");
            LineChartSeries series3 = data.addSeries(xDate, yNet);
            series3.setTitle("Net");

            chart.plot(data, bottomAxis, leftAxis);

            FileOutputStream fileOut = new FileOutputStream(filePath);
            workbook.write(fileOut);
            fileOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Change the String of Money into appropriate format, as positive number won't have + sign, so we have to add it.
     * For positive number, the "+" will be discarded when you try to add money into Financial Planner --> error.
     */
    public static String checkMoneyString (String moneyString) {
        return (moneyString.charAt(STARTING_INDEX) == MINUS_SIGN_CHAR)
                || moneyString.charAt(STARTING_INDEX) == PLUS_SIGN_CHAR
                ? (moneyString) : (PLUS_SIGN_STRING + moneyString);
    }

    /**
     * Return string for each specific cell, as different method for different column of 1 row.
     */
    public static String retrieveDataFromOneRow (Row row, Cell cell, int columnIndex) {
        if (isStringCellType(cell) && cell != null) {
            return cell.getStringCellValue().trim();
        }
        if (isNumericCellType(cell) && cell != null && columnIndex == THIRD_COLUMN) {
            return Double.toString(cell.getNumericCellValue() * CHANGE_TO_DOUBLE);
        }
        return null;
    }

    /**
     * Create a records with data given.
     */
    private static Entry createRecord(String nameString, String dateString, String moneyString, String tagsString)
            throws ParseException {
        Name nameParse = ParserUtil.parseName(nameString);
        Date dateParse = ParserUtil.parseDate(dateString);
        CashFlow cashFlow = ParserUtil.parseCashFlow(moneyString);
        Set<Tag> tagList = new HashSet<>();
        if (tagsString != null) {
            String processedTags = tagsString.replace(TAG_SEPARATOR, WHITE_SPACE + PREFIX_TAG);
            System.out.println("TAG ADDED: " + PREFIX_TAG + processedTags);
            ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(
                    WHITE_SPACE + PREFIX_TAG + processedTags, PREFIX_TAG);
            tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));
        }
        return new Entry(nameParse, dateParse, cashFlow, tagList);
    }

    /**
     * Check if the Cell type is String.
     */
    private static Boolean isStringCellType(Cell cell) {
        return (cell.getCellTypeEnum() == CellType.STRING);
    }

    /**
     * Check if the Cell type is Numeric.
     */
    private static Boolean isNumericCellType(Cell cell) {
        return (cell.getCellTypeEnum() == CellType.NUMERIC);
    }

    //==========================================SUB METHOD FOR EXPORT EXCEL=============================================

    /**
     * Write Entry data into Excel Sheet.
     */
    private static void writeDataIntoExcelSheetRecord (List<Entry> entries, XSSFSheet sheet) {
        logger.info("----------------------------------------------------------START WRITE INTO EXCEL FILE");
        int rowNum = STARTING_INDEX;
        Row startingRow = sheet.createRow(rowNum);
        writeDataIntoCell(startingRow, FIRST_COLUMN, NAME_TITLE);
        writeDataIntoCell(startingRow, SECOND_COLUMN, DATE_TITLE);
        writeDataIntoCell(startingRow, THIRD_COLUMN, MONEY_TITLE);
        writeDataIntoCell(startingRow, FOURTH_COLUMN, TAG_TITLE);

        for (Entry entry : entries) {
            Row row = sheet.createRow(++rowNum);
            StringBuilder stringBuilder = new StringBuilder();
            writeDataIntoCell(row, FIRST_COLUMN, entry.getName().fullName);
            writeDataIntoCell(row, SECOND_COLUMN, entry.getDate().value);
            writeDataIntoCell(row, THIRD_COLUMN, entry.getCashFlow().valueDouble);
            if (entry.getTags().size() > RECORD_EMPTY) {
                for (Tag tag : entry.getTags()) {
                    stringBuilder.append(tag.tagName + TAG_SEPARATOR);
                }
                writeDataIntoCell(row, FOURTH_COLUMN, stringBuilder.toString()
                        .substring(STARTING_INDEX, stringBuilder.toString().length() - LEFT_OUT_CHARACTER));
                logger.info("---------------------Tag: " + stringBuilder.toString()
                        .substring(STARTING_INDEX, stringBuilder.toString().length() - LEFT_OUT_CHARACTER));
            }
        }
    }

    /**
     * Write Summary data into Excel Sheet.
     */
    private static void writeDataIntoExcelSheetSummary (List<SummaryEntry> daySummaryEntryList, XSSFSheet sheet) {
        int rowNum = STARTING_INDEX;
        Row startingRow = sheet.createRow(rowNum);
        writeDataIntoCell(startingRow, FIRST_COLUMN, DATE_TITLE);
        writeDataIntoCell(startingRow, SECOND_COLUMN, INCOME_TITLE);
        writeDataIntoCell(startingRow, THIRD_COLUMN, OUTCOME_TITLE);
        writeDataIntoCell(startingRow, FOURTH_COLUMN, TOTAL_MONEY);
        for (SummaryEntry summaryEntry : daySummaryEntryList) {
            Row row = sheet.createRow(++rowNum);
            writeDataIntoCell(row, FIRST_COLUMN,
                    summaryEntry.getIdentifier());
            writeDataIntoCell(row, SECOND_COLUMN,
                    Double.parseDouble(removeCurrencySign(summaryEntry.getTotalIncome())));
            writeDataIntoCell(row, THIRD_COLUMN,
                    Double.parseDouble(removeCurrencySign((summaryEntry.getTotalExpense()))));
            writeDataIntoCell(row, FOURTH_COLUMN,
                    Double.parseDouble(removeCurrencySign(summaryEntry.getTotal())));
        }
    }

    /**
     * Remove the character of $ in the String money retrieved.
     */
    private static String removeCurrencySign (String money) {
        String moneyString = null;
        if (money.contains(CURRENCY_STRING)) {
            for (int i = STARTING_INDEX; i < money.length(); i++) {
                if (money.charAt(i) == CURRENCY_CHAR) {
                    moneyString = (money.charAt(STARTING_INDEX) == MINUS_SIGN_CHAR)
                                ? (MINUS_SIGN_STRING + money.substring(++i))
                                : (PLUS_SIGN_STRING + money.substring(++i));
                }
            }
        } else {
            moneyString = (money.charAt(STARTING_INDEX) == MINUS_SIGN_CHAR)
                        ? (PLUS_SIGN_STRING + money)
                        : money;
        }
        return moneyString;
    }

    /**
     * Write data into cell.
     */
    private static void writeDataIntoCell (Row row, int colNum, Object object) {
        if (object instanceof String) {
            row.createCell(colNum).setCellValue((String) object);
        } else {
            row.createCell(colNum).setCellValue((Double) object);
        }
    }

    /**
     * Create the fileName path.
     */
    public static String setPathFile (String nameFile, String directoryPath) {
        String checkedNameFile;

        checkedNameFile = (nameFile.length() > 5
                && nameFile.substring(nameFile.length() - 5, nameFile.length()).equals(".xlsx"))
                ? nameFile : (nameFile + ".xlsx");
        logger.info("=----------------------------------------" + checkedNameFile);
        return directoryPath + (System.getProperty("file.separator") + checkedNameFile);
    }
    /**
     * Set the name for the Excel file based on type of inputs.
     */
    public static String setNameExcelFile (Date startDate, Date endDate) {
        return (startDate == null && endDate == null)
                ? "ENTRIES_ALL.xlsx"
                : ((startDate.equals(endDate))
                ? String.format("ENTRIES_%1$s.xlsx", startDate.getValue())
                : String.format("ENTRIES_%1$s_%2$s.xlsx", startDate.getValue(), endDate.getValue()));
    }
}
