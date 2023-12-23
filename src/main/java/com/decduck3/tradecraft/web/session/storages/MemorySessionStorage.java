package com.decduck3.tradecraft.web.session.storages;

import com.decduck3.tradecraft.web.session.SessionStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemorySessionStorage implements SessionStorage {
    private final Map<String, Map<String, String>> storage = new HashMap<>();

    @Override
    public void save(String id, String key, String value) {
        if(!storage.containsKey(id)){
            storage.put(id, new HashMap<>());
        }
        storage.get(id).put(key, value);
    }

    @Override
    public String fetch(String id, String key) {
        if(!storage.containsKey(id)){
            return null;
        }
        return storage.get(id).get(key);
    }

    @Override
    public String genID() {
        return ("tc." + UUID.randomUUID() + UUID.randomUUID()).replace("-", "");
    }
}
