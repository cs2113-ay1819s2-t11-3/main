package seedu.budgeteer.commons.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import seedu.budgeteer.commons.exceptions.IllegalValueException;

/**
 * Utility class to fetch the crypto-currencies prices from the internet using multithreading
 * and to store them for fast retrieval.
 * Once an interval of time has passed, the prices are deemed to be expired and will be fetched again.
 * This reduces the amount of lag due to fetching from the internet.
 */
public class CryptoUtil {

    /**
     * A Specialised thread class used for fetching the price of a particular crypto-currency type
     * To be used with multi-threading to improve performance for making multiple http calls concurrently
     */
    class CryptoThread extends Thread {
        private String cryptoType;

        public CryptoThread(String cryptoType) {
            this.cryptoType = cryptoType;
        }

        /**
         * Function to be run on the thread when thread.start(); is called
         * Fetches the crypto-price by using an API call via http request.
         */
        public void run() {

            try {

                if (this.cryptoType.equalsIgnoreCase("BTC")) {
                    btcPrice = fetchBtc();
                } else if (this.cryptoType.equalsIgnoreCase("ETH")) {
                    ethPrice = fetchEth();
                } else if (this.cryptoType.equalsIgnoreCase("LTC")) {
                    ltcPrice = fetchLtc();
                } else {
                    throw new IllegalValueException("Unknown Crypto");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static Double btcPrice = 0.0;
    private static Double ethPrice = 0.0;
    private static Double ltcPrice = 0.0;
    private static CryptoUtil cryptoUtilInstance = null;

    private Date lastUpdated;

    private CryptoUtil() {
        updateCryptoPrices();
    }

    public static CryptoUtil getInstance() {
        if (cryptoUtilInstance == null) {
            cryptoUtilInstance = new CryptoUtil();
        }

        return cryptoUtilInstance;
    }

    /**
     * Update internal prices by using multi-threading.
     * Uses join to ensure that all threads run to completion before exiting function.
     */
    private void updateCryptoPrices() {
        CryptoThread btcThread = new CryptoThread("BTC");
        CryptoThread ethThread = new CryptoThread("ETH");
        CryptoThread ltcThread = new CryptoThread("LTC");
        btcThread.start();
        ethThread.start();
        ltcThread.start();
        try {
            btcThread.join();
            ethThread.join();
            ltcThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        lastUpdated = new Date();
    }

    /**
     * Check the interval to determine if 15 minutes have passed or not.
     * If the interval has lapsed, update the price data.
     */
    private void checkIntervalAndUpdate() {
        Date currentTime = new Date();
        long diff = currentTime.getTime() - lastUpdated.getTime();
        long diffMinutes = diff / (60 * 1000);

        if (diffMinutes > 10 || btcPrice == 0 || ethPrice == 0 || ltcPrice == 0) {
            updateCryptoPrices();
        }

    }

    public double getBtc() {
        checkIntervalAndUpdate();
        return btcPrice;
    }

    public double getEth() {
        checkIntervalAndUpdate();
        return ethPrice;
    }

    public double getLtc() {
        checkIntervalAndUpdate();
        return ltcPrice;
    }

    private double fetchBtc() {
        return getCrypto("BTC");
    }

    private double fetchEth() {
        return getCrypto("ETH");
    }

    private double fetchLtc() {
        return getCrypto("LTC");
    }

    private double getCrypto(String cryptoType) {
        double price = 0.0;
        try {
            URL url = new URL("https://min-api.cryptocompare.com/data/pricemulti?fsyms="
                    + cryptoType + "&tsyms=SGD");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output = br.readLine();
            output = output.substring(14);
            output = output.substring(0, output.length() - 2);
            price = Float.parseFloat(output);

            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return price;

    }
}
