package com.decduck3.tradecraft.db.models;

import org.bson.types.ObjectId;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class Listing {
    private ObjectId id;
    // Visual stuff
    private String description;
    private List<Map<String, String>> features;

    // Single unit costing information
    private int centsPerUnit;
    private int available;

    // Bulk billing information
    private int[] bulkBreakpoints;
    private double[] bulkMultipliers;

    // Item information
    private List<ItemStack> items;
    // Vendor information
    private ObjectId vendorID;
    // Sale information
    private double saleMultipler;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Map<String, String>> getFeatures() {
        return features;
    }

    public void setFeatures(List<Map<String, String>> features) {
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

    public int[] getBulkBreakpoints() {
        return bulkBreakpoints;
    }

    public void setBulkBreakpoints(int[] bulkBreakpoints) {
        this.bulkBreakpoints = bulkBreakpoints;
    }

    public double[] getBulkMultipliers() {
        return bulkMultipliers;
    }

    public void setBulkMultipliers(double[] bulkMultipliers) {
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
