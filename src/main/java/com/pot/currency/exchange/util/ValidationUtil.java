package com.pot.currency.exchange.util;

import java.math.BigDecimal;

public class ValidationUtil {

    public static void validateParams(String... params) {
        for (String param : params) {
            if (param == null || param.isBlank()) {
                String message = "Please fill all the required fields.";
                throw new IllegalArgumentException(message);
            }
        }
    }

    public static void validateCurrencyCode(String code) throws IllegalArgumentException {
        if (code.length() != 3) {
            String message = "Code must be 3 characters long.";
            throw new IllegalArgumentException(message);
        }
    }

    public static void validateDecimal(BigDecimal rate) throws IllegalArgumentException {
        if (rate.compareTo(BigDecimal.ZERO) <= 0) {
            String message = "Exchange rate must be greater than 0.";
            throw new IllegalArgumentException(message);
        }
    }

    public static void validateCodePair(String codePair) {
        if (codePair.length() != 6) {
            throw new IllegalArgumentException("Code pair must be 6 characters long.");
        }
    }

    public static void validateCurrencySign(String sign) {
        if (sign.length() > 3) {
            String message = "Sign can not be greater 3 characters long.";
            throw new IllegalArgumentException(message);
        }
    }

    public static void validateCurrencyName(String name) {
        if (name.length() > 15) {
            String message = "Name is very long.";
            throw new IllegalArgumentException(message);
        }
    }
}
