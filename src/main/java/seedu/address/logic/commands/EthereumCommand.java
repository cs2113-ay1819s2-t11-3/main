package seedu.address.logic.commands;

import seedu.address.logic.CommandHistory;
import seedu.address.model.Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Returns how many Ethereum you can buy at the current market price.
 */
public class EthereumCommand extends Command {

    public static final String COMMAND_WORD = "ethereum";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Displays how much ethereum you can buy.\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "You are able to buy .";

    @Override
    public CommandResult execute(Model model, CommandHistory history) {
        double price = 0.0;
        try {
            URL url = new URL("https://min-api.cryptocompare.com/data/pricemulti?fsyms=ETH&tsyms=USD");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            System.out.println("Output from Server .... \n");
            String output = br.readLine();
            System.out.println(output);
            //            while ((output = br.readLine()) != null) {
            //                System.out.println(output);
            //                temp = output;
            //            }
            System.out.println(output.substring(14, 21));
            price = Float.parseFloat(output.substring(14, 21));
            System.out.println(price);

            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        double roundOff = (double) Math.round(price * 100.0) / 100.0;

        // This is where you divide the cashflow by the price of ethereum, and add it to the message

        System.out.println("The current price of ethereum is $" + roundOff);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
