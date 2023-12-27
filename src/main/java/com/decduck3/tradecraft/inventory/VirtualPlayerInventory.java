package com.decduck3.tradecraft.inventory;

import com.decduck3.tradecraft.TradeCraft;
import com.decduck3.tradecraft.db.models.VirtualInventoryBack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.decduck3.tradecraft.utils.SuperList.superlist;

public class VirtualPlayerInventory implements PlayerInventory {
    private final VirtualInventoryBack db;
    private final OfflinePlayer player;

    // Just a raw create
    public VirtualPlayerInventory(VirtualInventoryBack db, OfflinePlayer player) {
        this.db = db;
        this.player = player;
    }

    // Loads player inventory from backer
    public VirtualPlayerInventory(OfflinePlayer player, VirtualInventoryBack back) {
        this.db = back;

        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer != null) {
            TradeCraft.logger().info("Loading virtual inventory in player's inventory");
            // Load inventory database into player
            PlayerInventory playerInventory = onlinePlayer.getInventory();
            playerInventory.setArmorContents(this.getArmorContents());
            playerInventory.setStorageContents(this.getStorageContents());
            // playerInventory.setExtraContents(this.getExtraContents());
            onlinePlayer.updateInventory();
        }

        this.player = player;
    }

    // Create from alternative archive
    public VirtualPlayerInventory(OfflinePlayer player, VirtualInventoryBack back, ItemStack[] armor, ItemStack[] extra, ItemStack[] contents){
        this.player = player;
        this.db = back;
        db.setArmor(Arrays.stream(armor).toList());
        db.setExtra(Arrays.stream(extra).toList());
        db.setInventory(Arrays.stream(contents).toList());
    }

    public void syncBacker() {
        // Load player inventory into db
        Player onlinePlayer = this.player.getPlayer();
        if (onlinePlayer == null) {
            throw new RuntimeException("Cannot sync db entry when player is offline!");
        }
        PlayerInventory inventory = onlinePlayer.getInventory();
        db.setArmor(Arrays.stream(inventory.getArmorContents()).toList());
        db.setInventory(Arrays.stream(inventory.getStorageContents()).toList());
        db.setExtra(Arrays.stream(inventory.getExtraContents()).toList());
    }

    public static VirtualPlayerInventory createNew(Player player) {
        VirtualInventoryBack db = new VirtualInventoryBack();
        PlayerInventory inventory = player.getInventory();
        db.setPlayerUUID(player.getUniqueId().toString());
        db.setArmor(Arrays.stream(inventory.getArmorContents()).toList());
        db.setInventory(Arrays.stream(inventory.getStorageContents()).toList());
        db.setExtra(Arrays.stream(inventory.getExtraContents()).toList());

        return new VirtualPlayerInventory(db, player);
    }

    private <T> T fetchOnlineOffline(Function<Player, T> online, Supplier<T> offline) {
        if(player == null){
            return offline.get();
        }
        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer == null) {
            return offline.get();
        }
        return online.apply(onlinePlayer);
    }

    private void switchOnlineOffline(Consumer<Player> online, Runnable offline) {
        if(player == null){
            offline.run();
            return;
        }
        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer == null) {
            offline.run();
            return;
        }
        online.accept(onlinePlayer);
    }

    public VirtualInventoryBack getDb() {
        return db;
    }

    // Everything from here on out is passthrough
    @Override
    public @Nullable ItemStack @NotNull [] getArmorContents() {
        return fetchOnlineOffline(
                player -> player.getInventory().getArmorContents(),
                () -> db.getArmor().toArray(new ItemStack[0])
        );
    }

    @Override
    public @Nullable ItemStack @NotNull [] getExtraContents() {
        return fetchOnlineOffline(
                player -> player.getInventory().getExtraContents(),
                () -> db.getArmor().toArray(new ItemStack[0])
        );
    }

    @Override
    public @Nullable ItemStack getHelmet() {
        return fetchOnlineOffline(
                player -> player.getInventory().getHelmet(),
                () -> db.getArmor().get(3)
        );
    }

    @Override
    public @Nullable ItemStack getChestplate() {
        return fetchOnlineOffline(
                player -> player.getInventory().getChestplate(),
                () -> db.getArmor().get(2)
        );
    }

    @Override
    public @Nullable ItemStack getLeggings() {
        return fetchOnlineOffline(
                player -> player.getInventory().getLeggings(),
                () -> db.getArmor().get(1)
        );
    }

    @Override
    public @Nullable ItemStack getBoots() {
        return fetchOnlineOffline(
                player -> player.getInventory().getBoots(),
                () -> db.getArmor().get(0)
        );
    }

    @Override
    public int getSize() {
        return fetchOnlineOffline(
                player -> player.getInventory().getSize(),
                () -> db.getInventory().size()
        );
    }

    // Sticking with the default max cap of 64
    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public void setMaxStackSize(int i) {
        // Nothing!
    }

    @Override
    public @Nullable ItemStack getItem(int i) {
        return fetchOnlineOffline(
                player -> player.getInventory().getItem(i),
                () -> db.getInventory().get(i)
        );
    }

    @Override
    public void setItem(int i, @Nullable ItemStack itemStack) {
        switchOnlineOffline(
                player -> player.getInventory().setItem(i, itemStack),
                () -> db.getInventory().set(i, itemStack)
        );
    }

    @Override
    public @NotNull HashMap<Integer, ItemStack> addItem(@NotNull ItemStack... itemStacks) throws IllegalArgumentException {
        return fetchOnlineOffline(
                player -> player.getInventory().addItem(itemStacks),
                () -> {
                    HashMap<Integer, ItemStack> items = new HashMap<>();
                    for (int i = 0; i < itemStacks.length; i++) {
                        items.put(i, items.get(i));
                    }
                    int currentNullIndex = 0;
                    itemLoop:
                    for (int i = 0; i < itemStacks.length; i++) {
                        while (db.getInventory().get(currentNullIndex) != null) {
                            currentNullIndex++;
                            if (currentNullIndex > db.getInventory().size()) {
                                break itemLoop;
                            }
                        }
                        //currentNullIndex--;
                        db.getInventory().set(currentNullIndex, items.get(i));
                        items.remove(i);
                    }

                    return items;
                }
        );
    }

    @Override
    public @NotNull HashMap<Integer, ItemStack> removeItem(@NotNull ItemStack... itemStacks) throws IllegalArgumentException {
        return fetchOnlineOffline(
                player -> player.getInventory().removeItem(itemStacks),
                () -> {
                    HashMap<Integer, ItemStack> items = new HashMap<>();
                    // Loop over inventory once, as the itemStacks array is probably shorter
                    List<ItemStack> inventory = db.getInventory();
                    for (int i = 0; i < inventory.size(); i++) {
                        for (int m = 0; m < itemStacks.length; m++) {
                            if (inventory.get(i).equals(itemStacks[m])) {
                                if (itemStacks[m].getAmount() > inventory.get(i).getAmount()) {
                                    // Remove and add the leftovers to items
                                    inventory.set(i, null);
                                    int leftOver = itemStacks[m].getAmount() - inventory.get(i).getAmount();
                                    ItemStack leftovers = itemStacks[m];
                                    leftovers.setAmount(leftOver);
                                    items.put(m, leftovers);
                                } else if (itemStacks[m].getAmount() == inventory.get(i).getAmount()) {
                                    // Remove
                                    inventory.set(i, null);
                                } else if (itemStacks[m].getAmount() < inventory.get(i).getAmount()) {
                                    // Subtract
                                    ItemStack remaining = itemStacks[m];
                                    remaining.setAmount(inventory.get(i).getAmount() - itemStacks[m].getAmount());
                                    inventory.set(i, remaining);
                                } else {
                                    throw new RuntimeException("Logically not possible to reach here.");
                                }
                            }
                        }
                    }
                    db.setInventory(inventory);

                    return items;
                }
        );
    }

    @Override
    public @NotNull HashMap<Integer, ItemStack> removeItemAnySlot(@NotNull ItemStack... itemStacks) throws IllegalArgumentException {
        return fetchOnlineOffline(
                player -> player.getInventory().removeItem(itemStacks),
                () -> {
                    HashMap<Integer, ItemStack> leftover = this.removeItem(itemStacks);

                    List<ItemStack> armor = db.getArmor();
                    for (int i = 0; i < 4; i++) {
                        Set<Integer> keys = leftover.keySet();
                        for (Integer key : keys) {
                            if (armor.get(i).equals(leftover.get(key))) {
                                ItemStack item = leftover.get(key);
                                if (item.getAmount() > armor.get(i).getAmount()) {
                                    // Remove and add the leftovers to items
                                    armor.set(i, null);
                                    int leftOver = item.getAmount() - armor.get(i).getAmount();
                                    item.setAmount(leftOver);
                                    leftover.put(key, item);
                                } else if (item.getAmount() == armor.get(i).getAmount()) {
                                    // Remove
                                    armor.set(i, null);
                                    leftover.remove(key);
                                } else if (item.getAmount() < armor.get(i).getAmount()) {
                                    // Subtract
                                    item.setAmount(armor.get(i).getAmount() - item.getAmount());
                                    armor.set(i, item);
                                    leftover.put(key, item);
                                } else {
                                    throw new RuntimeException("Logically not possible to reach here.");
                                }
                            }
                        }
                    }
                    db.setArmor(armor);

                    // TODO do it for extra item (not strictly needed)
                    return leftover;
                }
        );
    }

    @Override
    public @Nullable ItemStack @NotNull [] getContents() {
        return fetchOnlineOffline(
                player -> player.getInventory().getContents(),
                () -> superlist(db.getInventory(), db.getArmor()).toArray(new ItemStack[0])
        );
    }

    @Override
    public void setContents(@Nullable ItemStack @NotNull [] itemStacks) throws IllegalArgumentException {
        switchOnlineOffline(
                player -> player.getInventory().setContents(itemStacks),
                () -> db.setInventory(Arrays.stream(itemStacks).toList())
        );
    }

    @Override
    public @Nullable ItemStack @NotNull [] getStorageContents() {
        return fetchOnlineOffline(
                player -> player.getInventory().getStorageContents(),
                () -> db.getInventory().toArray(new ItemStack[0])
        );
    }

    @Override
    public void setStorageContents(@Nullable ItemStack @NotNull [] itemStacks) throws IllegalArgumentException {
        switchOnlineOffline(
                player -> player.getInventory().setStorageContents(itemStacks),
                () -> db.setInventory(Arrays.stream(itemStacks).toList())
        );
    }

    @Override
    public boolean contains(@NotNull Material material) throws IllegalArgumentException {
        return fetchOnlineOffline(
                player -> player.getInventory().contains(material),
                () -> superlist(db.getInventory(), db.getArmor()).stream().anyMatch(e -> e.getType().equals(material))
        );
    }

    @Override
    public boolean contains(@Nullable ItemStack itemStack) {
        return fetchOnlineOffline(
                player -> player.getInventory().contains(itemStack),
                () -> superlist(db.getInventory(), db.getArmor()).stream().anyMatch(e -> e.equals(itemStack))
        );
    }

    @Override
    public boolean contains(@NotNull Material material, int i) throws IllegalArgumentException {
        return fetchOnlineOffline(
                player -> player.getInventory().contains(material),
                () -> superlist(db.getInventory(), db.getArmor()).stream().anyMatch(e -> e.getType().equals(material) && e.getAmount() == i)
        );
    }

    @Override
    public boolean contains(@Nullable ItemStack itemStack, int i) {
        return fetchOnlineOffline(
                player -> player.getInventory().contains(itemStack),
                () -> superlist(db.getInventory(), db.getArmor()).stream().anyMatch(e -> e.isSimilar(itemStack) && e.getAmount() == i)
        );
    }

    @Override
    public boolean containsAtLeast(@Nullable ItemStack itemStack, int i) {
        return fetchOnlineOffline(
                player -> player.getInventory().containsAtLeast(itemStack, i),
                () -> superlist(db.getInventory(), db.getArmor()).stream().anyMatch(e -> e.isSimilar(itemStack) && e.getAmount() > i)
        );
    }

    @Override
    public @NotNull HashMap<Integer, ? extends ItemStack> all(@NotNull Material material) throws IllegalArgumentException {
        return fetchOnlineOffline(
                player -> player.getInventory().all(material),
                () -> {
                    List<ItemStack> combined = superlist(db.getInventory(), db.getArmor());
                    return (HashMap<Integer, ? extends ItemStack>) IntStream.range(0, combined.size())
                            .mapToObj(e -> new AbstractMap.SimpleEntry<>(e, combined.get(e)))
                            .filter(e -> e.getValue().getType().equals(material))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                }
        );
    }

    @Override
    public @NotNull HashMap<Integer, ? extends ItemStack> all(@Nullable ItemStack itemStack) {
        return fetchOnlineOffline(
                player -> player.getInventory().all(itemStack),
                () -> {
                    List<ItemStack> combined = superlist(db.getInventory(), db.getArmor());
                    return (HashMap<Integer, ? extends ItemStack>) IntStream.range(0, combined.size())
                            .mapToObj(e -> new AbstractMap.SimpleEntry<>(e, combined.get(e)))
                            .filter(e -> e.getValue().isSimilar(itemStack))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                }
        );
    }

    @Override
    public int first(@NotNull Material material) throws IllegalArgumentException {
        return fetchOnlineOffline(
                player -> player.getInventory().first(material),
                () -> IntStream.range(0, db.getInventory().size())
                        .filter(e -> db.getInventory().get(e).getType().equals(material))
                        .findFirst()
                        .orElseThrow(IllegalArgumentException::new)
        );
    }

    @Override
    public int first(@NotNull ItemStack itemStack) {
        return fetchOnlineOffline(
                player -> player.getInventory().first(itemStack),
                () -> IntStream.range(0, db.getInventory().size())
                        .filter(e -> db.getInventory().get(e).equals(itemStack))
                        .findFirst()
                        .orElseThrow(IllegalArgumentException::new)
        );
    }

    @Override
    public int firstEmpty() {
        return fetchOnlineOffline(
                player -> player.getInventory().firstEmpty(),
                () -> IntStream.range(0, db.getInventory().size())
                        .filter(e -> db.getInventory().get(e) == null || db.getInventory().get(e).isEmpty())
                        .findFirst().orElse(-1)
        );
    }

    @Override
    public boolean isEmpty() {
        return fetchOnlineOffline(
                player -> player.getInventory().isEmpty(),
                () -> superlist(db.getInventory(), db.getArmor()).stream().filter(e -> !(e == null || e.isEmpty())).toList().isEmpty()
        );
    }

    @Override
    public void remove(@NotNull Material material) throws IllegalArgumentException {
        switchOnlineOffline(
                player -> player.getInventory().remove(material),
                () -> {

                }
        );
    }

    @Override
    public void remove(@NotNull ItemStack itemStack) {
        switchOnlineOffline(
                player -> player.getInventory().remove(itemStack),
                () -> {}
        );
    }

    @Override
    public void clear(int i) {
        switchOnlineOffline(
                player -> player.getInventory().clear(i),
                () -> db.getInventory().set(i, null)
        );
    }

    @Override
    public void clear() {
        switchOnlineOffline(
                player -> player.getInventory().clear(),
                () -> db.setInventory(Arrays.asList(new ItemStack[db.getInventory().size()]))
        );
    }

    @Override
    public int close() {
        return 0;
    }

    @Override
    public @NotNull List<HumanEntity> getViewers() {
        return null;
    }

    @Override
    public @NotNull InventoryType getType() {
        return InventoryType.PLAYER;
    }

    @Override
    public void setItem(@NotNull EquipmentSlot equipmentSlot, @Nullable ItemStack itemStack) {

    }

    @Override
    public @NotNull ItemStack getItem(@NotNull EquipmentSlot equipmentSlot) {
        return null;
    }

    @Override
    public void setArmorContents(@Nullable ItemStack[] itemStacks) {

    }

    @Override
    public void setExtraContents(@Nullable ItemStack[] itemStacks) {

    }

    @Override
    public void setHelmet(@Nullable ItemStack itemStack) {

    }

    @Override
    public void setChestplate(@Nullable ItemStack itemStack) {

    }

    @Override
    public void setLeggings(@Nullable ItemStack itemStack) {

    }

    @Override
    public void setBoots(@Nullable ItemStack itemStack) {

    }

    @Override
    public @NotNull ItemStack getItemInMainHand() {
        return null;
    }

    @Override
    public void setItemInMainHand(@Nullable ItemStack itemStack) {

    }

    @Override
    public @NotNull ItemStack getItemInOffHand() {
        return null;
    }

    @Override
    public void setItemInOffHand(@Nullable ItemStack itemStack) {

    }

    @Override
    public @NotNull ItemStack getItemInHand() {
        return null;
    }

    @Override
    public void setItemInHand(@Nullable ItemStack itemStack) {

    }

    @Override
    public int getHeldItemSlot() {
        return 0;
    }

    @Override
    public void setHeldItemSlot(int i) {

    }

    @Override
    public @Nullable HumanEntity getHolder() {
        return null;
    }

    @Override
    public @Nullable InventoryHolder getHolder(boolean b) {
        return null;
    }

    @Override
    public @NotNull ListIterator<ItemStack> iterator() {
        return null;
    }

    @Override
    public @NotNull ListIterator<ItemStack> iterator(int i) {
        return null;
    }

    @Override
    public @Nullable Location getLocation() {
        return null;
    }
}
