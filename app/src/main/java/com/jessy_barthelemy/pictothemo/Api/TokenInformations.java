package com.jessy_barthelemy.pictothemo.Api;

import com.jessy_barthelemy.pictothemo.Helpers.ApplicationHelper;

import java.text.ParseException;
import java.util.Date;

public class TokenInformations {
    private String accessToken;
    private Date expiresToken;
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

    public TokenInformations(String accessToken,String expireToken, String pseudo, String password, boolean isPasswordSalted) {
        this.accessToken = accessToken;
        this.pseudo = pseudo;
        this.password = password;
        this.setExpiresToken(expireToken);
        this.isPasswordSalted = isPasswordSalted;
    }

    public Date getExpiresToken() {
        return expiresToken;
    }

    public void setExpiresToken(String expiresToken){
        try {
            this.expiresToken = ApplicationHelper.convertStringToDate(expiresToken);
        } catch (ParseException e) {
            this.expiresToken = null;
        }
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
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

    public void setPasswordSalted(boolean passwordSalted) {
        isPasswordSalted = passwordSalted;
    }
}
