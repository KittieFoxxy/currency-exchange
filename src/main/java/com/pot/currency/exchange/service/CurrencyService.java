package com.pot.currency.exchange.service;

import com.pot.currency.exchange.entity.Currency;

import java.util.List;

public interface CurrencyService {

    Currency add(Currency currency);

    List<Currency> getAllCurrencies();

    Currency get(String code);

}
