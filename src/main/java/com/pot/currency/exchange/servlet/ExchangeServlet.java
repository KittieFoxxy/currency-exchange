package com.pot.currency.exchange.servlet;

import com.pot.currency.exchange.exception.ExchangeRateNotFoundException;
import com.pot.currency.exchange.response.ConvertResponse;
import com.pot.currency.exchange.service.ExchangeService;
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

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {

    private ExchangeService exchangeService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext context = config.getServletContext();
        exchangeService = (ExchangeService) context.getAttribute(ExchangeService.class.getSimpleName());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String amountParam = req.getParameter("amount");

        try {
            ValidationUtil.validateParams(from, to, amountParam);
            ValidationUtil.validateCurrencyCode(from);
            ValidationUtil.validateCurrencyCode(to);
            BigDecimal amount = new BigDecimal(amountParam);
            ValidationUtil.validateDecimal(amount);

            ConvertResponse result = exchangeService.exchange(
                    from.toUpperCase(),
                    to.toUpperCase(),
                    amount
            );

            ResponseUtil.sendResponse(resp, HttpServletResponse.SC_OK, result);

        } catch (NumberFormatException e) {
            String message = "Amount must be a number.";
            ResponseUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, message);
        } catch (IllegalArgumentException e) {
            ResponseUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (ExchangeRateNotFoundException e) {
            ResponseUtil.sendError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            ResponseUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal error during exchange");
        }
    }
}
