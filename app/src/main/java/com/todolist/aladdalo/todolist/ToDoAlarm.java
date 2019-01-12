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

import com.orm.query.Condition;
import com.orm.query.Select;
import com.todolist.aladdalo.todolist.db.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static android.content.Context.ALARM_SERVICE;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;
import static java.lang.Integer.parseInt;

/**
* Classe pour gérer le système d'alerte à l'aide de sonneries ou notification
* */
public class ToDoAlarm {

    private static final String actionAlarm="com.todolist.aladdalo.todolist.intent.action.ALARM";
    private static final String IDTaskName = "TaskName";
    private static final String IDTaskDate = "TaskDate";
    private static final String IDTask = "IDTask";

    /**
     * Constructeur
     */
    public ToDoAlarm(){

        Log.d("Todo_"+this.toString(),"new ManageAlarm");

      /*  //Intent intent=new Intent();

        intent.putExtra(IDTaskName,taskName); //share taskname in intent

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
        //Log.d("Todo_"+this.toString(),"Alarm to:"+sdf.format(calendar.getTime()));*/


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

    //to link task with pending intent
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

    /**
     * retire une alarme
     * @param context
     * @param taskName nom de la tache
     * @param intent
     * @param idTask id de la tache
     */
    public void removeAlarm(Context context,String taskName,Intent intent,int idTask) {


        //Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, idTask, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        //SharedPreferences.Editor sPf = prefs.edit();
        Log.d("Todo_" + this.toString(), "rem:" + taskName + ":" + idTask);
        //sPf.remove(taskName);
        //sPf.commit();



    }

    /**
     * retire toutes les alarmes
     * @param context
     * @param taskName nom de la tache
     * @param intent
     */
    public void removeAllAlarm(Context context,String taskName,Intent intent){
        /*PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int)calendar.getTimeInMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), pendingIntent);*/

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor sPf=prefs.edit();

        sPf.clear();
        sPf.commit();
    }


    /**
     * Ajoute une alarme a une tache
     * @param act si act= 0:mais pas la notif prévenant que l'alarme est bien activée 1:sinon
     * @param context
     * @param idTask id de la tache
     * @param taskName nom de la tache
     * @param intent
     * @param taskDate date de la deadline de la tache
     * @param time heure et min de la deadline
     */
    public void addAlarm(int act , Context context,int idTask,String taskName,Intent intent, int taskDate,int time){
        //Intent intent=new Intent();

        intent.putExtra(IDTaskName,taskName); //share taskname in intent

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
        if(act==1){
            //new ToDoNotif(0,context, taskName,dateTime,idTask);
            Toast.makeText(context, "Alarm activated", Toast.LENGTH_SHORT).show();
        }


        intent.putExtra(IDTaskDate,dateTime);
        intent.putExtra(IDTask,idTask);
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
        getPendingID(context,taskName);
        getAllKeyID(context);

        /*removeAlarm(context,taskName, intent);
        getAllKeyID(context);
        addAlarm(context,idTask,taskName,intent,calendar);
        getAllKeyID(context);*/

        //Toast.makeText(context, "Alarm to:"+i, Toast.LENGTH_LONG).show();
        //SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        //Log.d("Todo_"+this.toString(),"Alarm to:"+sdf.format(calendar.getTime()));




        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, idTask, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), pendingIntent);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor sPf=prefs.edit();

        sPf.putInt(taskName,idTask);
        sPf.commit();
    }

    /**
     * Pour remettre les alarmes des taches qui avaient l'alarme "true" au moment ou l'on recrée un nouvelle intent
     * @param intent
     * @param context
     */
    public void restoreAlarm(Intent intent,Context context){
        List<Task> tasks;
        tasks = Select.from(Task.class)
                .where(Condition.prop("alarme").eq(1))
                .list();
        for(Task t : tasks){
            addAlarm(0,context,t.getId().intValue(),t.getTaskName(),intent,(int)t.getDate(),(int)t.getTime());

            Log.d("Todo_"+this.toString(),t.getTaskName()+":remettreAlarme");
        }
    }



}
