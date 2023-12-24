package com.decduck3.tradecraft.db;

import com.decduck3.tradecraft.TradeCraft;
import com.decduck3.tradecraft.db.models.User;
import com.mongodb.ConnectionString;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.Objects;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class DatabaseManager {
    private final MongoClient client;
    private final MongoDatabase database;

    // Collection references
    private final MongoCollection<User> users;

    public DatabaseManager() {
        String uri = TradeCraft.config().getString("db");
        if(uri == null){
            throw new RuntimeException("Could not connect to database, config.yml 'db' is null");
        }

        // POJOs setup
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));

        String databaseName = Objects.requireNonNullElse(new ConnectionString(uri).getDatabase(), "tradecraft");
        client = MongoClients.create(uri);
        database = client.getDatabase(databaseName).withCodecRegistry(pojoCodecRegistry);
        users = database.getCollection("users", User.class);

        TradeCraft.logger().info("Connected to database");
    }

    public void close(){
        client.close();
    }

    public MongoDatabase getDatabase(){
        return database;
    }

    public MongoCollection<User> getUsers(){
        return users;
    }
}
