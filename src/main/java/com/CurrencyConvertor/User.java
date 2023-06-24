package com.CurrencyConvertor;

import java.util.Map;

public class User {
    private String name;
    private Map<String, Double> wallet;

    // Default constructor, required for deserialization
    public User() {
    }

    public User(String name, Map<String, Double> wallet) {
        this.name = name;
        this.wallet = wallet;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Double> getWallet() {
        return this.wallet;
    }

    public void setWallet(Map<String, Double> wallet) {
        this.wallet = wallet;
    }
}

