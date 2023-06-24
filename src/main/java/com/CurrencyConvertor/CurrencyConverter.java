package com.CurrencyConvertor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.nio.file.Paths;
import java.util.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class CurrencyConverter {
    private static final String USERS_JSON_PATH = "src/main/java/com/CurrencyConvertor/users.json";
    private static final String RATES_JSON_PATH = "src/main/java/com/CurrencyConvertor/fx_rates.json";
    private static final String TRANSACTIONS_PATH = "src/main/java/com/CurrencyConvertor/transactions.txt";
    private static final String USD = "usd";

    private Logger logger = LogManager.getLogger(CurrencyConverter.class);

    public static void main(String[] args) {
        CurrencyConverter converter = new CurrencyConverter();
        converter.run(args);
    }

    public void run(String[] args) {
        List<User> users = loadJsonFile(USERS_JSON_PATH, new TypeReference<List<User>>() {});
        Map<String, User> userMap = generateUserMap(users);
        Map<String, Rate> rateMap = loadRates();
        rateMap = addUsdRate(rateMap);
        processTransactions(userMap, rateMap);
        updateJsonFile(USERS_JSON_PATH, users);
    }

    public Map<String, User> generateUserMap(List<User> users) {
        Map<String, User> userMap = new HashMap<>();
        for (User user : users) {
            userMap.put(user.getName(), user);
        }
        return userMap;
    }

    public Map<String, Rate> loadRates() {
        return loadJsonFile(RATES_JSON_PATH, new TypeReference<Map<String, Rate>>() {});
    }

    public Map<String, Rate> addUsdRate(Map<String, Rate> rateMap) {
        Rate usdRate = new Rate();
        usdRate.setRate(1);
        usdRate.setCode(USD);
        usdRate.setAlphaCode(USD);
        usdRate.setNumericCode("840");
        usdRate.setName("United States Dollar");
        rateMap.put(USD, usdRate);
        return rateMap;
    }

    public String readFile(String filePath) {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            return scanner.useDelimiter("\\Z").next();
        } catch (IOException e) {
            logger.error("Failed to read file: " + filePath, e);
            return null;
        }
    }

    public void writeFile(String filePath, String content) {
        try {
            Files.write(Paths.get(filePath), content.getBytes());
        } catch (IOException e) {
            logger.error("Failed to write to file: " + filePath, e);
        }
    }

    public void processTransactions(Map<String, User> userMap, Map<String, Rate> rateMap) {
        String transactions = readFile(TRANSACTIONS_PATH);
        if (transactions == null) return;
        for (String line : transactions.split("\n")) {
            processTransactionLine(line, userMap, rateMap);
        }
    }

    public void processTransactionLine(String line, Map<String, User> userMap, Map<String, Rate> rateMap) {
        String[] parts = line.split(" ");
        if (parts.length != 4) {
            logger.warn("Invalid transaction line: " + line);
            return;
        }

        String userName = parts[0];
        User user = userMap.get(userName);
        if (user == null) {
            logger.warn("Unknown user: " + userName);
            return;
        }

        processTransactionForUser(user, parts, rateMap);
    }

    private void processTransactionForUser(User user, String[] parts, Map<String, Rate> rateMap) {
        String fromCurrency = parts[1].toLowerCase();
        double fromAmount;
        try {
            fromAmount = Double.parseDouble(parts[3]);
        } catch (NumberFormatException e) {
            logger.warn("Invalid amount: " + parts[3]);
            return;
        }
        if (user.getWallet().get(fromCurrency) == null || user.getWallet().get(fromCurrency) < fromAmount) {
            logger.warn("Insufficient funds: " + user.getName() + " " + fromCurrency);
            return;
        }

        exchangeCurrency(user, fromCurrency, fromAmount, parts[2].toLowerCase(), rateMap);
    }

    private void exchangeCurrency(User user, String fromCurrency, double fromAmount, String toCurrency, Map<String, Rate> rateMap) {
        Rate rate = rateMap.get(toCurrency);
        if (rate == null) {
            logger.warn("Invalid currency: " + toCurrency);
            return;
        }

        if (fromCurrency.equals(toCurrency)) {
            logger.info("Same currency, skipping transaction for user: " + user.getName());
            return;
        }

        double toAmount = fromAmount * rate.getRate();
        user.getWallet().put(fromCurrency, user.getWallet().get(fromCurrency) - fromAmount);
        user.getWallet().put(toCurrency, user.getWallet().getOrDefault(toCurrency, 0.0) + toAmount);

        logger.info("Processed transaction for user: " + user.getName());
    }

    public void updateJsonFile(String fileName, List<User> users) {
        try {
            String json = new ObjectMapper().writeValueAsString(users);
            writeFile(fileName, json);
        } catch (IOException e) {
            logger.error("Failed to write JSON to file: " + fileName, e);
        }
    }

    public <T> T loadJsonFile(String fileName, TypeReference<T> type) {
        try {
            return new ObjectMapper().readValue(Paths.get(fileName).toFile(), type);
        } catch (IOException e) {
            logger.error("Failed to read JSON from file: " + fileName, e);
            return null;
        }
    }
}









