package com.pot.currency.exchange.repository;

import com.pot.currency.exchange.entity.ExchangeRate;

import java.math.BigDecimal;
import java.util.Optional;

public interface ExchangeRateRepository extends CrudRepository<ExchangeRate> {

    Optional<ExchangeRate> findByCodePair(String baseCode, String targetCode);

    Optional<ExchangeRate> updateRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate);

}
