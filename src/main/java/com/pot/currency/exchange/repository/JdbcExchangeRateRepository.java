package com.pot.currency.exchange.repository;

import com.pot.currency.exchange.entity.Currency;
import com.pot.currency.exchange.entity.ExchangeRate;
import com.pot.currency.exchange.exception.ExchangeRateAlreadyExistException;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class JdbcExchangeRateRepository implements ExchangeRateRepository {

    private static final String SAVE_SQL = "INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) VALUES (?, ?, ?)";
    private static final String FIND_ALL_SQL = """
            SELECT
                er.id AS id,
                er.rate AS rate,
                bc.id AS bc_id, bc.code AS bc_code, bc.full_name AS bc_name, bc.sign AS bc_sign,
                tc.id AS tc_id, tc.code AS tc_code, tc.full_name AS tc_name, tc.sign AS tc_sign
            FROM exchange_rates er
            JOIN currencies bc ON er.base_currency_id = bc.id
            JOIN currencies tc ON er.target_currency_id = tc.id
            """;
    private static final String FIND_BY_CODE_PAIR_SQL = """
                    SELECT
                        er.id AS id,
                        er.rate AS rate,
                        bc.id AS bc_id, bc.code AS bc_code, bc.full_name AS bc_name, bc.sign AS bc_sign,
                        tc.id AS tc_id, tc.code AS tc_code, tc.full_name AS tc_name, tc.sign AS tc_sign
                    FROM exchange_rates er
                    JOIN currencies bc ON er.base_currency_id = bc.id
                    JOIN currencies tc ON er.target_currency_id = tc.id
                    WHERE bc.code = ? AND tc.code = ?
            """;

    private static final String UPDATE_RATE_SQL = "UPDATE exchange_rates SET rate = ? WHERE id = ?";

    private final DataSource dataSource;

    JdbcExchangeRateRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<ExchangeRate> save(ExchangeRate exchangeRate) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setLong(1, exchangeRate.baseCurrency().id());
            statement.setLong(2, exchangeRate.targetCurrency().id());
            statement.setBigDecimal(3, exchangeRate.rate());
            statement.executeUpdate();

            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    Long id = resultSet.getLong(1);
                    return Optional.of(new ExchangeRate(id, exchangeRate.baseCurrency(), exchangeRate.targetCurrency(), exchangeRate.rate()));
                }
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 19 || e.getMessage().contains("UNIQUE constraint failed")) {
                throw new ExchangeRateAlreadyExistException("ExchangeRate already exists.");
            }
            throw new RuntimeException("Database error: ", e);
        }
        return Optional.empty();
    }

    @Override
    public List<ExchangeRate> findAll() {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(FIND_ALL_SQL)) {
            while (resultSet.next()) {
                exchangeRates.add(mapResultSetToExchangeRate(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
        return exchangeRates;
    }

    @Override
    public Optional<ExchangeRate> findByCodePair(String baseCode, String targetCode) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(FIND_BY_CODE_PAIR_SQL)) {
            statement.setString(1, baseCode.toUpperCase());
            statement.setString(2, targetCode.toUpperCase());
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToExchangeRate(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<ExchangeRate> updateRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        Optional<ExchangeRate> exchangeRate = findByCodePair(baseCurrencyCode, targetCurrencyCode);

        if (exchangeRate.isEmpty()) {
            return Optional.empty();
        }
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(UPDATE_RATE_SQL)
        ) {
            statement.setBigDecimal(1, rate);
            statement.setLong(2, exchangeRate.get().id());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                return Optional.empty();
            }
            ExchangeRate current = exchangeRate.get();
            return Optional.of(new ExchangeRate(
                    current.id(),
                    current.baseCurrency(),
                    current.targetCurrency(),
                    rate
            ));
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }

    private ExchangeRate mapResultSetToExchangeRate(ResultSet rs) throws SQLException {
        Currency base = new Currency(
                rs.getLong("bc_id"),
                rs.getString("bc_code"),
                rs.getString("bc_name"),
                rs.getString("bc_sign")
        );

        Currency target = new Currency(
                rs.getLong("tc_id"),
                rs.getString("tc_code"),
                rs.getString("tc_name"),
                rs.getString("tc_sign")
        );

        return new ExchangeRate(
                rs.getLong("id"),
                base,
                target,
                rs.getBigDecimal("rate")
        );
    }
}
