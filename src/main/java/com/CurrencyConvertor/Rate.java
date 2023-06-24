package com.CurrencyConvertor;

public class Rate {
    private String code;
    private String alphaCode;
    private String numericCode;
    private String name;
    private double rate;
    private String date;
    private double inverseRate;

    public String getCode() {
        return code;
    }

    public String getAlphaCode() {
        return alphaCode;
    }

    public String getNumericCode() {
        return numericCode;
    }

    public String getName() {
        return name;
    }

    public double getRate() {
        return rate;
    }

    public String getDate() {
        return date;
    }

    public double getInverseRate() {
        return inverseRate;
    }
    
    public void setCode(String code) {
        this.code = code;
    }

    public void setAlphaCode(String alphaCode) {
        this.alphaCode = alphaCode;
    }

    public void setNumericCode(String numericCode) {
        this.numericCode = numericCode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setInverseRate(double inverseRate) {
        this.inverseRate = inverseRate;
    }
}

