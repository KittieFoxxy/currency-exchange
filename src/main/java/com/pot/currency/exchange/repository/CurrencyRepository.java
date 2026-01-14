package com.pot.currency.exchange.repository;

import com.pot.currency.exchange.entity.Currency;

import java.util.Optional;

public interface CurrencyRepository extends CrudRepository<Currency> {

    Optional<Currency> findByCode(String code);
}
