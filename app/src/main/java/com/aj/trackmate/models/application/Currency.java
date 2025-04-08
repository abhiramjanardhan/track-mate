package com.aj.trackmate.models.application;

public enum Currency {
    INR("INR"),
    USD("USD"),
    CAD("CAD"),
    EUR("EUR"),
    GBP("GBP");

    private String currency;

    Currency(String currency) {
        this.currency = currency;
    }

    public String getCurrency() {
        return currency;
    }

    public static Currency fromCurrency(String platformCurrency) {
        for (Currency currency : values()) {
            if (currency.getCurrency().equalsIgnoreCase(platformCurrency)) {
                return currency;
            }
        }
        throw new IllegalArgumentException("No enum constant with currency: " + platformCurrency);
    }
}
