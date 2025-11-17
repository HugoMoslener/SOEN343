package com.TopFounders.ui.controller;

import com.TopFounders.domain.model.User;

public class UserDataResponse {
    private User user;
    private String tierNotification;
    private boolean tierUpgraded;

    public UserDataResponse() {
    }

    public UserDataResponse(User user, String tierNotification, boolean tierUpgraded) {
        this.user = user;
        this.tierNotification = tierNotification;
        this.tierUpgraded = tierUpgraded;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTierNotification() {
        return tierNotification;
    }

    public void setTierNotification(String tierNotification) {
        this.tierNotification = tierNotification;
    }

    public boolean isTierUpgraded() {
        return tierUpgraded;
    }

    public void setTierUpgraded(boolean tierUpgraded) {
        this.tierUpgraded = tierUpgraded;
    }
}

