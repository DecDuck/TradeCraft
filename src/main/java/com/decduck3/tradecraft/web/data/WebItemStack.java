package com.decduck3.tradecraft.web.data;

import io.papermc.paper.inventory.ItemRarity;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class WebItemStack {
    private final boolean empty;
    private String id;
    private Material material = null;
    private Integer amount = null;
    private ItemRarity rarity = null;
    private ItemMeta meta = null;

    public WebItemStack(ItemStack itemStack){
        if(itemStack == null){
            empty = true;
            return;
        }
        empty = false;
        id = itemStack.getType().getKey().toString();
        material = itemStack.getType();
        amount = itemStack.getAmount();
        rarity = itemStack.getRarity();
        meta = itemStack.getItemMeta();
    }

    public boolean isEmpty() {
        return empty;
    }

    public String getId() {
        return id;
    }

    public Material getMaterial() {
        return material;
    }

    public Integer getAmount() {
        return amount;
    }

    public ItemRarity getRarity() {
        return rarity;
    }

    public ItemMeta getMeta() {
        return meta;
    }
}
