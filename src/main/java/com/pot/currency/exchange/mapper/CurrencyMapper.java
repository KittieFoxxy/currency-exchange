package com.pot.currency.exchange.mapper;

import com.pot.currency.exchange.entity.Currency;
import com.pot.currency.exchange.response.CurrencyResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CurrencyMapper {

    CurrencyMapper INSTANCE = Mappers.getMapper(CurrencyMapper.class);

    @Mapping(target = "name", source = "fullName")
    CurrencyResponse toResponse(Currency currency);

    @Mapping(target = "name", source = "fullName")
    List<CurrencyResponse> toResponseList(List<Currency> currency);

}
