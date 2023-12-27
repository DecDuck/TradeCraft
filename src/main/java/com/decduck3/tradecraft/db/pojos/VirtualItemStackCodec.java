package com.decduck3.tradecraft.db.pojos;

import com.decduck3.tradecraft.inventory.VirtualItemStack;
import com.google.gson.Gson;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bukkit.inventory.ItemStack;

public class VirtualItemStackCodec implements Codec<VirtualItemStack> {
    private static final Gson gson = new Gson();
    @Override
    public VirtualItemStack decode(BsonReader bsonReader, DecoderContext decoderContext) {
        return gson.fromJson(bsonReader.readString(), VirtualItemStack.class);
    }

    @Override
    public void encode(BsonWriter bsonWriter, VirtualItemStack itemStack, EncoderContext encoderContext) {
        bsonWriter.writeString(gson.toJson(itemStack));
        bsonWriter.flush();
    }

    @Override
    public Class<VirtualItemStack> getEncoderClass() {
        return VirtualItemStack.class;
    }
}
