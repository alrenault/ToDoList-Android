package com.todolist.aladdalo.todolist;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.orm.query.Condition;
import com.orm.query.Select;
import com.todolist.aladdalo.todolist.db.Task;

import java.util.List;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;

public class ToDoNotif {
    public int ID_NOTIFICATION = 0;



    public ToDoNotif(int state,Context context, String taskName, String taskDate,int idTask){
        String stateStr="";
        String commentStr="";
        if(state==0){
            stateStr="Alarm Start:";
            commentStr="Let's go";
        }else if(state==1){
            stateStr="Alarm dring:";
            commentStr="You did it";
        }

        //Notification
        Intent notificationIntent = new Intent(context, ToDoListActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        final PendingIntent contentIntent = PendingIntent.getActivity(context,
                (int)System.currentTimeMillis(), notificationIntent,
                PendingIntent.FLAG_ONE_SHOT);


        Notification.Builder builder = new Notification.Builder(context)
                .setWhen(System.currentTimeMillis())
                .setTicker("NotifToDoTask")
                .setSmallIcon(R.drawable.ic_add_box_black_24dp)
                .setContentTitle(stateStr+taskDate)
                .setContentText(taskName+":\n"
                        +commentStr)
                .setContentIntent(contentIntent);

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(NOTIFICATION_SERVICE);

        if(state==1){
            Log.d("Todo_"+this.toString(),"ToDo Alarm:finish");
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));

            alarmdesactivated(idTask);

        }else if(state==0){
            Log.d("Todo_"+this.toString(),"ToDo Alarm:start");
            builder.setDefaults(Notification.DEFAULT_SOUND);
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(500);
        }

        notificationManager.notify((int)System.currentTimeMillis(), builder.build());


    }

    //Désactive l'alarme de la tache une fois que la deadline est arrivée
    public void alarmdesactivated(int taskID){

        Task task = Task.findById(Task.class, taskID);


        task.setAlarme(false);
        task.save();

        Log.d("Todo_"+this.toString(),taskID+":desacAlarme|"+task.getAlarme());
    }

}
