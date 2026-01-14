package com.pot.currency.exchange.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Currency {

    private Long id;
    private String code;
    @JsonProperty("name")
    private String fullName;
    private String sign;

    public Currency(Long id, String code, String fullName, String sign) {
        this.id = id;
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
    }

    public Currency(String code, String fullName, String sign) {
        new Currency(null, code, fullName, sign);
    }

    public Long id() {
        return id;
    }

    public String code() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String name() {
        return fullName;
    }

    public String sign() {
        return sign;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Currency currency = (Currency) o;
        return Objects.equals(id, currency.id) && Objects.equals(code, currency.code) && Objects.equals(fullName, currency.fullName) && Objects.equals(sign, currency.sign);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(code);
        result = 31 * result + Objects.hashCode(fullName);
        result = 31 * result + Objects.hashCode(sign);
        return result;
    }
}
