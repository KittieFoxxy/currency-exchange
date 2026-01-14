package com.pot.currency.exchange.service;

import com.pot.currency.exchange.entity.ExchangeRate;

import java.math.BigDecimal;
import java.util.List;

public interface ExchangeRateService {

    ExchangeRate add(ExchangeRate currency);

    List<ExchangeRate> getAllRates();

    ExchangeRate get(String baseCurrencyCode, String targetCurrencyCode);

    ExchangeRate updateRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate);
}
