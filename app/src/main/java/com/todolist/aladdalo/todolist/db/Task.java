package com.todolist.aladdalo.todolist.db;

import android.provider.BaseColumns;

import com.orm.SugarRecord;
import com.orm.dsl.Table;
import com.orm.dsl.Unique;
import com.todolist.aladdalo.todolist.Priorite;

public class Task extends SugarRecord{
    private String taskName;

    private int date;

    private  int time;

    private Priorite priority = Priorite.Fini;

    public Task(){

    }

    public Task(String taskName){

        this.taskName = taskName;
        this.date = 0;
        this.time = 0;
    }


    public Task(String taskName, int date){

        this.taskName = taskName;
        this.date = date;
        this.time = 0;
    }


    public Task(String taskName, int date, int time){

        this.taskName = taskName;
        this.date = date;
        this.time = time;
    }


    public String getTaskName() {
        return taskName;
    }

    public int getDate() {
        return date;
    }

    public String getDateString(){
        String date = String.valueOf(this.date);

        if(date.length() == 1){
            return "";
        }

        return date.substring(6,8)+"/"+date.substring(4,6)+"/"+date.substring(0,4);
    }


    public int getTime() {
        return time;
    }

    public String getTimeString(){
        String time = String.valueOf(this.time);
        if(time.length() == 1){
            return "";
        }
        return time.substring(1,3)+":"+time.substring(3,5);
    }

    public Priorite getPriority() {
        return priority;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setPriority(Priorite priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return taskName + " " + getDateString() + " " + getTimeString();
    }

        /*public static final String DB_NAME = "com.todolist.aladdalo.todolist.db";
    public static final int DB_VERSION = 1;

    public class TaskEntry implements BaseColumns {
        public static final String TABLE = "tasks";

        public static final String COL_TASK_TITLE = "title";
    }*/
}
