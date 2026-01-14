package com.pot.currency.exchange.service;

import com.pot.currency.exchange.repository.CurrencyRepository;
import com.pot.currency.exchange.repository.ExchangeRateRepository;

public class ServiceConfig {

    public static CurrencyService currencyService(CurrencyRepository repository) {
        return new DefaultCurrencyService(repository);
    }

    public static ExchangeRateService exchangeRateService(ExchangeRateRepository repository) {
        return new DefaultExchangeRateService(repository);
    }

    public static ExchangeService convertService(ExchangeRateRepository repository) {
        return new DefaultExchangeService(repository);
    }
}
