package com.todolist.aladdalo.todolist;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static android.content.Context.ALARM_SERVICE;
import static java.lang.Integer.parseInt;

public class ToDoAlarm {

    private static final String actionAlarm="com.todolist.aladdalo.todolist.intent.action.ALARM";
    private static final String IDTaskName = "TaskName";
    private static final String IDTaskDate = "TaskDate";

    public ToDoAlarm(Context context, String taskName, int taskDate,int time,Intent intent,int idTask){


        //Intent intent=new Intent();

        intent.putExtra(IDTaskName,taskName);

        String dateStr=String.valueOf(taskDate);
        String timeStr=String.valueOf(time);

        String day=dateStr.substring(6,8);
        String month=dateStr.substring(4,6);
        String year=dateStr.substring(0,4);
        String hour=timeStr.substring(1,3);
        String min=timeStr.substring(3,5);

        String dateTime;
        dateTime=day+"/"+month+"/"+year;
        dateTime+="(";
        dateTime+=hour+":"+min;
        dateTime+=")";

        new ToDoNotif(0,context, taskName,dateTime);

        intent.putExtra(IDTaskDate,dateTime);
        intent.setAction(actionAlarm);

        //Log.d("Todo_"+this.toString(),intent.getStringExtra(IDTaskName));
        //intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

        //context.sendBroadcast(intent);

        //Alarm
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, parseInt(day));
        calendar.set(Calendar.MONTH,parseInt(month)-1);
        calendar.set(Calendar.YEAR, parseInt(year));

        calendar.set(Calendar.HOUR_OF_DAY, parseInt(hour));
        calendar.set(Calendar.MINUTE, parseInt(min));


        //removeAllAlarm(context,taskName, intent);
        addAlarm(context,idTask,taskName,intent,calendar);
        getPendingID(context,taskName);
        getAllKeyID(context);

        /*removeAlarm(context,taskName, intent);
        getAllKeyID(context);
        addAlarm(context,idTask,taskName,intent,calendar);
        getAllKeyID(context);*/

        //Toast.makeText(context, "Alarm to:"+i, Toast.LENGTH_LONG).show();
        //SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        //Log.d("Todo_"+this.toString(),"Alarm to:"+sdf.format(calendar.getTime()));


    }

    public void SavePendingID(int id,Context context){
       /* List<Integer> pendIntentList=getPendingID(context);
        if (pendIntentList.contains(id)) {
            return;
        }
        pendIntentList.add(id);
        */
    }

    public void getAllKeyID(Context context){
        //List<String> pendIntentList=new ArrayList<>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        for ( Map.Entry<String, ? > entry : prefs.getAll().entrySet()) {
            String key = entry.getKey();
            int id =(Integer) entry.getValue();
            Log.d("Todo_"+this.toString(),"all:"+key+":"+id);

        }
    }

    public int getPendingID(Context context,String keyTask){
        //List<String> pendIntentList=new ArrayList<>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        for ( Map.Entry<String, ? > entry : prefs.getAll().entrySet()) {
            String key = entry.getKey();
            int id =(Integer) entry.getValue();
            if(key==keyTask){
                Log.d("Todo_"+this.toString(),"get:"+id);
                return id;
            }
            //Log.d("Todo_"+this.toString(),key+":"+id);

        }
        return -1;
    }

    public void removeAlarm(Context context,String taskName,Intent intent) {

        int id = getPendingID(context, taskName);
        if (id != -1) {
            //Intent intent = new Intent();
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor sPf = prefs.edit();
            Log.d("Todo_" + this.toString(), "rem:" + taskName + ":" + id);
            sPf.remove(taskName);
            sPf.commit();
        } else{
            Log.d("Todo_" + this.toString(), "Alreadyrem:" + taskName);

        }


    }

    public void removeAllAlarm(Context context,String taskName,Intent intent){
        /*PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int)calendar.getTimeInMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), pendingIntent);*/

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor sPf=prefs.edit();

        sPf.clear();
        sPf.commit();
    }

    public void addAlarm(Context context,int ID,String taskName,Intent intent,Calendar calendar){
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), pendingIntent);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor sPf=prefs.edit();

        sPf.putInt(taskName,ID);
        sPf.commit();
    }



}
