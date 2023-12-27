package com.decduck3.tradecraft.db.models;

import org.bson.types.ObjectId;
import org.bukkit.inventory.ItemStack;

public class Listing {
    private ObjectId id;

    // Single unit costing information
    private int centsPerUnit;
    private int available;

    // Bulk billing information
    private int[] bulkBreakpoints;
    private double[] bulkMultipliers;

    // Item information
    private ItemStack item;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
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

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }
}
