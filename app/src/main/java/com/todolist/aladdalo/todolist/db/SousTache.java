package com.todolist.aladdalo.todolist.db;

import android.util.Log;

import com.orm.SugarRecord;
import com.todolist.aladdalo.todolist.Priorite;

import java.util.Objects;

public class SousTache extends SugarRecord {


    private int idtask;

    private String taskName;

    private int date;

    private int time;

    private int priority;

    private boolean alarme;

    private int progress;

    public SousTache(){

    }




    public SousTache(String taskName, int progress, boolean alarme,int idtask){

        this.taskName = taskName;
        this.date = 100000000;
        this.time = 0;
        this.priority = 0;
        this.progress = progress;
        this.alarme=alarme;
        this.idtask=idtask;
    }


    public SousTache(String taskName, int date, int time, int progress, boolean alarme,int idtask){

        this.taskName = taskName;
        this.date = date;
        this.time = time;
        this.priority = 0;
        this.progress = progress;
        this.alarme=alarme;
        this.idtask=idtask;
    }

    public SousTache(String taskName, int date, int time, int progress, boolean alarme, int priority,int idtask){

        this.taskName = taskName;
        this.date = date;
        this.time = time;
        this.priority = priority;
        this.progress = progress;
        this.alarme=alarme;
        this.idtask=idtask;
    }

    public String getTaskName() {
        return taskName;
    }

    public int getDate() {
        return date;
    }

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
        SousTache task = (SousTache) o;
        return getDate() == task.getDate() &&
                getTime() == task.getTime() &&
                getPriority() == task.getPriority() &&
                getAlarme() == task.getAlarme() &&
                getProgress() == task.getProgress() &&
                Objects.equals(getTaskName(), task.getTaskName());
    }

    public int getIdTask() {
        return idtask;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTaskName(), getDate(), getTime(), getPriority(), getAlarme(), getProgress());
    }
}


