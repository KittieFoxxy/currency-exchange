package com.pot.currency.exchange.service;

import com.pot.currency.exchange.entity.ExchangeRate;
import com.pot.currency.exchange.exception.ExchangeRateNotFoundException;
import com.pot.currency.exchange.repository.ExchangeRateRepository;

import java.math.BigDecimal;
import java.util.List;

class DefaultExchangeRateService implements ExchangeRateService {

    private final ExchangeRateRepository repository;

    DefaultExchangeRateService(ExchangeRateRepository repository) {
        this.repository = repository;
    }

    @Override
    public ExchangeRate add(ExchangeRate exchangeRate) {
        return repository.save(exchangeRate)
                .orElseThrow(() -> new RuntimeException("Failed to retrieve saved exchange rate from database"));
    }

    @Override
    public List<ExchangeRate> getAllRates() {
        return repository.findAll();
    }

    @Override
    public ExchangeRate get(String baseCurrencyCode, String targetCurrencyCode) {
        return repository.findByCodePair(baseCurrencyCode, targetCurrencyCode)
                .orElseThrow(() -> new ExchangeRateNotFoundException("Currency with code %s-%s not found".formatted(baseCurrencyCode, targetCurrencyCode)));
    }

    @Override
    public ExchangeRate updateRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        return repository.updateRate(baseCurrencyCode, targetCurrencyCode, rate)
                .orElseThrow(() -> new ExchangeRateNotFoundException("Exchange rate for pair " + baseCurrencyCode + "/" + targetCurrencyCode + " not found"));
    }
}
