package com.decduck3.tradecraft.db;

import com.decduck3.tradecraft.TradeCraft;
import com.decduck3.tradecraft.db.models.Listing;
import com.decduck3.tradecraft.db.models.User;
import com.decduck3.tradecraft.db.models.Vendor;
import com.decduck3.tradecraft.db.models.VirtualInventoryBack;
import com.decduck3.tradecraft.db.pojos.ItemStackCodec;
import com.mongodb.ConnectionString;
import com.mongodb.client.*;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class DatabaseManager {
    private final MongoClient client;
    private final MongoDatabase database;

    // Collection references
    private final MongoCollection<User> users;
    private final MongoCollection<Listing> listings;
    private final MongoCollection<Vendor> vendors;
    private final MongoCollection<VirtualInventoryBack> virtualInventories;

    public DatabaseManager() {
        String uri = TradeCraft.config().getString("db");
        if (uri == null) {
            throw new RuntimeException("Could not connect to database, config.yml 'db' is null");
        }

        // POJOs setup
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoCodecRegistry = fromRegistries(
                CodecRegistries.fromCodecs(new ItemStackCodec()),
                getDefaultCodecRegistry(),
                fromProviders(pojoCodecProvider)
        );

        String databaseName = Objects.requireNonNullElse(new ConnectionString(uri).getDatabase(), "tradecraft");
        client = MongoClients.create(uri);
        database = client.getDatabase(databaseName).withCodecRegistry(pojoCodecRegistry);

        users = database.getCollection("users", User.class);
        listings = database.getCollection("listings", Listing.class);
        virtualInventories = database.getCollection("vinv", VirtualInventoryBack.class);
        vendors = database.getCollection("vendors", Vendor.class);

        Vendor v = new Vendor();
        ObjectId id = Objects.requireNonNull(vendors.insertOne(v).getInsertedId()).asObjectId().getValue();



        /*
        // Debug Listing
        Listing listing = new Listing();
        listing.setCreatedAt(new Date());

        // Display
        listing.setDescription("The absolute sexiest Raw Chicken you've ever seen. Check out the fucking awesome promotional pictures we took for this product, I can't believe it. I just want to eat it so so bad.");
        listing.setFeatures(Map.of("Features", List.of(
                "Flexibility. The chicken is raw! Unlimited possibilities.",
                "Sexy. This chicken is very sexy."
        )));
        listing.setPictureTransforms(List.of("skewY(12deg)", "skewY(-12deg)", "rotate(34deg)"));

        // Costing
        listing.setCentsPerUnit(1799);
        listing.setAvailable(53);

        // Bulk billing
        // If they buy at least 10, they get 90% of the original price
        // If they buy at least 15, they get 85% of the original price
        // etc etc
        listing.setBulkBreakpoints(List.of(10, 15, 20));
        listing.setBulkMultipliers(List.of(0.9, 0.85, 0.8));

        // Items
        listing.setItems(List.of(new ItemStack(Material.ANVIL)));
        listing.setVendorID(id);
        listing.setSaleMultipler(1); // No sale

        listings.insertOne(listing);
        */

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

    public MongoCollection<Listing> getListings() {
        return listings;
    }

    public MongoCollection<VirtualInventoryBack> getVirtualInventories() {
        return virtualInventories;
    }

    public MongoCollection<Vendor> getVendors() {
        return vendors;
    }
}
