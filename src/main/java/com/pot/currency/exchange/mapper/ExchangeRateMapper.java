package com.pot.currency.exchange.mapper;

import com.pot.currency.exchange.entity.ExchangeRate;
import com.pot.currency.exchange.response.ExchangeRateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = CurrencyMapper.class)
public interface ExchangeRateMapper {

    ExchangeRateMapper INSTANCE = Mappers.getMapper(ExchangeRateMapper.class);

    ExchangeRateResponse toResponse(ExchangeRate exchangeRate);

    List<ExchangeRateResponse> toResponseList(List<ExchangeRate> currency);
}
