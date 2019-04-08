package seedu.budgeteer.logic.parser;

import static seedu.budgeteer.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.budgeteer.logic.commands.FilterCommand;
import seedu.budgeteer.logic.parser.exceptions.ParseException;
import seedu.budgeteer.model.entry.CashFlowContainsSpecifiedKeywordsPredicate;
import seedu.budgeteer.model.entry.DateContainsSpecifiedKeywordsPredicate;
import seedu.budgeteer.model.entry.NameContainsKeywordsPredicate;
import seedu.budgeteer.model.entry.TagContainsSpecifiedKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FilterCommandParser implements Parser<FilterCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the FilterCommand
     * and returns an FilterCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public FilterCommand parse(String args) throws ParseException {

        String trimmedArgs = args.trim();

        /**
         * Used for initial separation of prefix and args.
         */

        final Pattern prefixFormat = Pattern.compile("(?<prefix>\\w/)(?<arguments>.*)");

        final Matcher matcher = prefixFormat.matcher(trimmedArgs);
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
        }

        final String prefix = matcher.group("prefix");
        final String arguments = matcher.group("arguments");

        String[] keyWords = arguments.split("\\s+");

        if (prefix.equals("n/")) {
            return new FilterCommand(new NameContainsKeywordsPredicate(Arrays.asList(keyWords)));
        }
        else if (prefix.equals("d/")) {
            return new FilterCommand(new DateContainsSpecifiedKeywordsPredicate(Arrays.asList(keyWords)));
        }
        else if (prefix.equals("c/")) {
            return new FilterCommand(new CashFlowContainsSpecifiedKeywordsPredicate(Arrays.asList(keyWords)));
        }
        else if (prefix.equals("t/")) {
            return new FilterCommand(new TagContainsSpecifiedKeywordsPredicate(Arrays.asList(keyWords)));
        }
        else {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
        }
    }

}