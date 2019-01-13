package com.todolist.aladdalo.todolist;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Vibrator;
import android.util.Log;

import com.todolist.aladdalo.todolist.db.SousTache;
import com.todolist.aladdalo.todolist.db.Task;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Classe pour gérer des notifications
 */
public class ToDoNotif {


    public int ID_NOTIFICATION = 0;

    public int IDT=100;


    /**
     * Permet de creer une notif
     * @param state si 0: notif au moment de la mise en route d'une alerte, si 1: notif au moment de la deadline
     * @param context
     * @param taskName nom de la tache
     * @param taskDate date et heure de la tache
     * @param idTask id de la tache
     */
    public ToDoNotif(int state,Context context, String taskName, String taskDate,int idTask){
        String stateStr="";
        String commentStr="";
        if(state==0){
            stateStr="Alarm Start:";
            commentStr="Let's go";
        }else if(state==1){
            stateStr="Tâche finis:"+taskDate;
            //commentStr=taskDate+":";
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
                .setSmallIcon(R.drawable.end_task)
                .setContentTitle(stateStr)
                .setContentText(commentStr+taskName
                        )
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

    /**
     * Desactive l'alarme de la tache une fois que la deadline est arrivee
     * @param taskID
     */
    public void alarmdesactivated(int taskID){
        Log.d("Todo_"+this.toString(),"chiffre:"+taskID);
        if(taskID>IDT){
            Task task = Task.findById(Task.class,taskID-IDT);


            task.setAlarme(false);
            task.save();

            Log.d("Todo_"+this.toString(),taskID+":desacAlarme|"+task.getAlarme());
        }else{
            SousTache task = SousTache.findById(SousTache.class, taskID);


            task.setAlarme(false);
            task.save();

            Log.d("Todo_"+this.toString(),taskID+":desacAlarme|"+task.getAlarme());
        }

    }

}
