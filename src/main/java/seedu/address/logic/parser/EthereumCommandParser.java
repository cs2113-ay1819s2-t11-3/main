package seedu.address.logic.parser;

import seedu.address.logic.commands.EthereumCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.entry.Date;
import seedu.address.model.entry.DateAfterGivenPredicate;
import seedu.address.model.entry.DateBeforeGivenPredicate;

import java.util.function.Predicate;
import java.util.stream.Stream;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ENDDATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STARTDATE;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_ENTRYS;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class EthereumCommandParser implements Parser<EthereumCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the ReportCommand
     * and returns an ReportCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public EthereumCommand parse(String args) throws ParseException {
        Date startDate = null;
        Date endDate = null;
        Predicate afterStartPredicate = null;
        Predicate beforeEndPredicate = null;
        Predicate finalPredicate = null;

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_STARTDATE, PREFIX_ENDDATE);

        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EthereumCommand.MESSAGE_USAGE));
        }


        if(arePrefixesPresent(argMultimap, PREFIX_STARTDATE)){
            //Get Entries from this date onwards
            //TODO
            startDate = ParserUtil.parseDate(argMultimap.getValue(PREFIX_STARTDATE).get());
            afterStartPredicate = new DateAfterGivenPredicate(startDate);

        }
        if(arePrefixesPresent(argMultimap, PREFIX_ENDDATE)){
            //Get Entries until this date
            //TODO
            endDate = ParserUtil.parseDate(argMultimap.getValue(PREFIX_ENDDATE).get());
            beforeEndPredicate = new DateBeforeGivenPredicate(endDate);
        }

        if (endDate != null && startDate != null && startDate.isAfter(endDate))

            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EthereumCommand.MESSAGE_USAGE));

        else if (endDate != null && startDate != null && !startDate.isAfter(endDate))

            finalPredicate = afterStartPredicate.and(beforeEndPredicate);

        else if (endDate == null && startDate == null)

            finalPredicate = PREDICATE_SHOW_ALL_ENTRYS;

        else
            finalPredicate = (afterStartPredicate == null) ? beforeEndPredicate : afterStartPredicate;


        return new EthereumCommand(finalPredicate);
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
//    FOR REFERENCE:
//    /**
//     * Parses the given {@code String} of arguments in the context of the AddCommand
//     * and returns an AddCommand object for execution.
//     * @throws ParseException if the user input does not conform the expected format
//     */
//    public AddCommand parse(String args) throws ParseException {
//        ArgumentMultimap argMultimap =
//                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_DATE, PREFIX_CASHFLOW, PREFIX_TAG);
//
//        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_DATE, PREFIX_CASHFLOW)
//                || !argMultimap.getPreamble().isEmpty()) {
//            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
//        }
//
//        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
//        Date date = ParserUtil.parseDate(argMultimap.getValue(PREFIX_DATE).get());
//        CashFlow cashFlow = ParserUtil.parseCashFlow(argMultimap.getValue(PREFIX_CASHFLOW).get());
//        Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));
//
//        Entry person = new Entry(name, date, cashFlow, tagList);
//
//        return new AddCommand(person);
//    }
//
//    /**
//     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
//     * {@code ArgumentMultimap}.
//     */
//    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
//        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
//    }

}