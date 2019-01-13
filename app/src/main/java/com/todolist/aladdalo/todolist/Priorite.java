package com.todolist.aladdalo.todolist;

import com.google.gson.annotations.SerializedName;

/**
 * Enum représentant la priorité sous forme d'entier afin de limiter les possibilités
 */
public enum Priorite{
    @SerializedName("0") Fini(0),
    @SerializedName("1") Faible(1),
    @SerializedName("2") Moyenne(2),
    @SerializedName("3") Forte(3);


    private int prio;

    Priorite(int t) {
        this.prio = t;
    }

    public int getPrio() {
        return prio;
    }
}
