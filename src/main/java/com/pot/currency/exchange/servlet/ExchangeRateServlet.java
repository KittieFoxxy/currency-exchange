package com.pot.currency.exchange.servlet;

import com.pot.currency.exchange.entity.ExchangeRate;
import com.pot.currency.exchange.exception.ExchangeRateNotFoundException;
import com.pot.currency.exchange.mapper.ExchangeRateMapper;
import com.pot.currency.exchange.response.ExchangeRateResponse;
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

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    private ExchangeRateService exchangeRateService;
    private final ExchangeRateMapper mapper = ExchangeRateMapper.INSTANCE;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext context = config.getServletContext();
        exchangeRateService = (ExchangeRateService) context.getAttribute(ExchangeRateService.class.getSimpleName());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String pathInfo = req.getPathInfo();
            String codePair = pathInfo.replace("/", "").toUpperCase();
            ValidationUtil.validateCodePair(codePair);
            String baseCurrencyCode = codePair.substring(0, 3);
            String targetCurrencyCode = codePair.substring(3);

            ExchangeRate rate = exchangeRateService.get(baseCurrencyCode, targetCurrencyCode);
            ExchangeRateResponse exchangeRateResponse = mapper.toResponse(rate);

            ResponseUtil.sendResponse(resp, 200, exchangeRateResponse);
        } catch (IllegalArgumentException e) {
            ResponseUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (ExchangeRateNotFoundException e) {
            ResponseUtil.sendError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            ResponseUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String pathInfo = req.getPathInfo();
            String codePair = pathInfo.replace("/", "").toUpperCase();
            ValidationUtil.validateCodePair(codePair);
            String baseCurrencyCode = codePair.substring(0, 3);
            String targetCurrencyCode = codePair.substring(3);

            String rateParam = req.getParameter("rate");
            BigDecimal rate = new BigDecimal(rateParam).setScale(6, RoundingMode.HALF_EVEN);
            ValidationUtil.validateDecimal(rate);

            ExchangeRate updatedRate = exchangeRateService.updateRate(baseCurrencyCode, targetCurrencyCode, rate);
            ExchangeRateResponse exchangeRateResponse = mapper.toResponse(updatedRate);

            ResponseUtil.sendResponse(resp, 200, exchangeRateResponse);
        } catch (IllegalArgumentException e) {
            ResponseUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (ExchangeRateNotFoundException e) {
            ResponseUtil.sendError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            ResponseUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
