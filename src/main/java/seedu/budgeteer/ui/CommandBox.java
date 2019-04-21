package seedu.budgeteer.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;

import seedu.budgeteer.commons.core.LogsCenter;
import seedu.budgeteer.logic.commands.CommandResult;
import seedu.budgeteer.logic.commands.exceptions.CommandException;
import seedu.budgeteer.logic.parser.exceptions.ParseException;


/**
 * The UI component that is responsible for receiving user command inputs.
 */
public class CommandBox extends UiPart<Region> {

    public static final String ERROR_STYLE_CLASS = "error";
    private static final String FXML = "CommandBox.fxml";
    private static final String[] allSuggestions = {"add", "add n/ d/ c/ t/", "clear", "delete",
        "filter", "find n/ d/ c/ t/",
        "display", "display n/ or d/ or c/ or t/",
        "report", "report s/", "report e/", "report s/ e/",
        "report insight/",
        "find", "lock set/password",
        "find n/ or d/ or c/ or t/",
        "edit", "edit n/ d/ c/ t/", "help",
        "list", "select", "undo", "redo",
        "bitcoin", "ethereum", "litecoin", "stock n/, crypto n/",
        "invest interest/ years/"};
    private final Logger logger = LogsCenter.getLogger(CommandBox.class);
    private final CommandExecutor commandExecutor;
    private final List<String> history;
    private List<String> listedSuggestions = new ArrayList<>(Arrays.asList(allSuggestions));
    private ContextMenu suggestionsMenu = new ContextMenu();

    private ListElementPointer historySnapshot;

    @FXML
    private TextField commandTextField;

    public CommandBox(CommandExecutor commandExecutor, List<String> history) {
        super(FXML);
        this.commandExecutor = commandExecutor;
        this.history = history;
        createSuggestions();
        // calls #setStyleToDefault() whenever there is a change to the text of the command box.
        commandTextField.textProperty().addListener((unused1, unused2, unused3) -> setStyleToDefault());
        historySnapshot = new ListElementPointer(history);

    }

    /**
     * Handles the key press event, {@code keyEvent}.
     */
    @FXML
    private void handleKeyPress(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
        case UP:
            // As up and down buttons will alter the position of the caret,
            // consuming it causes the caret's position to remain unchanged
            keyEvent.consume();

            navigateToPreviousInput();
            break;
        case DOWN:
            keyEvent.consume();
            navigateToNextInput();
            break;
        case ESCAPE:
            keyEvent.consume();
            suggestionsMenu.hide();
            break;

        default:
            // let JavaFx handle the keypress
        }
    }

    /**
     * Updates the text field with the previous input in {@code historySnapshot},
     * if there exists a previous input in {@code historySnapshot}
     */
    private void navigateToPreviousInput() {
        assert historySnapshot != null;
        if (!historySnapshot.hasPrevious()) {
            return;
        }

        replaceText(historySnapshot.previous());
    }

    /**
     * Updates the text field with the next input in {@code historySnapshot},
     * if there exists a next input in {@code historySnapshot}
     */
    private void navigateToNextInput() {
        assert historySnapshot != null;
        if (!historySnapshot.hasNext()) {
            return;
        }

        replaceText(historySnapshot.next());
    }

    /**
     * Sets {@code CommandBox}'s text field with {@code text} and
     * positions the caret to the end of the {@code text}.
     */
    private void replaceText(String text) {
        commandTextField.setText(text);
        commandTextField.positionCaret(commandTextField.getText().length());
    }

    /**
     * Handles the Enter button pressed event.
     */
    @FXML
    private void handleCommandEntered() {
        try {
            commandExecutor.execute(commandTextField.getText());
            initHistory();
            historySnapshot.next();
            commandTextField.setText("");
        } catch (CommandException | ParseException e) {
            initHistory();
            setStyleToIndicateCommandFailure();
        }
    }

    /**
     * Initializes the history snapshot.
     */
    private void initHistory() {
        historySnapshot = new ListElementPointer(history);
        // add an empty string to represent the most-recent end of historySnapshot, to be shown to
        // the user if she tries to navigate past the most-recent end of the historySnapshot.
        historySnapshot.add("");
    }

    /**
     * Sets the command box style to use the default style.
     */
    private void setStyleToDefault() {
        commandTextField.getStyleClass().remove(ERROR_STYLE_CLASS);
    }

    /**
     * Sets the command box style to indicate a failed command.
     */
    private void setStyleToIndicateCommandFailure() {
        ObservableList<String> styleClass = commandTextField.getStyleClass();

        if (styleClass.contains(ERROR_STYLE_CLASS)) {
            return;
        }

        styleClass.add(ERROR_STYLE_CLASS);
    }

    /**
     * Represents a function that can execute commands.
     */
    @FunctionalInterface
    public interface CommandExecutor {
        /**
         * Executes the command and returns the result.
         *
         * @see seedu.budgeteer.logic.Logic#execute(String)
         */
        CommandResult execute(String commandText) throws CommandException, ParseException;
    }

    /**
     * Creates a list of Matched Suggestions based on User Input
     */
    public void createSuggestions() {
        commandTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            String userInput = commandTextField.getText();
            List<String> matchedSuggestions = listedSuggestions.stream()
                    .filter(i-> i.toLowerCase().startsWith(userInput.toLowerCase()))
                    .collect(Collectors.toList());

            if (matchedSuggestions.contains(userInput) || userInput.isEmpty() || matchedSuggestions.isEmpty()) {
                suggestionsMenu.hide();
            } else {
                createPopupWindow(matchedSuggestions);
                if (!suggestionsMenu.isShowing()) {
                    suggestionsMenu.show(this.commandTextField, Side.TOP, 200, 0); // Popup position.
                }
            }
        });
    }

    /**
     * Instantiates the Suggestions Popup Window based on the list {@code listedSuggestions}.
     *
     */

    private void createPopupWindow(List<String> matchedSuggestions) {
        List<CustomMenuItem> popupMenu = new ArrayList<>();
        suggestionsMenu.getItems().clear();
        for (int i = 0; i < matchedSuggestions.size(); i++) {
            Label suggestion = new Label(matchedSuggestions.get(i));
            suggestion.setPrefHeight(30);
            CustomMenuItem item = new CustomMenuItem(suggestion, true);
            popupMenu.add(item);

            item.setOnAction(actionEvent -> {
                logger.log(Level.INFO, suggestion.getText());
                suggestionsMenu.hide();
                commandTextField.setText(suggestion.getText());
                commandTextField.positionCaret(suggestion.getText().length());
            });
        }
        suggestionsMenu.getItems().addAll(popupMenu);
    }

}
