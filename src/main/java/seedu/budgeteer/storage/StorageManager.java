package seedu.budgeteer.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.budgeteer.commons.core.LogsCenter;
import seedu.budgeteer.commons.exceptions.DataConversionException;
import seedu.budgeteer.model.ReadOnlyEntriesBook;
import seedu.budgeteer.model.ReadOnlyUserPrefs;
import seedu.budgeteer.model.UserPrefs;

/**
 * Manages storage of EntriesBook data in local storage.
 */
public class StorageManager implements Storage {

    private static final Logger logger = LogsCenter.getLogger(StorageManager.class);
    private BudgeteerStorage budgeteerStorage;
    private UserPrefsStorage userPrefsStorage;


    public StorageManager(BudgeteerStorage budgeteerStorage, UserPrefsStorage userPrefsStorage) {
        super();
        this.budgeteerStorage = budgeteerStorage;
        this.userPrefsStorage = userPrefsStorage;
    }

    // ================ UserPrefs methods ==============================

    @Override
    public Path getUserPrefsFilePath() {
        return userPrefsStorage.getUserPrefsFilePath();
    }

    @Override
    public Optional<UserPrefs> readUserPrefs() throws DataConversionException, IOException {
        return userPrefsStorage.readUserPrefs();
    }

    @Override
    public void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException {
        userPrefsStorage.saveUserPrefs(userPrefs);
    }


    // ================ EntriesBook methods ==============================

    @Override
    public Path getAddressBookFilePath() {
        return budgeteerStorage.getAddressBookFilePath();
    }

    @Override
    public Optional<ReadOnlyEntriesBook> readEntriesBook() throws DataConversionException, IOException {
        return readEntriesBook(budgeteerStorage.getAddressBookFilePath());
    }

    @Override
    public Optional<ReadOnlyEntriesBook> readEntriesBook(Path filePath) throws DataConversionException, IOException {
        logger.fine("Attempting to read data from file: " + filePath);
        return budgeteerStorage.readEntriesBook(filePath);
    }

    @Override
    public void saveAddressBook(ReadOnlyEntriesBook addressBook) throws IOException {
        saveAddressBook(addressBook, budgeteerStorage.getAddressBookFilePath());
    }

    @Override
    public void saveAddressBook(ReadOnlyEntriesBook addressBook, Path filePath) throws IOException {
        logger.fine("Attempting to write to data file: " + filePath);
        budgeteerStorage.saveAddressBook(addressBook, filePath);
    }

}
