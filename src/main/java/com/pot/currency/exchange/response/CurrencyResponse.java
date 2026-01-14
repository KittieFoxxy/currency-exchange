package com.pot.currency.exchange.response;

public record CurrencyResponse(
        Long id,
        String code,
        String name,
        String sign
) {
}
