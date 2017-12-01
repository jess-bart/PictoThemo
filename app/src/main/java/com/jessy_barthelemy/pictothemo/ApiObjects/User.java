package com.jessy_barthelemy.pictothemo.ApiObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class User implements Serializable {
    private int id;
    private String pseudo;
    private Calendar registrationDate;
    private int profil;
    private ArrayList<Trophy> trophies;

    public User(){}

    public User(String pseudo){
        this.id = -1;
        this.pseudo = pseudo;
        this.trophies = new ArrayList<>();
        this.profil = 0;
    }

    public User(int id, String pseudo){
        this.id = id;
        this.pseudo = pseudo;
        this.trophies = new ArrayList<>();
        this.profil = 0;
    }

    public User(int id, String pseudo, Calendar registrationDate, int profil){
        this.id = id;
        this.pseudo = pseudo;
        this.trophies = new ArrayList<>();
        this.registrationDate = registrationDate;
        this.profil = profil;
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

    public int getProfil() {
        return profil;
    }
}
