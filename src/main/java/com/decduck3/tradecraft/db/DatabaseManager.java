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



        // Debug Listing
        /*
        Vendor v = new Vendor();
        ObjectId id = Objects.requireNonNull(vendors.insertOne(v).getInsertedId()).asObjectId().getValue();

        Listing listing = new Listing();
        listing.setCreatedAt(new Date());
        listing.setTitle("Diamond Sword Bundle");

        // Display
        listing.setDescription("The quickest way to get a DIY Diamond Sword! Features everything you need to craft your own (Crafting Table not supplied).");
        listing.setFeatures(Map.of("Features", List.of(
                "Sticks! Includes a stick for the handle, so you can get a hold on that mob problem.",
                "Diamonds. Diamonds are the very toughest material, so it lasts for a long time and doesn't get away from you."
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
        listing.setItems(List.of(new ItemStack(Material.DIAMOND, 2), new ItemStack(Material.STICK, 1)));
        listing.setVendorID(id);
        listing.setSaleMultipler(0.9); // No sale

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
