package com.pot.currency.exchange.repository;

import javax.sql.DataSource;

public class RepositoryConfig {

    public static CurrencyRepository currencyRepository(DataSource dataSource) {
        return new JdbcCurrencyRepository(dataSource);
    }

    public static ExchangeRateRepository exchangeRateRepository(DataSource dataSource) {
        return new JdbcExchangeRateRepository(dataSource);
    }
}
