package com.decduck3.tradecraft.db.models;

import com.decduck3.tradecraft.TradeCraft;
import org.bson.types.ObjectId;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class VirtualInventoryBack {
    private ObjectId id;
    private String playerUUID;
    private List<ItemStack> armor = new ArrayList<>();
    private List<ItemStack> inventory = new ArrayList<>();
    private List<ItemStack> extra = new ArrayList<>();


    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public List<ItemStack> getArmor() {
        return armor;
    }

    public void setArmor(List<ItemStack> armor) {
        this.armor = armor;
    }

    public List<ItemStack> getInventory() {
        return inventory;
    }

    public void setInventory(List<ItemStack> inventory) {
        this.inventory = inventory;
    }

    public List<ItemStack> getExtra() {
        return extra;
    }

    public void setExtra(List<ItemStack> extra) {
        this.extra = extra;
    }

    public String getPlayerUUID() {
        return playerUUID;
    }

    public void setPlayerUUID(String playerUUID) {
        this.playerUUID = playerUUID;
    }

    public static VirtualInventoryBack findOrInitialiseBack(Player player) {
        List<VirtualInventoryBack> foundBacks = new ArrayList<>();
        TradeCraft.database().getVirtualInventories().find(eq("playerUUID", player.getUniqueId().toString())).into(foundBacks);
        if (foundBacks.size() > 1) {
            // Should not be possible
            // We return the first one and log the error
            TradeCraft.logger().severe("DATABASE ISSUE: multiple backs with the same playerUUID. Manually reconcile.");
            return foundBacks.get(0);
        }
        if (foundBacks.size() == 1) {
            return foundBacks.get(0);
        }

        // Otherwise, create a new user
        VirtualInventoryBack back = new VirtualInventoryBack();
        back.setPlayerUUID(player.getUniqueId().toString());
        TradeCraft.database().getVirtualInventories().insertOne(back);
        return back;
    }

    public static VirtualInventoryBack findByPlayerUUID(String playerUUID) {
        List<VirtualInventoryBack> foundBacks = new ArrayList<>();
        TradeCraft.database().getVirtualInventories().find(eq("playerUUID", playerUUID)).into(foundBacks);
        if (foundBacks.size() > 1) {
            // Should not be possible
            // We return the first one and log the error
            TradeCraft.logger().severe("DATABASE ISSUE: multiple backs with the same playerUUID. Manually reconcile.");
            return foundBacks.get(0);
        }
        if (foundBacks.size() == 1) {
            return foundBacks.get(0);
        }
        return null;
    }
}
