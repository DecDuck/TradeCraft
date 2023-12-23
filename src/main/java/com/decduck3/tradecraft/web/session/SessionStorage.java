package com.decduck3.tradecraft.web.session;

public interface SessionStorage {
    void save(String id, String key, String value);
    String fetch(String id, String key);
    void delete(String id, String key);
    String genID();
}
