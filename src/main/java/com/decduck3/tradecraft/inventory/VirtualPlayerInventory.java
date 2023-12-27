package com.decduck3.tradecraft.inventory;

import com.decduck3.tradecraft.db.models.VirtualInventoryBack;
import org.bson.types.ObjectId;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class VirtualPlayerInventory implements PlayerInventory {
    private final VirtualInventoryBack db;
    private final OfflinePlayer player;

    private VirtualPlayerInventory(VirtualInventoryBack db, OfflinePlayer player) {
        this.db = db;
        this.player = player;
    }

    public VirtualPlayerInventory(OfflinePlayer player, VirtualInventoryBack entry) {
        Player onlinePlayer = player.getPlayer();
        if(onlinePlayer != null){
            // Load inventory database into player
            PlayerInventory inventory = onlinePlayer.getInventory();
            inventory.setArmorContents(this.getArmorContents());
            inventory.setContents(this.getContents());
            inventory.setExtraContents(this.getExtraContents());
        }

        this.player = player;
        this.db = entry;
    }

    public void syncEntry(){
        // Load player inventory into db
        Player onlinePlayer = this.player.getPlayer();
        if(onlinePlayer == null){
            throw new RuntimeException("Cannot sync db entry when player is offline!");
        }
        PlayerInventory inventory = onlinePlayer.getInventory();
        db.setArmor(Arrays.stream(inventory.getArmorContents()).map(e -> (VirtualItemStack) e).toList());
        db.setInventory(Arrays.stream(inventory.getContents()).map(e -> (VirtualItemStack) e).toList());
        db.setExtra(Arrays.stream(inventory.getExtraContents()).map(e -> (VirtualItemStack) e).toList());
    }

    public static VirtualPlayerInventory createNew(Player player){
        VirtualInventoryBack db = new VirtualInventoryBack();
        PlayerInventory inventory = player.getInventory();
        db.setArmor(Arrays.stream(inventory.getArmorContents()).map(e -> (VirtualItemStack) e).toList());
        db.setInventory(Arrays.stream(inventory.getContents()).map(e -> (VirtualItemStack) e).toList());
        db.setExtra(Arrays.stream(inventory.getExtraContents()).map(e -> (VirtualItemStack) e).toList());

        return new VirtualPlayerInventory(db, player);
    }

    private <T> T fetchOnlineOffline(Function<Player, T> online, Supplier<T> offline){
        Player onlinePlayer = player.getPlayer();
        if(onlinePlayer == null){
            return offline.get();
        }
        return online.apply(onlinePlayer);
    }

    private void switchOnlineOffline(Consumer<Player> online, Runnable offline){
        Player onlinePlayer = player.getPlayer();
        if(onlinePlayer == null){
            offline.run();
            return;
        }
        online.accept(onlinePlayer);
    }

    // Everything from here on out is passthrough
    @Override
    public @Nullable ItemStack @NotNull [] getArmorContents() {
        return fetchOnlineOffline(
                player -> player.getInventory().getArmorContents(),
                () -> (ItemStack[]) db.getArmor().stream().toArray()
        );
    }

    @Override
    public @Nullable ItemStack @NotNull [] getExtraContents() {
        return fetchOnlineOffline(
                player -> player.getInventory().getExtraContents(),
                () -> (ItemStack[]) db.getArmor().stream().toArray()
        );
    }

    @Override
    public @Nullable ItemStack getHelmet() {
        return fetchOnlineOffline(
                player -> player.getInventory().getHelmet(),
                () -> db.getArmor().get(0)
        );
    }

    @Override
    public @Nullable ItemStack getChestplate() {
        return fetchOnlineOffline(
                player -> player.getInventory().getChestplate(),
                () -> db.getArmor().get(1)
        );
    }

    @Override
    public @Nullable ItemStack getLeggings() {
        return fetchOnlineOffline(
                player -> player.getInventory().getLeggings(),
                () -> db.getArmor().get(2)
        );
    }

    @Override
    public @Nullable ItemStack getBoots() {
        return fetchOnlineOffline(
                player -> player.getInventory().getBoots(),
                () -> db.getArmor().get(3)
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
                () -> db.getInventory().set(i, (VirtualItemStack) itemStack)
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
                    for(int i = 0; i < itemStacks.length; i++){
                        while(db.getInventory().get(currentNullIndex) != null){
                            currentNullIndex++;
                            if(currentNullIndex > db.getInventory().size()){
                                break itemLoop;
                            }
                        }
                        //currentNullIndex--;
                        db.getInventory().set(currentNullIndex, (VirtualItemStack) items.get(i));
                        items.remove(i);
                    }

                    return items;
                }
        );
    }

    @Override
    public @NotNull HashMap<Integer, ItemStack> removeItem(@NotNull ItemStack... itemStacks) throws IllegalArgumentException {
        return null;
    }

    @Override
    public @NotNull HashMap<Integer, ItemStack> removeItemAnySlot(@NotNull ItemStack... itemStacks) throws IllegalArgumentException {
        return null;
    }

    @Override
    public @Nullable ItemStack @NotNull [] getContents() {
        return new ItemStack[0];
    }

    @Override
    public void setContents(@Nullable ItemStack @NotNull [] itemStacks) throws IllegalArgumentException {

    }

    @Override
    public @Nullable ItemStack @NotNull [] getStorageContents() {
        return new ItemStack[0];
    }

    @Override
    public void setStorageContents(@Nullable ItemStack @NotNull [] itemStacks) throws IllegalArgumentException {

    }

    @Override
    public boolean contains(@NotNull Material material) throws IllegalArgumentException {
        return false;
    }

    @Override
    public boolean contains(@Nullable ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean contains(@NotNull Material material, int i) throws IllegalArgumentException {
        return false;
    }

    @Override
    public boolean contains(@Nullable ItemStack itemStack, int i) {
        return false;
    }

    @Override
    public boolean containsAtLeast(@Nullable ItemStack itemStack, int i) {
        return false;
    }

    @Override
    public @NotNull HashMap<Integer, ? extends ItemStack> all(@NotNull Material material) throws IllegalArgumentException {
        return null;
    }

    @Override
    public @NotNull HashMap<Integer, ? extends ItemStack> all(@Nullable ItemStack itemStack) {
        return null;
    }

    @Override
    public int first(@NotNull Material material) throws IllegalArgumentException {
        return 0;
    }

    @Override
    public int first(@NotNull ItemStack itemStack) {
        return 0;
    }

    @Override
    public int firstEmpty() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void remove(@NotNull Material material) throws IllegalArgumentException {

    }

    @Override
    public void remove(@NotNull ItemStack itemStack) {

    }

    @Override
    public void clear(int i) {

    }

    @Override
    public void clear() {

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
        return null;
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
