package com.pot.currency.exchange.service;

import com.pot.currency.exchange.entity.ExchangeRate;
import com.pot.currency.exchange.exception.ExchangeRateNotFoundException;
import com.pot.currency.exchange.repository.ExchangeRateRepository;
import com.pot.currency.exchange.response.ConvertResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

class DefaultExchangeService implements ExchangeService {

    private final ExchangeRateRepository repository;

    DefaultExchangeService(ExchangeRateRepository repository) {
        this.repository = repository;
    }

    @Override
    public ConvertResponse exchange(String from, String to, BigDecimal amount) {
        ExchangeRate rate = findExchangeRate(from, to)
                .orElseThrow(() -> new ExchangeRateNotFoundException("Exchange rate not found for " + from + "-" + to));
        BigDecimal convertedAmount = amount.multiply(rate.getRate()).setScale(2, RoundingMode.HALF_EVEN);
        return new ConvertResponse(
                rate.getBaseCurrency(),
                rate.getTargetCurrency(),
                rate.getRate(),
                amount,
                convertedAmount
        );
    }

    private Optional<ExchangeRate> findExchangeRate(String from, String to) {
        // Прямой курс
        Optional<ExchangeRate> direct = repository.findByCodePair(from, to);
        if (direct.isPresent()) return direct;

        // Обратный курс
        Optional<ExchangeRate> reverse = repository.findByCodePair(to, from);
        if (reverse.isPresent()) {
            BigDecimal rate = BigDecimal.ONE.divide(reverse.get().getRate(), 6, RoundingMode.HALF_EVEN);
            return Optional.of(new ExchangeRate(null, reverse.get().getTargetCurrency(), reverse.get().getBaseCurrency(), rate));
        }

        // Кросс-курс через USD
        return findCrossRate(from, to);
    }

    private Optional<ExchangeRate> findCrossRate(String from, String to) {
        Optional<ExchangeRate> usdToBase = repository.findByCodePair("USD", from);
        Optional<ExchangeRate> usdToTarget = repository.findByCodePair("USD", to);

        if (usdToBase.isPresent() && usdToTarget.isPresent()) {
            BigDecimal rate = usdToTarget.get().getRate().divide(usdToBase.get().getRate(), 6, RoundingMode.HALF_EVEN);
            return Optional.of(new ExchangeRate(usdToBase.get().getTargetCurrency(), usdToTarget.get().getTargetCurrency(), rate));
        }
        return Optional.empty();
    }
}
