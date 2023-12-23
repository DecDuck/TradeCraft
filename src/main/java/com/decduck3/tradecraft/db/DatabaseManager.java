package com.decduck3.tradecraft.db;

import com.decduck3.tradecraft.TradeCraft;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    public DatabaseManager() {
        String username = TradeCraft.config().getString("db.username");
        String password = TradeCraft.config().getString("db.password");
        String url = TradeCraft.config().getString("db.url");
        if(username == null || password == null || url == null){
            throw new RuntimeException("Could not initialise the database, url not found");
        }
        try(Connection connection = DriverManager.getConnection(url, username, password)){

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
