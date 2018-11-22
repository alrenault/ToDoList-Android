package com.todolist.aladdalo.todolist.db;

/**
 * Classe qui encapsule les methodes de gestion et de recuperation de compte
 * */
public class Account {

    private Account(){} //ininstanciable

    public static boolean isAccount(){return false;}

    public static Task[] getTasksByAccount(){return null;}

    public static boolean authenticate(){return false;};
}
