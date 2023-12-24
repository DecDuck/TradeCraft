package com.decduck3.tradecraft.security;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class PasswordUtilities {
    private static final int SALT_ROUNDS = 16;
    public static boolean validPassword(String password){
        // Simple password rules, just needs to be longer or equal to 16 characters
        return password.length() >= 16;
    }

    // Abstractions on top of BCrypt for easy swap-out
    public static String hashPassword(String password){
        return BCrypt.hashpw(password, BCrypt.gensalt(SALT_ROUNDS));
    }
    public static boolean comparePasswords(String password, String hash){
        return BCrypt.checkpw(password, hash);
    }
}
