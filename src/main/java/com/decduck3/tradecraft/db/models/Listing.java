package com.decduck3.tradecraft.db.models;

import org.bson.types.ObjectId;

public class Listing {
    private ObjectId id;

    // Single unit costing information
    private int centsPerUnit;
    private int available;

    // Bulk billing information
    private int[] bulkBreakpoints;
    private double[] bulkMultipliers;

    // Item information
    private String itemID;
    private String itemTexturePath;
}
