package com.decduck3.tradecraft.db;

import com.decduck3.tradecraft.TradeCraft;
import com.decduck3.tradecraft.db.models.Listing;
import com.decduck3.tradecraft.db.models.User;
import com.decduck3.tradecraft.db.pojos.VirtualItemStackCodec;
import com.mongodb.ConnectionString;
import com.mongodb.client.*;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.Objects;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class DatabaseManager {
    private final MongoClient client;
    private final MongoDatabase database;

    // Collection references
    private final MongoCollection<User> users;
    private final MongoCollection<Listing> listings;

    public DatabaseManager() {
        String uri = TradeCraft.config().getString("db");
        if (uri == null) {
            throw new RuntimeException("Could not connect to database, config.yml 'db' is null");
        }

        // POJOs setup

        CodecRegistry pojoCodecRegistry = fromRegistries(
                CodecRegistries.fromCodecs(new VirtualItemStackCodec()),
                getDefaultCodecRegistry()
        );

        String databaseName = Objects.requireNonNullElse(new ConnectionString(uri).getDatabase(), "tradecraft");
        client = MongoClients.create(uri);
        database = client.getDatabase(databaseName).withCodecRegistry(pojoCodecRegistry);
        users = database.getCollection("users", User.class);
        listings = database.getCollection("listings", Listing.class);

        TradeCraft.logger().info("Connected to database");
    }

    public void close() {
        client.close();
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public MongoCollection<User> getUsers() {
        return users;
    }
}
