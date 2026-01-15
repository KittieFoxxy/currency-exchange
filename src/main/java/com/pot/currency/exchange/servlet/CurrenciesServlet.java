package com.pot.currency.exchange.servlet;

import com.pot.currency.exchange.entity.Currency;
import com.pot.currency.exchange.exception.CurrencyAlreadyExistException;
import com.pot.currency.exchange.mapper.CurrencyMapper;
import com.pot.currency.exchange.response.CurrencyResponse;
import com.pot.currency.exchange.service.CurrencyService;
import com.pot.currency.exchange.util.ResponseUtil;
import com.pot.currency.exchange.util.ValidationUtil;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private CurrencyService currencyService;
    private final CurrencyMapper mapper = CurrencyMapper.INSTANCE;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext context = config.getServletContext();
        currencyService = (CurrencyService) context.getAttribute(CurrencyService.class.getSimpleName());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            List<Currency> currencies = currencyService.getAllCurrencies();
            List<CurrencyResponse> currenciesResponse = mapper.toResponseList(currencies);
            ResponseUtil.sendResponse(resp, HttpServletResponse.SC_OK, currenciesResponse);
        } catch (Exception e) {
            String message = "Internal Server Error";
            ResponseUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String name = req.getParameter("name");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");
        try {
            ValidationUtil.validateParams(name, code, sign);
            ValidationUtil.validateCurrencyName(name);
            ValidationUtil.validateCurrencyCode(code);
            ValidationUtil.validateCurrencySign(sign);
            Currency newCurrency = new Currency(code, name, sign);
            Currency response = currencyService.add(newCurrency);
            CurrencyResponse currencyResponse = mapper.toResponse(response);
            ResponseUtil.sendResponse(resp, HttpServletResponse.SC_CREATED, currencyResponse);
        } catch (IllegalArgumentException e) {
            ResponseUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (CurrencyAlreadyExistException e) {
            ResponseUtil.sendError(resp, HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (Exception e) {
            String message = "Internal error occurred.";
            ResponseUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
        }
    }
}
