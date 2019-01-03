package com.todolist.aladdalo.todolist.db;

import android.provider.BaseColumns;

import com.orm.SugarRecord;
import com.orm.dsl.Table;
import com.orm.dsl.Unique;
import com.todolist.aladdalo.todolist.Priorite;

public class Task extends SugarRecord{
    private String taskName;

    private int date;

    private int time;

    private int priority;

    private boolean alarme;

    private int progress;

    public Task(){

    }

    public Task(String taskName, int progress, boolean alarme){

        this.taskName = taskName;
        this.date = 100000000;
        this.time = 0;
        this.priority = 0;
        this.progress = progress;
        this.alarme=alarme;
    }


    public Task(String taskName, int date, int time, int progress, boolean alarme){

        this.taskName = taskName;
        this.date = date;
        this.time = time;
        this.priority = 0;
        this.progress = progress;
        this.alarme=alarme;
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

        /*public static final String DB_NAME = "com.todolist.aladdalo.todolist.db";
    public static final int DB_VERSION = 1;

    public class TaskEntry implements BaseColumns {
        public static final String TABLE = "tasks";

        public static final String COL_TASK_TITLE = "title";
    }*/
}
