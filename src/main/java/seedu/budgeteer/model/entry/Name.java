package seedu.budgeteer.model.entry;

import static java.util.Objects.requireNonNull;
import static seedu.budgeteer.commons.util.AppUtil.checkArgument;

/**
 * Represents a Entry's name in the budgeteer book.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class Name {


    /*
     * The first character of the budgeteer must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    //TODO: May need to change the constraints
    public static final String VALIDATION_REGEX = "[\\p{Alnum}][\\p{Alnum} ]*";
    public static final String MESSAGE_CONSTRAINTS = "Names can only contain alphanumeric characters and spaces, "
            + "and it should not be blank";

    public final String fullName;

    /**
     * Constructs a {@code Name}.
     *
     * @param name A valid name.
     */
    public Name(String name) {
        requireNonNull(name);
        checkArgument(isValidName(name), MESSAGE_CONSTRAINTS);
        fullName = name;
    }

    /**
     * Returns true if a given string is a valid name.
     */
    public static boolean isValidName(String test) {
        return test.matches(VALIDATION_REGEX);
    }


    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Name // instanceof handles nulls
                && fullName.equals(((Name) other).fullName)); // state check
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

}
