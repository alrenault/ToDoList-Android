package com.todolist.aladdalo.todolist.db;

import com.orm.SugarRecord;
import com.todolist.aladdalo.todolist.Priorite;

import java.util.Objects;

/**
 * Classe créant une tâche.
 * Etend SugarRecord pour en faire une table dans la base de donnée interne
 */
public class Task extends SugarRecord{

    /**Nom (ou intitulé) de la tâche*/
    private String taskName;

    /**Date de la deadline de la tâche*/
    private int date;

    /** Heure de la deadline de la tâche*/
    private int time;

    /**Priorité de la tâche*/
    private int priority;

    /**Booléen indiquant si une alarme est activée pour la tâche ou non*/
    private boolean alarme;

    /**Progression (en pourcentage) de la tâche*/
    private int progress;

    /**Constructeur par défaut*/
    public Task(){

    }

    /**
     * Constructeur sans deadline ou priorité (par défaut à faible)
     * @param taskName Intitulé de la tâche
     * @param progress Pourcentage de progression de la tâche
     * @param alarme Si une alarme a été créée ou non
     */
    public Task(String taskName, int progress, boolean alarme){

        this.taskName = taskName;
        this.date = 100000000;
        this.time = 0;
        this.priority = 0;
        this.progress = progress;
        this.alarme=alarme;
    }

    /**
     * Constructeur sans priorité (par défaut à faible
     * @param taskName Intitulé de la tâche
     * @param date Date de la deadline
     * @param time Heure de la deadline
     * @param progress Pourcentage de progression de la tâche
     * @param alarme Si une alarme a été créée ou non
     */
    public Task(String taskName, int date, int time, int progress, boolean alarme){

        this.taskName = taskName;
        this.date = date;
        this.time = time;
        this.priority = 0;
        this.progress = progress;
        this.alarme=alarme;
    }

    /**
     * Constructeur contenant toutes les informations
     * @param taskName Intitulé de la tâche
     * @param date Date de la deadline
     * @param time Heure de la deadline
     * @param progress Pourcentage de progression de la tâche
     * @param alarme Si une alarme a été créée ou non
     * @param priority Priorité de la tâche
     */
    public Task(String taskName, int date, int time, int progress, boolean alarme, int priority){

        this.taskName = taskName;
        this.date = date;
        this.time = time;
        this.priority = priority;
        this.progress = progress;
        this.alarme=alarme;
    }

    public String getTaskName() {
        return taskName;
    }

    public int getDate() {
        return date;
    }

    /**
     * Retourne la date sous la forme d'une string de la forme suivante : dd/MM/yyyy
     * @return La date de la deadline de la tâche
     */
    public String getDateString(){
        String date = String.valueOf(this.date);

        if(date.length() != 8){
            return "";
        }

        return date.substring(6,8)+"/"+date.substring(4,6)+"/"+date.substring(2,4);
    }

    public int getTime() {
        return time;
    }

    /**
     * Retourne l'heure sous la forme d'une string de la forme suivante : HH:mm
     * @return L'heure de la deadline de la tâche
     */
    public String getTimeString(){
        String time = String.valueOf(this.time);

        if(time.length() != 5){
            return "";
        }

        return time.substring(1,3)+":"+time.substring(3,5);
    }

    public int getPriority() {
        return priority;
    }

    public boolean getAlarme() {
        return alarme;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public void setPriority(Priorite priority) {
        this.priority = priority.getPrio();
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }

    public void setAlarme(boolean alarme) {this.alarme=alarme;}

    public boolean isFinish(int time, int date){
        if(this.date < date)
            return true;
        if(this.date == date && this.time < time)
            return true;

        return false;
    }

    @Override
    public String toString() {
        return taskName + " " + getDateString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return getDate() == task.getDate() &&
                getTime() == task.getTime() &&
                getPriority() == task.getPriority() &&
                getAlarme() == task.getAlarme() &&
                getProgress() == task.getProgress() &&
                Objects.equals(getTaskName(), task.getTaskName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTaskName(), getDate(), getTime(), getPriority(), getAlarme(), getProgress());
    }

}
