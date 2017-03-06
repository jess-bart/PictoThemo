package com.jessy_barthelemy.pictothemo.Api;

public class Picture {
    private int id ;
    private String theme;
    private User user;
    private int positiveVote;
    private int negativeVote;

    public Picture(int id, String theme, User user, int positiveVote, int negativeVote) {

        this.id = id;
        this.theme = theme;
        this.user = user;
        this.positiveVote = positiveVote;
        this.negativeVote = negativeVote;
    }

    public int getNegativeVote() {
        return negativeVote;
    }

    public void setNegativeVote(int negativeVote) {
        this.negativeVote = negativeVote;
    }

    public int getPositiveVote() {
        return positiveVote;
    }

    public void setPositiveVote(int positiveVote) {
        this.positiveVote = positiveVote;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
