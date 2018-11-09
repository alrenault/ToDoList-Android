package com.todolist.aladdalo.todolist.db;

import android.provider.BaseColumns;

import com.orm.SugarRecord;
import com.orm.dsl.Table;
import com.orm.dsl.Unique;

public class TaskContract extends SugarRecord{
    String taskName;

    public TaskContract(){

    }

    public TaskContract(String taskName){
        this.taskName = taskName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public String toString() {
        return taskName;
    }

        /*public static final String DB_NAME = "com.todolist.aladdalo.todolist.db";
    public static final int DB_VERSION = 1;

    public class TaskEntry implements BaseColumns {
        public static final String TABLE = "tasks";

        public static final String COL_TASK_TITLE = "title";
    }*/
}
