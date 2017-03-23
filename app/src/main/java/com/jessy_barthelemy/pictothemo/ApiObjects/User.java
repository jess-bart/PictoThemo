package com.jessy_barthelemy.pictothemo.ApiObjects;

public class User {
    private String pseudo;

    public User(String pseudo){
        this.pseudo = pseudo;
    }

    public String getPseudo() { return pseudo; }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }


}
