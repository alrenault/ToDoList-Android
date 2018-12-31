package com.todolist.aladdalo.todolist;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import static android.content.Context.ALARM_SERVICE;

public class ToDoAlarm {

    private static final String actionAlarm="com.todolist.aladdalo.todolist.intent.action.ALARM";
    private static final String IDTaskName = "TaskName";
    private static final String IDTaskDate = "TaskDate";

    public ToDoAlarm(Context context, String taskName, int taskDate,int hour){
        int i=0;

        Intent intent=new Intent();

        intent.putExtra(IDTaskName,taskName);

        String date=String.valueOf(taskDate);
        String time=String.valueOf(hour);

        date=date.substring(6,8)+"/"+date.substring(4,6)+"/"+date.substring(0,4);
        date+="(";
        date+=time.substring(1,3)+":"+time.substring(3,5);
        date+=")";

        intent.putExtra(IDTaskDate,date);
        intent.setAction(actionAlarm);

        //Log.d("Todo_"+this.toString(),intent.getStringExtra(IDTaskName));
        //intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

        //Alarm
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 234324243, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                + (i * 1000), pendingIntent);
        //Toast.makeText(context, "Alarm to:"+i, Toast.LENGTH_LONG).show();
        Log.d("Todo_"+this.toString(),"Alarm");

        //context.sendBroadcast(intent);
    }

}
