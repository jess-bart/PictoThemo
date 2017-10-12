package com.jessy_barthelemy.pictothemo.ApiObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class User implements Serializable {
    private int id;
    private String pseudo;
    private Calendar registrationDate;
    private ArrayList<Trophy> trophies;

    public User(){}

    public User(String pseudo){
        this.id = -1;
        this.pseudo = pseudo;
        this.trophies = new ArrayList<>();
    }

    public User(int id, String pseudo){
        this.id = id;
        this.pseudo = pseudo;
        this.trophies = new ArrayList<>();
    }

    public User(int id, String pseudo, Calendar registrationDate){
        this.id = id;
        this.pseudo = pseudo;
        this.trophies = new ArrayList<>();
        this.registrationDate = registrationDate;
    }

    public String getPseudo() { return pseudo; }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void addTrophy(Trophy trophy){
        this.trophies.add(trophy);
    }

    public Calendar getRegistrationDate() {
        return registrationDate;
    }

    public ArrayList<Trophy> getTrophies() {
        return trophies;
    }
}
