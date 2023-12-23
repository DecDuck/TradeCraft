package com.decduck3.tradecraft.web.session;

import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.NotificationReceiver;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.IdentityManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
I've hijack the security context to store request-relevant information i.e. session data and stuff
Probably not the best way to do this, but should be pretty performant and fits our use case so...
Fuck it, we're doing it
 */
public class ArbitraryDataSecurityContext implements SecurityContext {
    private final Map<String, String> arbitraryData = new HashMap<>();

    @Override
    public boolean authenticate() {
        return false;
    }

    @Override
    public boolean login(String s, String s1) {
        return false;
    }

    @Override
    public void logout() {

    }

    @Override
    public void setAuthenticationRequired() {

    }

    @Override
    public boolean isAuthenticationRequired() {
        return false;
    }

    @Override
    public void addAuthenticationMechanism(AuthenticationMechanism authenticationMechanism) {

    }

    @Override
    public List<AuthenticationMechanism> getAuthenticationMechanisms() {
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        return false;
    }

    @Override
    public Account getAuthenticatedAccount() {
        return null;
    }

    @Override
    public String getMechanismName() {
        return null;
    }

    @Override
    public IdentityManager getIdentityManager() {
        return null;
    }

    @Override
    public void authenticationComplete(Account account, String s, boolean b) {

    }

    @Override
    public void authenticationFailed(String s, String s1) {

    }

    @Override
    public void registerNotificationReceiver(NotificationReceiver notificationReceiver) {

    }

    @Override
    public void removeNotificationReceiver(NotificationReceiver notificationReceiver) {

    }

    public Map<String, String> getArbitraryData() {
        return arbitraryData;
    }

    public String getData(String key){
        return arbitraryData.get(key);
    }

    public void setData(String key, String value){
        arbitraryData.put(key, value);
    }
}
