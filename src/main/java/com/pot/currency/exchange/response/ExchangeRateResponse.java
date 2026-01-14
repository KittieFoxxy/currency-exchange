package com.pot.currency.exchange.response;

import java.math.BigDecimal;

public record ExchangeRateResponse(
        Long id,
        CurrencyResponse baseCurrency,
        CurrencyResponse targetCurrency,
        BigDecimal rate
) {
}
