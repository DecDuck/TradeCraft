package com.decduck3.tradecraft.db.models;

import com.decduck3.tradecraft.TradeCraft;
import org.bson.types.ObjectId;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class User {
    private ObjectId id;
    private String playerUUID;

    // Alternative login details
    private boolean alternativeLogin;
    private String alternativeUsername;
    private String alternativePasswordHash;

    // Cached objects
    private String cUsername;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getPlayerUUID() {
        return playerUUID;
    }

    public void setPlayerUUID(String playerUUID) {
        this.playerUUID = playerUUID;
    }

    public boolean isAlternativeLogin() {
        return alternativeLogin;
    }

    public void setAlternativeLogin(boolean alternativeLogin) {
        this.alternativeLogin = alternativeLogin;
    }

    public String getAlternativeUsername() {
        return alternativeUsername;
    }

    public void setAlternativeUsername(String alternativeUsername) {
        this.alternativeUsername = alternativeUsername;
    }

    public String getAlternativePasswordHash() {
        return alternativePasswordHash;
    }

    public void setAlternativePasswordHash(String alternativePasswordHash) {
        this.alternativePasswordHash = alternativePasswordHash;
    }

    public String getCUsername() {
        return cUsername;
    }

    public void setCUsername(String cUsername) {
        this.cUsername = cUsername;
    }

    public static User findOrInitialiseUser(String playerUUID, Player player){
        List<User> foundUsers = new ArrayList<>();
        TradeCraft.database().getUsers().find(eq("playerUUID", playerUUID)).into(foundUsers);
        if(foundUsers.size() > 1){
            // Should not be possible
            // We return the first one and log the error
            TradeCraft.logger().severe("DATABASE ISSUE: multiple users with the same playerUUID. Manually reconcile.");
            return foundUsers.get(0);
        }
        if(foundUsers.size() == 1){
            User foundUser = foundUsers.get(0);
            if(player != null && !player.getName().equals(foundUser.cUsername)){
                foundUser.setCUsername(player.getName());
                TradeCraft.database().getUsers().updateOne(eq("_id", foundUser.getId()), set("cUsername", foundUser.getCUsername()));
            }
            return foundUser;
        }

        // Otherwise, create a new user
        User user = new User();
        user.playerUUID = playerUUID;
        if(player != null){
            user.cUsername = player.getName();
        }
        TradeCraft.database().getUsers().insertOne(user);
        return user;
    }

    public static User findUser(String objectID){
        List<User> users = new ArrayList<>();
        TradeCraft.database().getUsers().find(eq("_id", new ObjectId(objectID))).into(users);
        if(users.size() != 1){
            return null;
        }
        return users.get(0);
    }

    public static User findUserByUsername(String username){
        List<User> users = new ArrayList<>();
        TradeCraft.database().getUsers().find(eq("alternativeUsername", username)).into(users);
        if(users.size() != 1){
            return null;
        }
        return users.get(0);
    }
}
