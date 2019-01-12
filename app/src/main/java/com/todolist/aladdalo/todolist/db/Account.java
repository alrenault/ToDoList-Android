package com.todolist.aladdalo.todolist.db;

import com.orm.SugarRecord;

public class Account extends SugarRecord {
    private String username;
    private String mdp;

    private boolean active;

    public Account(){}

    public Account(String username, String mdp){
        this.username = username;
        this.mdp = mdp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Account : username = "+username+" mdp = "+mdp;
    }

    public boolean same(Account accountExcept) {
        return username.equals(accountExcept.getUsername()) && mdp.equals(accountExcept.getMdp());
    }
}
