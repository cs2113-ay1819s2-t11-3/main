//@@author ngkaicong
package seedu.budgeteer.logic.parser;

import static seedu.budgeteer.commons.util.DateUtil.isEarlierThan;
import static seedu.budgeteer.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.budgeteer.logic.parser.CliSyntax.PREFIX_DIR;

import java.util.Arrays;
import java.util.stream.Stream;

import seedu.budgeteer.commons.core.Messages;
import seedu.budgeteer.logic.commands.ExportExcelCommand;
import seedu.budgeteer.logic.parser.exceptions.ParseException;
import seedu.budgeteer.model.entry.Date;





/**
 * Parses input arguments and create ExportExcelCommand object.
 */
public class ExportExcelCommandParser implements Parser<ExportExcelCommand> {
    private static String whiteSpace = " ";

    /**
     * Parses the given code {@code String} of arguments in the context of the ExportExcelCommand
     * @return an ExportExcelCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format.
     */
    public ExportExcelCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            return new ExportExcelCommand();
        }
        return createExportExcelCommand(args);
    }

    /**
     * Create Export Excel Command using args.
     */
    private static ExportExcelCommand createExportExcelCommand (String args) throws ParseException {
        String stringDate = whiteSpace;
        String stringPath = whiteSpace;
        boolean isDateExist = true;
        boolean isPathExist = true;
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_DATE, PREFIX_DIR);
        if (!arePrefixesPresent(argMultimap, PREFIX_DATE) || !argMultimap.getPreamble().isEmpty()) {
            isDateExist = false;
        }
        if (!arePrefixesPresent(argMultimap, PREFIX_DIR) || !argMultimap.getPreamble().isEmpty()) {
            isPathExist = false;
        }
        if (!isDateExist && !isPathExist) {
            throw new ParseException(
                    String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ExportExcelCommand.MESSAGE_USAGE));
        }
        if (isDateExist) {
            stringDate = argMultimap.getValue(PREFIX_DATE).get();
        }
        if (isPathExist) {
            stringPath = argMultimap.getValue(PREFIX_DIR).get();
        }
        if (stringDate.trim().isEmpty() && stringPath.trim().isEmpty()) {
            return new ExportExcelCommand();
        }
        return parseArgumentsModeIntoCommand(stringDate.trim(), stringPath.trim());
    }
    /**
     * Parse the arguments into different argument mode, hence, we will have different command mode.
     */
    private static ExportExcelCommand parseArgumentsModeIntoCommand (String stringDate, String stringPath)
            throws ParseException {
        String directoryPath;
        if (stringDate.isEmpty()) {
            directoryPath = ParserUtil.parseDirectoryString(stringPath);
            return new ExportExcelCommand(directoryPath);
        } else {
            String[] dates = splitByWhitespace(stringDate);
            return parseDateIntoDifferentMode(dates, stringPath);
        }
    }

    /**
     * Parse the string Date into different mode, hence return different commands.
     */
    private static ExportExcelCommand parseDateIntoDifferentMode (String[] dates, String stringPath)
            throws ParseException {
        Date startDate;
        Date endDate;
        String directoryPath;
        int dateNum = Arrays.asList(dates).size();

        if (dateNum > ExportExcelCommand.DUO_MODE) {
            throw new ParseException(
                    String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, Messages.MESSAGE_INVALID_DATE_REQUIRED));
        } else if (dateNum == ExportExcelCommand.SINGLE_MODE) {
            startDate = ParserUtil.parseDate(Arrays.asList(dates).get(ExportExcelCommand.FIRST_ELEMENT).trim());
            endDate = ParserUtil.parseDate(Arrays.asList(dates).get(ExportExcelCommand.FIRST_ELEMENT).trim());
        } else {
            startDate = ParserUtil.parseDate(Arrays.asList(dates).get(ExportExcelCommand.FIRST_ELEMENT).trim());
            endDate = ParserUtil.parseDate(Arrays.asList(dates).get(ExportExcelCommand.SECOND_ELEMENT).trim());
        }
        if (isDateOrderValid(startDate, endDate)) {
            if (stringPath == null || stringPath.isEmpty()) {
                return new ExportExcelCommand(startDate, endDate);
            } else {
                directoryPath = ParserUtil.parseDirectoryString(stringPath);
                return new ExportExcelCommand(startDate, endDate, directoryPath);
            }
        } else {
            throw new ParseException(
                    String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, Messages.MESSAGE_INVALID_STARTDATE_ENDDATE));
        }
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

    /**
     * Splits a string using whitespace as delimiters
     * @param args String arguments that have 2 dates.
     * @return array of split strings
     */
    private static String[] splitByWhitespace(String args) {
        if (args.isEmpty()) {
            return null;
        }
        return args.split("\\s+");
    }

    /**
     * Check whether the Dates are valid period or not.
     */
    private static boolean isDateOrderValid(Date startDate, Date endDate) {
        return isEarlierThan(startDate, endDate) || startDate.equals(endDate);
    }
}
