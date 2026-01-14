package com.pot.currency.exchange.response;

import com.pot.currency.exchange.entity.Currency;

import java.math.BigDecimal;

public record ConvertResponse(
        Currency baseCurrency,
        Currency targetCurrency,
        BigDecimal rate,
        BigDecimal amount,
        BigDecimal convertedAmount
) {
}
