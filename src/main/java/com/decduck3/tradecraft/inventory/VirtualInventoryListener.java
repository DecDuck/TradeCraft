package com.decduck3.tradecraft.inventory;

import com.decduck3.tradecraft.TradeCraft;
import com.decduck3.tradecraft.db.models.VirtualInventoryBack;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;

public class VirtualInventoryListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Initialise a virtual inventory if needed
        VirtualInventoryBack back = VirtualInventoryBack.findByPlayerUUID(event.getPlayer().getUniqueId().toString());
        if (back == null) {
            VirtualPlayerInventory inv = VirtualPlayerInventory.createNew(event.getPlayer());
            TradeCraft.database().getVirtualInventories().insertOne(inv.getDb());
        } else {
            // Loads the vinv into the player
            VirtualPlayerInventory vinv = new VirtualPlayerInventory(event.getPlayer(), back);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        // This is done immediately because it expires
        PlayerInventory playerInventory = event.getPlayer().getInventory();
        InventoryArchive archive = new InventoryArchive(playerInventory.getArmorContents(), playerInventory.getExtraContents(), playerInventory.getStorageContents());
        String playerUUID = event.getPlayer().getUniqueId() + "";

        // Now we have a non-expiring version of everything we need
        VirtualInventoryBack back = VirtualInventoryBack.findByPlayerUUID(playerUUID);
        assert back != null;
        OfflinePlayer player = TradeCraft.singleton().getConfig().getOfflinePlayer(playerUUID);
        assert player != null;
        List<ItemStack[]> archiveUnpacked = archive.unpack();
        VirtualPlayerInventory virtualPlayerInventory = new VirtualPlayerInventory(player, back, archiveUnpacked.get(0), archiveUnpacked.get(1), archiveUnpacked.get(2));
        TradeCraft.database().getVirtualInventories().replaceOne(eq("_id", back.getId()), virtualPlayerInventory.getDb());
    }

    private static class InventoryArchive {
        private final List<String> data;

        public InventoryArchive(ItemStack[]... inventories) {
            data = Arrays.stream(inventories).map(InventoryArchive::toBase64).toList();
        }

        public List<ItemStack[]> unpack() {
            return data.stream().map(e -> {
                try {
                    return fromBase64(e);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }).toList();
        }

        public static String toBase64(ItemStack[] inventory) throws IllegalStateException {
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

                // Write the size of the inventory
                dataOutput.writeInt(inventory.length);

                // Save every element in the list
                for (ItemStack itemStack : inventory) {
                    dataOutput.writeObject(itemStack);
                }

                // Serialize that array
                dataOutput.close();
                return Base64Coder.encodeLines(outputStream.toByteArray());
            } catch (Exception e) {
                throw new IllegalStateException("Unable to save item stacks.", e);
            }
        }

        public static ItemStack[] fromBase64(String data) throws IOException {
            try {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
                BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
                ItemStack[] items = new ItemStack[dataInput.readInt()];

                // Read the serialized inventory
                for (int i = 0; i < items.length; i++) {
                    items[i] = (ItemStack) dataInput.readObject();
                }

                dataInput.close();
                return items;
            } catch (ClassNotFoundException e) {
                throw new IOException("Unable to decode class type.", e);
            }
        }
    }
}
