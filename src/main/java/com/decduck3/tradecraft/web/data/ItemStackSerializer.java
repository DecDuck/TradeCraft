package com.decduck3.tradecraft.web.data;

import com.decduck3.tradecraft.TradeCraft;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class ItemStackSerializer implements TypeAdapterFactory {
    public static final ItemStackSerializer SINGLETON = new ItemStackSerializer();

    private static Map<String, String> LANG = new HashMap<>();

    public static void init() {
        File localUnpackTarget = new File(TradeCraft.unpacker().getUnpackTarget(), TradeCraft.unpacker().getUnpackVersion());
        File file = new File(localUnpackTarget, "lang/en_us.json");
        Gson gson = new Gson();
        try {
            LANG = gson.fromJson(Files.readString(file.toPath()), new TypeToken<Map<String, String>>() {}.getType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        if (ItemStack.class.isAssignableFrom(typeToken.getRawType())) {
            return (TypeAdapter<T>) new ItemStackAdapter();
        }
        return null;
    }

    private static class ItemStackAdapter extends TypeAdapter<ItemStack> {
        @Override
        public void write(JsonWriter jsonWriter, ItemStack itemStack) throws IOException {
            if(itemStack == null){
                jsonWriter.nullValue();
                return;
            }
            jsonWriter.beginObject();
            jsonWriter.name("id");
            jsonWriter.value(itemStack.getType().getKey().toString());
            jsonWriter.name("amount");
            jsonWriter.value(itemStack.getAmount());
            if (LANG.containsKey(itemStack.translationKey())) {
                jsonWriter.name("name");
                jsonWriter.value(LANG.get(itemStack.translationKey()));
            }
            jsonWriter.name("key");
            jsonWriter.value(itemStack.translationKey());
            jsonWriter.name("rarity");
            jsonWriter.value(itemStack.getRarity().toString());
            jsonWriter.name("meta");
            ItemMeta meta = itemStack.getItemMeta();
            jsonWriter.beginObject();
            if (meta.displayName() != null) {
                jsonWriter.name("displayName");
                jsonWriter.value(((TranslatableComponent) Objects.requireNonNull(meta.displayName())).fallback());
            }
            jsonWriter.name("enchantments");
            jsonWriter.beginObject();
            Set<Map.Entry<Enchantment, Integer>> entrySet = meta.getEnchants().entrySet();
            for (Map.Entry<Enchantment, Integer> entry : entrySet) {
                jsonWriter.name(entry.getKey().toString());
                jsonWriter.value(entry.getValue().intValue());
            }
            jsonWriter.endObject();
            jsonWriter.endObject();
            jsonWriter.endObject();
        }

        @Override
        public ItemStack read(JsonReader jsonReader) throws IOException {
            return null;
        }
    }
}
