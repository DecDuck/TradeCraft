package com.decduck3.tradecraft.db.models;

import com.decduck3.tradecraft.TradeCraft;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;

public class Listing {
    private ObjectId id;
    // Meta
    private String title;
    private Date createdAt;
    // Visual stuff
    private String description;
    private Map<String, List<String>> features;
    private List<String> pictureTransforms;

    // Single unit costing information
    private int centsPerUnit;
    private int available;

    // Bulk billing information
    private List<Integer> bulkBreakpoints;
    private List<Double> bulkMultipliers;

    // Item information
    private List<ItemStack> items;
    // Vendor information
    private ObjectId vendorID;
    // Sale information
    private double saleMultipler;

    public static Listing findListing(String objectID){
        List<Listing> listings = new ArrayList<>();
        TradeCraft.database().getListings().find(eq("_id", new ObjectId(objectID))).into(listings);
        if(listings.size() != 1){
            return null;
        }
        return listings.get(0);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getPictureTransforms() {
        return pictureTransforms;
    }

    public void setPictureTransforms(List<String> pictureTransforms) {
        this.pictureTransforms = pictureTransforms;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, List<String>> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, List<String>> features) {
        this.features = features;
    }

    public int getCentsPerUnit() {
        return centsPerUnit;
    }

    public void setCentsPerUnit(int centsPerUnit) {
        this.centsPerUnit = centsPerUnit;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public List<Integer> getBulkBreakpoints() {
        return bulkBreakpoints;
    }

    public void setBulkBreakpoints(List<Integer> bulkBreakpoints) {
        this.bulkBreakpoints = bulkBreakpoints;
    }

    public List<Double> getBulkMultipliers() {
        return bulkMultipliers;
    }

    public void setBulkMultipliers(List<Double> bulkMultipliers) {
        this.bulkMultipliers = bulkMultipliers;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public void setItems(List<ItemStack> items) {
        this.items = items;
    }

    public ObjectId getVendorID() {
        return vendorID;
    }

    public void setVendorID(ObjectId vendorID) {
        this.vendorID = vendorID;
    }

    public double getSaleMultipler() {
        return saleMultipler;
    }

    public void setSaleMultipler(double saleMultipler) {
        this.saleMultipler = saleMultipler;
    }
}
