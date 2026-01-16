
package com.amalitech.hospitalmanagementsystem.nosql;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public final class MongoConnectionUtil {

    private static volatile MongoClient client;

    // Environment-first (safer) → fallback to defaults for dev only
    private static final String URI =
            System.getenv("MONGO_URI") != null
                    ? System.getenv("MONGO_URI")
                    : "mongodb://localhost:27017";

    private static final String DB =
            System.getenv("MONGO_DB") != null
                    ? System.getenv("MONGO_DB")
                    : "hms"; // default name you used

    private MongoConnectionUtil() {}

    public static MongoDatabase db() {
        if (client == null) {
            synchronized (MongoConnectionUtil.class) {
                if (client == null) {
                    try {
                        MongoClientSettings settings = MongoClientSettings.builder()
                                .applyConnectionString(new ConnectionString(URI))
                                .applicationName("HMS-JavaFX")
                                .applyToConnectionPoolSettings(pool -> {
                                    pool.maxSize(20);
                                    pool.minSize(5);
                                })
                                .build();

                        client = MongoClients.create(settings);

                    } catch (MongoException e) {
                        throw new RuntimeException("Failed to connect to MongoDB: " + e.getMessage(), e);
                    }
                }
            }
        }
        return client.getDatabase(DB);
    }

    public static void shutdown() {
        if (client != null) {
            client.close();
        }
    }
}
