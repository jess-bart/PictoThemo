package com.jessy_barthelemy.pictothemo.ApiObjects;

import java.io.Serializable;

public class User implements Serializable {
    private int id;
    private String pseudo;

    User(){}

    public User(int id, String pseudo){
        this.id = id;
        this.pseudo = pseudo;
    }

    public User(String pseudo){
        this.id = -1;
        this.pseudo = pseudo;
    }

    public String getPseudo() { return pseudo; }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public int getId() {
        return id;
    }
}
