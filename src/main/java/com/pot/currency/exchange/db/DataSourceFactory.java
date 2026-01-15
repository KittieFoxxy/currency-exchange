package com.pot.currency.exchange.db;

import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class DataSourceFactory {

    public static DataSource SqlLiteDataSource() {
        URL resource = DataSourceFactory.class.getClassLoader().getResource("currency.db");
        String path = null;
        if (resource != null) {
            try {
                path = new File(resource.toURI()).getAbsolutePath();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        SQLiteDataSource sqLiteDataSource = new SQLiteDataSource();
//        sqLiteDataSource.setUrl("jdbc:sqlite:%s".formatted(path));
        sqLiteDataSource.setUrl("jdbc:sqlite:/usr/local/tomcat/currency.db");
        return sqLiteDataSource;
    }
}
