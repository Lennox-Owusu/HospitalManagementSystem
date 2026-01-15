
package com.amalitech.hospitalmanagementsystem.nosql;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public final class MongoConnectionUtil {
    private static volatile MongoClient client;
    private static final String URI = "mongodb://localhost:27017";
    private static final String DB  = "hms";

    private MongoConnectionUtil() {}

    public static MongoDatabase db() {
        if (client == null) {
            synchronized (MongoConnectionUtil.class) {
                if (client == null) {
                    MongoClientSettings settings = MongoClientSettings.builder()
                            .applyConnectionString(new ConnectionString(URI))
                            .applicationName("HMS-JavaFX")
                            .build();
                    client = MongoClients.create(settings);
                }
            }
        }
        return client.getDatabase(DB);
    }

    public static void shutdown() {
        if (client != null) client.close();
    }
}
