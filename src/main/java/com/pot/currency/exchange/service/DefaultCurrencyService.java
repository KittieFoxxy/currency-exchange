package com.pot.currency.exchange.service;

import com.pot.currency.exchange.entity.Currency;
import com.pot.currency.exchange.exception.CurrencyNotFoundException;
import com.pot.currency.exchange.repository.CurrencyRepository;

import java.util.List;

class DefaultCurrencyService implements CurrencyService {

    private final CurrencyRepository repository;

    public DefaultCurrencyService(CurrencyRepository repository) {
        this.repository = repository;
    }

    @Override
    public Currency add(Currency currency) {
        return repository.save(currency)
                .orElseThrow(() -> new RuntimeException("Failed to retrieve saved currency from database"));
    }

    @Override
    public List<Currency> getAllCurrencies() {
        return repository.findAll();
    }

    @Override
    public Currency get(String code) {
        return repository.findByCode(code)
                .orElseThrow(() -> new CurrencyNotFoundException("Currency with code %s not found".formatted(code)));
    }
}
