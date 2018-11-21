package com.todolist.aladdalo.todolist;

public enum Priorite {
    Fini(0),
    Faible(1),
    Moyenne(2),
    Forte(3);

    private int prio;

    Priorite(int t) {
        this.prio = t;
    }

    public int getPrio() {
        return prio;
    }
}
