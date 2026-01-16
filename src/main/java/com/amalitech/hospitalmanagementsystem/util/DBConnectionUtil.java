
package com.amalitech.hospitalmanagementsystem.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

public final class DBConnectionUtil {
    private static final Logger log = LoggerFactory.getLogger(DBConnectionUtil.class);
    private static final HikariDataSource dataSource;

    static {
        Properties props = new Properties();
        try (InputStream in = DBConnectionUtil.class.getResourceAsStream("/db/db.properties"))  {
            if (in != null) {
                props.load(in);
            } else {
                log.warn("db/db.properties not found; relying entirely on environment variables");
            }

            //Read from ENV first; fall back to properties if ENV is missing
            String url = envOrProp("DB_URL", props.getProperty("db.url"));
            String user = envOrProp("DB_USER", props.getProperty("db.user"));
            String password = envOrProp("DB_PASSWORD", props.getProperty("db.password"));
            int poolSize = Integer.parseInt(Objects.requireNonNull(envOrProp("DB_POOL_SIZE", props.getProperty("db.pool.size", "10"))));

            if (url == null || user == null || password == null) {
                throw new IllegalStateException(
                        "Database credentials are not set. Provide DB_URL, DB_USER, DB_PASSWORD as environment variables " +
                                "or set db.url, db.user, db.password in db/db.properties (for local dev only).");
            }

            HikariConfig cfg = new HikariConfig();
            cfg.setJdbcUrl(url);
            cfg.setUsername(user);
            cfg.setPassword(password);
            cfg.setMaximumPoolSize(poolSize);
            cfg.setDriverClassName("org.postgresql.Driver");
            cfg.setPoolName("HMS-HikariPool");

            // PreparedStatement caching (OK for PostgreSQL with Hikari)
            cfg.addDataSourceProperty("cachePrepStmts", "true");
            cfg.addDataSourceProperty("prepStmtCacheSize", "250");
            cfg.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            dataSource = new HikariDataSource(cfg);
            log.info("DB pool initialised");
        } catch (Exception e) {
            log.error("Failed to init DB pool", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    private static String envOrProp(String envName, String propFallback) {
        String v = System.getenv(envName);
        return (v != null && !v.isBlank()) ? v : (propFallback != null && !propFallback.isBlank() ? propFallback : null);
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
