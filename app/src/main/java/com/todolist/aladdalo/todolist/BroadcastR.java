package com.todolist.aladdalo.todolist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

public class BroadcastR extends BroadcastReceiver {

    private static final String actionAlarm="com.todolist.aladdalo.todolist.intent.action.ALARM";
    private static final String IDTaskName = "TaskName";
    private static final String IDTaskDate = "TaskDate";

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(actionAlarm)) {

            alarmAndNotif(context,intent);


        }else{
            Toast.makeText(context, "I don't know this action!!!!",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void alarmAndNotif(Context context,Intent intent){
        //Toast.makeText(context, "It's over!!!!", Toast.LENGTH_LONG).show();

        String taskName=intent.getStringExtra(IDTaskName);
        String taskDate=intent.getStringExtra(IDTaskDate);


        new ToDoNotif(1,context, taskName,taskDate);





    }
}
