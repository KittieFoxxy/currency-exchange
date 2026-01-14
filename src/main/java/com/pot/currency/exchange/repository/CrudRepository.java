package com.pot.currency.exchange.repository;

import java.util.List;
import java.util.Optional;

interface CrudRepository<T> {

    Optional<T> save(T entity);

    List<T> findAll();

}
