package com.pot.currency.exchange.servlet;

import com.pot.currency.exchange.entity.Currency;
import com.pot.currency.exchange.exception.CurrencyNotFoundException;
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

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {

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
            String pathInfo = req.getPathInfo();
            String code = pathInfo.replace("/", "").toUpperCase();
            ValidationUtil.validateCurrencyCode(code);
            Currency currency = currencyService.get(code);
            CurrencyResponse currencyResponse = mapper.toResponse(currency);

            ResponseUtil.sendResponse(resp, HttpServletResponse.SC_OK, currencyResponse);
        } catch (IllegalArgumentException e) {
            ResponseUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (CurrencyNotFoundException e) {
            ResponseUtil.sendError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            ResponseUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
