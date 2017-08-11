package com.jessy_barthelemy.pictothemo.ApiObjects;

import java.util.Calendar;

public class TokenInformations {
    private String accessToken;
    private Calendar expiresToken;
    private User user;
    private String password;
    private boolean isPasswordSalted;

    public TokenInformations() {
        this.accessToken = "";
        this.password = "";
        this.user = new User();
        this.expiresToken = null;
        this.isPasswordSalted = false;
    }

    public TokenInformations(String accessToken,Calendar expiresToken, User user, String password, boolean isPasswordSalted) {
        this.accessToken = accessToken;
        this.user = user;
        this.password = password;
        this.expiresToken = expiresToken;
        this.isPasswordSalted = isPasswordSalted;
    }

    public Calendar getExpiresToken() {
        return expiresToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isPasswordSalted() {
        return isPasswordSalted;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
