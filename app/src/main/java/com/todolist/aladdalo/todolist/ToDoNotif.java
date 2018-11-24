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
import android.util.Log;
import android.widget.Toast;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;

public class ToDoNotif {
    public int ID_NOTIFICATION = 0;



    public ToDoNotif(Context context, String taskName, String taskDate){


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
                .setContentTitle("ToDo Alarm:"+taskDate)
                .setContentText(taskName+":\n"
                                +"You did it")
                .setContentIntent(contentIntent);

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(NOTIFICATION_SERVICE);


        notificationManager.notify(ID_NOTIFICATION, builder.build());

        Log.d("Todo_"+this.toString(),"ToDo Alarm:"+taskDate);
        Log.d("Todo_"+this.toString(),taskName+":"+"You did it");
    }
}
