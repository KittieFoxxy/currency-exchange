package com.pot.currency.exchange;

import com.pot.currency.exchange.db.DataSourceFactory;
import com.pot.currency.exchange.repository.CurrencyRepository;
import com.pot.currency.exchange.repository.ExchangeRateRepository;
import com.pot.currency.exchange.repository.RepositoryConfig;
import com.pot.currency.exchange.service.ExchangeService;
import com.pot.currency.exchange.service.CurrencyService;
import com.pot.currency.exchange.service.ExchangeRateService;
import com.pot.currency.exchange.service.ServiceConfig;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import javax.sql.DataSource;

@WebListener
public class ApplicationContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        DataSource dataSource = DataSourceFactory.SqlLiteDataSource();
        CurrencyRepository cRepo = RepositoryConfig.currencyRepository(dataSource);
        ExchangeRateRepository erRepo = RepositoryConfig.exchangeRateRepository(dataSource);
        CurrencyService cService = ServiceConfig.currencyService(cRepo);
        sce.getServletContext().setAttribute(CurrencyService.class.getSimpleName(), cService);
        ExchangeRateService erService = ServiceConfig.exchangeRateService(erRepo);
        sce.getServletContext().setAttribute(ExchangeRateService.class.getSimpleName(), erService);
        ExchangeService cnvService = ServiceConfig.convertService(erRepo);
        sce.getServletContext().setAttribute(ExchangeService.class.getSimpleName(), cnvService);
    }
}
