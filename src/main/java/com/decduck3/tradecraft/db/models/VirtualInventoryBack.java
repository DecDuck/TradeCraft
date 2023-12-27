package com.decduck3.tradecraft.db.models;

import com.decduck3.tradecraft.inventory.VirtualItemStack;
import org.bson.types.ObjectId;

import java.util.List;

public class VirtualInventoryBack {
    private ObjectId id;
    private String playerUUID;
    private List<VirtualItemStack> armor;
    private List<VirtualItemStack> inventory;
    private List<VirtualItemStack> extra;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public List<VirtualItemStack> getArmor() {
        return armor;
    }

    public void setArmor(List<VirtualItemStack> armor) {
        this.armor = armor;
    }

    public List<VirtualItemStack> getInventory() {
        return inventory;
    }

    public void setInventory(List<VirtualItemStack> inventory) {
        this.inventory = inventory;
    }

    public List<VirtualItemStack> getExtra() {
        return extra;
    }

    public void setExtra(List<VirtualItemStack> extra) {
        this.extra = extra;
    }

    public String getPlayerUUID() {
        return playerUUID;
    }

    public void setPlayerUUID(String playerUUID) {
        this.playerUUID = playerUUID;
    }
}
