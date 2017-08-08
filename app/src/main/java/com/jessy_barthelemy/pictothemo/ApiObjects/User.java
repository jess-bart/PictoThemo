package com.jessy_barthelemy.pictothemo.ApiObjects;

import java.io.Serializable;

public class User implements Serializable {
    private int id;
    private String pseudo;

    public User(int id, String pseudo){
        this.id = id;
        this.pseudo = pseudo;
    }

    public String getPseudo() { return pseudo; }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }


}
