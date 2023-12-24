package com.decduck3.tradecraft.security;

import com.decduck3.tradecraft.TradeCraft;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class AccountLinkManager {

    private final Map<String, PendingAccountLink> pendingLinksByCode = new HashMap<>();

    public PendingAccountLink generate(){
        String code = null;
        boolean valid = false;
        while(!valid){
            code = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
            if(!pendingLinksByCode.containsKey(code)){
                valid = true;
            }
        }

        PendingAccountLink accountLink = new PendingAccountLink(code, null, LocalDateTime.now().plusMinutes(30));
        pendingLinksByCode.put(code, accountLink);
        return accountLink;
    }

    public boolean linkPlayer(String code, UUID playerUUID){
        if(!pendingLinksByCode.containsKey(code)){
            return false;
        }
        PendingAccountLink pendingAccountLink = pendingLinksByCode.get(code);
        if(pendingAccountLink.getExpiry().isBefore(LocalDateTime.now())){
            pendingLinksByCode.remove(code);
            return false;
        }
        pendingAccountLink.setUuid(playerUUID);
        return true;
    }

    // Only returns and destroy object if it's valid
    public PendingAccountLink collect(String code){
        if(!pendingLinksByCode.containsKey(code)){
            return null;
        }
        PendingAccountLink accountLink = pendingLinksByCode.get(code);
        if(accountLink.getUuid() == null){
            return null;
        }
        pendingLinksByCode.remove(code);
        return accountLink;
    }

    public void cleanup(){
        Set<String> codes = new HashSet<>(pendingLinksByCode.keySet());
        codes.forEach(e -> {
            if(pendingLinksByCode.get(e).getExpiry().isBefore(LocalDateTime.now())){
                pendingLinksByCode.remove(e);
            }
        });
    }

    public class PendingAccountLink {
        private String code;
        private UUID uuid;
        private LocalDateTime expiry;

        public PendingAccountLink(String code, UUID uuid, LocalDateTime expiry) {
            this.code = code;
            this.uuid = uuid;
            this.expiry = expiry;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public UUID getUuid() {
            return uuid;
        }

        public void setUuid(UUID uuid) {
            this.uuid = uuid;
        }

        public LocalDateTime getExpiry() {
            return expiry;
        }

        public void setExpiry(LocalDateTime expiry) {
            this.expiry = expiry;
        }
    };
}
