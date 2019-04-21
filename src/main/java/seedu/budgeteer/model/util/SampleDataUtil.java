package seedu.budgeteer.model.util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.budgeteer.model.EntriesBook;
import seedu.budgeteer.model.ReadOnlyEntriesBook;
import seedu.budgeteer.model.entry.CashFlow;
import seedu.budgeteer.model.entry.Date;
import seedu.budgeteer.model.entry.Entry;
import seedu.budgeteer.model.entry.Name;
import seedu.budgeteer.model.tag.Tag;

/**
 * Contains utility methods for populating {@code EntriesBook} with sample data.
 */
public class SampleDataUtil {
    public static Entry[] getSampleEntrys() {
        return new Entry[]{
            new Entry(new Name("Salary from tuition"), new Date("10-03-2019"), CashFlow.getCashFlow("+500"),
                    getTagSet("work")),
            new Entry(new Name("Lunch with Bernice"), new Date("12-03-2019"), CashFlow.getCashFlow("-12.9"),
                    getTagSet("colleagues", "friends")),
            new Entry(new Name("Netflix Subscription"), new Date("13-03-2019"), CashFlow.getCashFlow("-10"),
                    getTagSet("personal")),
            new Entry(new Name("Bursary Award"), new Date("14-03-2019"), CashFlow.getCashFlow("+4000"),
                    getTagSet("school", "award")),
            new Entry(new Name("Scholarship prize"), new Date("14-03-2019"), CashFlow.getCashFlow("+100"),
                    getTagSet("school", "award")),
            new Entry(new Name("Dinner with Roy Balakrishnan"), new Date("17-03-2019"), CashFlow.getCashFlow("-20.90"),
                    getTagSet("colleagues")),
            new Entry(new Name("Salary from Part-time job"), new Date("20-03-2019"), CashFlow.getCashFlow("+1200"),
                    getTagSet("work")),
            new Entry(new Name("Dinner with Bernice"), new Date("31-03-2019"), CashFlow.getCashFlow("-16.70"),
                    getTagSet("colleagues", "friends")),
            new Entry(new Name("Shopping for clothes"), new Date("04-04-2019"), CashFlow.getCashFlow("-79.9"),
                    getTagSet("shopping", "personal")),
            new Entry(new Name("Lottery Prize"), new Date("07-04-2019"), CashFlow.getCashFlow("+4000"),
                    getTagSet("personal")),
            new Entry(new Name("Salary from tuition"), new Date("10-04-2019"), CashFlow.getCashFlow("+500"),
                    getTagSet("work")),
            new Entry(new Name("Birthday Celebration for Peter"), new Date("16-04-2019"), CashFlow.getCashFlow("-80"),
                    getTagSet("colleagues"))
        };
    }

    public static ReadOnlyEntriesBook getSampleAddressBook() {
        EntriesBook sampleAb = new EntriesBook();
        for (Entry sampleEntry : getSampleEntrys()) {
            sampleAb.addEntry(sampleEntry);
        }
        return sampleAb;
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        return Arrays.stream(strings)
                .map(Tag::new)
                .collect(Collectors.toSet());
    }

}
