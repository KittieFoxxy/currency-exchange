package com.pot.currency.exchange.service;

import com.pot.currency.exchange.response.ConvertResponse;

import java.math.BigDecimal;

public interface ExchangeService {

    ConvertResponse exchange(String from, String to, BigDecimal amount);
}
