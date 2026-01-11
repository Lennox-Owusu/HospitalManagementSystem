
package com.amalitech.hospitalmanagementsystem.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public final class DBConnectionUtil {
    private static final Logger log = LoggerFactory.getLogger(DBConnectionUtil.class);
    private static HikariDataSource dataSource;

    static {
        try (InputStream in = DBConnectionUtil.class.getResourceAsStream("/db/db.properties"))  {
            if (in == null) {
                throw new IllegalStateException("Missing db/db.properties on classpath");
            }
            Properties props = new Properties();
            props.load(in);

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");
            int poolSize = Integer.parseInt(props.getProperty("db.pool.size", "10"));

            HikariConfig cfg = new HikariConfig();
            cfg.setJdbcUrl(url);
            cfg.setUsername(user);
            cfg.setPassword(password);
            cfg.setMaximumPoolSize(poolSize);
            cfg.setDriverClassName("org.postgresql.Driver");
            cfg.setPoolName("HMS-HikariPool");

            // Prepared statement caching
            cfg.addDataSourceProperty("cachePrepStmts", "true");
            cfg.addDataSourceProperty("prepStmtCacheSize", "250");
            cfg.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            dataSource = new HikariDataSource(cfg);
            log.info("DB pool initialised for {}", url);
        } catch (Exception e) {
            log.error("Failed to init DB pool", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    private DBConnectionUtil() {}

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void shutdown() {
        if (dataSource != null) {
            log.info("Shutting down datasource");
            dataSource.close();
        }
    }
}
