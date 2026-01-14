package com.pot.currency.exchange.servlet;

import com.pot.currency.exchange.entity.Currency;
import com.pot.currency.exchange.entity.ExchangeRate;
import com.pot.currency.exchange.exception.CurrencyNotFoundException;
import com.pot.currency.exchange.exception.ExchangeRateAlreadyExistException;
import com.pot.currency.exchange.mapper.ExchangeRateMapper;
import com.pot.currency.exchange.response.ExchangeRateResponse;
import com.pot.currency.exchange.service.CurrencyService;
import com.pot.currency.exchange.service.ExchangeRateService;
import com.pot.currency.exchange.util.ResponseUtil;
import com.pot.currency.exchange.util.ValidationUtil;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    private ExchangeRateService exchangeRateService;
    private CurrencyService currencyService;
    private final ExchangeRateMapper mapper = ExchangeRateMapper.INSTANCE;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext context = config.getServletContext();
        exchangeRateService = (ExchangeRateService) context.getAttribute(ExchangeRateService.class.getSimpleName());
        currencyService = (CurrencyService) context.getAttribute(CurrencyService.class.getSimpleName());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            List<ExchangeRate> rates = exchangeRateService.getAllRates();
            List<ExchangeRateResponse> responseRates = mapper.toResponseList(rates);
            ResponseUtil.sendResponse(resp, HttpServletResponse.SC_OK, responseRates);
        } catch (Exception e) {
            String message = "Internal Server Error";
            ResponseUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rateParam = req.getParameter("rate");

        try {
            ValidationUtil.validateParams(baseCurrencyCode, targetCurrencyCode, rateParam);
            ValidationUtil.validateCurrencyCode(baseCurrencyCode);
            ValidationUtil.validateCurrencyCode(targetCurrencyCode);
            BigDecimal rate = new BigDecimal(rateParam).setScale(6, RoundingMode.HALF_EVEN);
            ValidationUtil.validateDecimal(rate);

            Currency baseCurrency = currencyService.get(baseCurrencyCode);
            Currency targetCurrency = currencyService.get(targetCurrencyCode);
            ExchangeRate newExchangeRate = new ExchangeRate(baseCurrency, targetCurrency, rate);
            ExchangeRate exchangeRate = exchangeRateService.add(newExchangeRate);
            ExchangeRateResponse exchangeRateResponse = mapper.toResponse(exchangeRate);

            ResponseUtil.sendResponse(resp, HttpServletResponse.SC_CREATED, exchangeRateResponse);
        } catch (NumberFormatException e) {
            String message = "Exchange rate must be a number.";
            ResponseUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, message);
        } catch (IllegalArgumentException e) {
            ResponseUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (CurrencyNotFoundException e) {
            ResponseUtil.sendError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (ExchangeRateAlreadyExistException e) {
            ResponseUtil.sendError(resp, HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (Exception e) {
            String message = "Internal error occurred.";
            ResponseUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
        }
    }
}
