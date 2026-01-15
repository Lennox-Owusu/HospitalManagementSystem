
package com.amalitech.hospitalmanagementsystem.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public final class DataSourceProvider {
    private static HikariDataSource ds;

    private DataSourceProvider() {}

    public static synchronized DataSource get() {
        if (ds == null) {
            HikariConfig cfg = new HikariConfig();
            cfg.setJdbcUrl(System.getProperty("db.url", "jdbc:postgresql://localhost:5432/hospital_db"));
            cfg.setUsername(System.getProperty("db.user", "postgres"));
            cfg.setPassword(System.getProperty("db.pass", "postgres"));
            cfg.setMaximumPoolSize(10);
            cfg.setDriverClassName("org.postgresql.Driver");
            ds = new HikariDataSource(cfg);
        }
        return ds;
    }
}

