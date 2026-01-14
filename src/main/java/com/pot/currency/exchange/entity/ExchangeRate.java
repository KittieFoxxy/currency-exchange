package com.pot.currency.exchange.entity;

import java.math.BigDecimal;
import java.util.Objects;

public class ExchangeRate {

    private Long id;
    private Currency base;
    private Currency target;
    private BigDecimal rate;

    public ExchangeRate(Long id, Currency base, Currency target, BigDecimal rate) {
        this.id = id;
        this.base = base;
        this.target = target;
        this.rate = rate;
    }

    public ExchangeRate(Currency base, Currency target, BigDecimal rate) {
        new ExchangeRate(null, base, target, rate);
    }

    public Long id() {
        return id;
    }

    public Currency baseCurrency() {
        return base;
    }

    public Currency targetCurrency() {
        return target;
    }

    public BigDecimal rate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        ExchangeRate that = (ExchangeRate) o;
        return Objects.equals(id, that.id) && Objects.equals(base, that.base) && Objects.equals(target, that.target) && Objects.equals(rate, that.rate);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(base);
        result = 31 * result + Objects.hashCode(target);
        result = 31 * result + Objects.hashCode(rate);
        return result;
    }
}
