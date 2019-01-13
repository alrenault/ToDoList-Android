package com.todolist.aladdalo.todolist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

/**
 * Gere des actions envoyees par un intent
 * Lancer une alarme puis creer une notif de deadline
 */
public class BroadcastR extends BroadcastReceiver {

    private static final String actionAlarm="com.todolist.aladdalo.todolist.intent.action.ALARM";
    private static final String IDTaskName = "TaskName";
    private static final String IDTaskDate = "TaskDate";
    private static final String IDTask = "IDTask";

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(actionAlarm)) {
            Log.d("Todo_","test");
            try {
                alarmAndNotif(context, intent);
            }catch(Exception e){
                Log.d("Todo_",Log.getStackTraceString(e));
            }

        }else{
            Toast.makeText(context, "I don't know this action!!!!",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Permet d'envoyer une notif
     * @param context
     * @param intent
     */
    public void alarmAndNotif(Context context,Intent intent){
        //Toast.makeText(context, "It's over!!!!", Toast.LENGTH_LONG).show();

        String taskName=intent.getStringExtra(IDTaskName);
        String taskDate=intent.getStringExtra(IDTaskDate);
        int taskID=intent.getIntExtra(IDTask,0);


        new ToDoNotif(1,context, taskName,taskDate,taskID);

    }
}
