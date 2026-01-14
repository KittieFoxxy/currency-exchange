package com.pot.currency.exchange.repository;

import com.pot.currency.exchange.entity.Currency;
import com.pot.currency.exchange.exception.CurrencyAlreadyExistException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class JdbcCurrencyRepository implements CurrencyRepository {

    private static final String SAVE_CURRENCY_SQL = "INSERT INTO currencies (code, full_name, sign) VALUES (?, ?, ?)";
    private static final String FIND_ALL_CURRENCY_SQL = "SELECT id, full_name, code, sign FROM currencies";
    private static final String FIND_CURRENCY_BY_CODE_SQL = "SELECT id, full_name, code, sign FROM currencies WHERE code = ?";

    private final DataSource dataSource;

    JdbcCurrencyRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Currency> save(Currency currency) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(SAVE_CURRENCY_SQL, Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setString(1, currency.code().toUpperCase());
            statement.setString(2, currency.name());
            statement.setString(3, currency.sign());
            statement.executeUpdate();

            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    Long id = resultSet.getLong(1);
                    return Optional.of(new Currency(id, currency.code(), currency.name(), currency.sign()));
                }
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 19 || e.getMessage().contains("UNIQUE constraint failed")) {
                throw new CurrencyAlreadyExistException("Currency already exists.");
            }
            throw new RuntimeException("Database error: ", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Currency> findAll() {
        List<Currency> currencies = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(FIND_ALL_CURRENCY_SQL)
        ) {
            while (resultSet.next()) {
                currencies.add(new Currency(
                        resultSet.getLong("id"),
                        resultSet.getString("code"),
                        resultSet.getString("full_name"),
                        resultSet.getString("sign"))
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error: ", e);
        }
        return currencies;
    }

    @Override
    public Optional<Currency> findByCode(String code) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(FIND_CURRENCY_BY_CODE_SQL)) {
            statement.setString(1, code.toUpperCase());
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Currency(
                            rs.getLong("id"),
                            rs.getString("full_name"),
                            rs.getString("code"),
                            rs.getString("sign")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error: ", e);
        }

        return Optional.empty();
    }
}
