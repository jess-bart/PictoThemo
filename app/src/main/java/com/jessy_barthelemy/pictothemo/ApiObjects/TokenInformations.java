package com.jessy_barthelemy.pictothemo.ApiObjects;

import java.util.Calendar;

public class TokenInformations {
    private String accessToken;
    private Calendar expiresToken;
    private String pseudo;
    private String password;
    private boolean isPasswordSalted;

    public TokenInformations() {
        this.accessToken = "";
        this.pseudo = "";
        this.password = "";
        this.expiresToken = null;
        this.isPasswordSalted = false;
    }

    public TokenInformations(String accessToken,Calendar expiresToken, String pseudo, String password, boolean isPasswordSalted) {
        this.accessToken = accessToken;
        this.pseudo = pseudo;
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


    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
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
}
