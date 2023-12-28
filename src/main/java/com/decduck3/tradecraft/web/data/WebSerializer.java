package com.decduck3.tradecraft.web.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bson.types.ObjectId;

public class WebSerializer {
    public static final Gson singleton = new GsonBuilder()
            .registerTypeAdapterFactory(ItemStackSerializer.SINGLETON)
            .registerTypeAdapter(ObjectId.class, new ObjectIdSerializer())
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .create();
}
