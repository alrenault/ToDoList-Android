package com.todolist.aladdalo.todolist;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.todolist.aladdalo.todolist.db.Task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateTaskLayout {

    ToDoListActivity activity;

    private EditText txtDate, txtTime;
    private LinearLayout linearLayout;
    private EditText taskEditText;
    private EditText progressEditText;
    private RadioButton faible;
    private RadioButton moyenne;
    private RadioButton forte;
    private CheckBox alarmeCheck;
    private RadioGroup priority;


    public CreateTaskLayout(ToDoListActivity activity){
        this.activity = activity;
    }



        public void createTaskLayout(String taskDefault, String dateDefault, String timeDefault, int priorityDefault,int progress, boolean alarme){
        /*---------------------------------------
        Crée le layout pour la création de tâche
        ----------------------------------------*/

        /*Nom de la tâche*/
        final EditText taskEditText = new EditText(activity);
        taskEditText.setHint(R.string.desc_tache);
        taskEditText.setText(taskDefault);
        taskEditText.setMaxHeight(200);

        //EditText pour la date
        txtDate= new EditText(activity);
        txtDate.setWidth(400);
        txtDate.setHint(R.string.format_date);
        txtDate.setText(dateDefault);
        txtDate.setFocusable(false);
        txtDate.setClickable(true);

        //EditText pour l'heure
        txtTime=new EditText(activity);
        txtTime.setWidth(400);
        txtTime.setHint(R.string.format_heure);
        txtTime.setText(timeDefault);
        txtTime.setFocusable(false);
        txtTime.setClickable(true);


        //listener des EditText
        txtDate.setOnClickListener(activity);
        txtTime.setOnClickListener(activity);

        /*TextView pour priorité*/
        final TextView textPriority = new TextView(activity);
        textPriority.setText(R.string.txtPriorite);
        textPriority.setPadding(0,10,0,0);

        /*RadioGroup pour la priorité*/
        RadioGroup priority = new RadioGroup(activity);
        priority.setOrientation(LinearLayout.HORIZONTAL);

        /*Les RadioButton du RadioGroup*/
        final RadioButton faible = new RadioButton(activity);
        faible.setText(R.string.prioFaible);
        //faible.setChecked(true);
        final RadioButton moyenne = new RadioButton(activity);
        moyenne.setText(R.string.prioMoyenne);
        final RadioButton forte = new RadioButton(activity);
        forte.setText(R.string.prioForte);

        priority.addView(faible);
        priority.addView(moyenne);
        priority.addView(forte);

        switch (priorityDefault){
            case 1 : priority.check(faible.getId());
                break;
            case 2 : priority.check(moyenne.getId());
                break;
            case 3 : priority.check(forte.getId());
                break;
            default : priority.check(faible.getId());
        }
        //----------------------------

        //Progression
        final TextView textProgress = new TextView(activity);
        textProgress.setText(R.string.desc_progress);
        textProgress.setPadding(0,10,0,0);
        final EditText progressEditText = new EditText(activity);
        progressEditText.setHint(R.string.progress_hint);
        progressEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        progressEditText.setText(String.valueOf(progress));

        /*Checkbox pour activer ou non l'alarme*/
        final TextView textAlarme=new TextView(activity);
        textAlarme.setText(R.string.alarme);
        textAlarme.setPadding(120,10,0,0);

        final CheckBox alarmeCheck = new CheckBox(activity);
        alarmeCheck.setChecked(alarme);

        LinearLayout.LayoutParams layoutParamsCheck = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsCheck.setMargins(120,0,0,0);


        //Layout pour organiser l'AlerteDialog
        final LinearLayout linearLayout = new LinearLayout(activity);
        final LinearLayout date = new LinearLayout(activity);
        final LinearLayout time = new LinearLayout(activity);

        date.setOrientation(LinearLayout.HORIZONTAL);
        date.addView(txtDate);

        time.setOrientation(LinearLayout.HORIZONTAL);
        time.addView(txtTime);

        final LinearLayout progressLayout = new LinearLayout(activity);
        final LinearLayout alarmeLayout = new LinearLayout(activity);
        final LinearLayout progressAlarm = new LinearLayout(activity);

        progressLayout.addView(textProgress);
        progressLayout.addView(progressEditText);
        progressLayout.setOrientation(LinearLayout.VERTICAL);
        alarmeLayout.addView(textAlarme);
        alarmeLayout.addView(alarmeCheck,layoutParamsCheck);
        alarmeLayout.setOrientation(LinearLayout.VERTICAL);

        progressAlarm.setOrientation(LinearLayout.HORIZONTAL);
        progressAlarm.addView(progressLayout);
        progressAlarm.addView(alarmeLayout);

        linearLayout.addView(taskEditText);
        linearLayout.addView(date);
        linearLayout.addView(time);
        linearLayout.addView(textPriority);
        linearLayout.addView(priority);
        linearLayout.addView(progressAlarm);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        /*Définir la bonne taille pour le champ taskEditText*/
        ViewGroup.LayoutParams params = taskEditText.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        taskEditText.setLayoutParams(params);

        /*---------------------------------------
             Fin de la création du layout
        ----------------------------------------*/

        this.linearLayout = linearLayout;
        this.taskEditText = taskEditText;
        this.progressEditText = progressEditText;
        this.faible = faible;
        this.moyenne = moyenne;
        this.forte = forte;
        this.alarmeCheck=alarmeCheck;
    }

//    public void createTaskLayout(String taskDefault, String dateDefault, String timeDefault, int priorityDefault,int progress, boolean alarme){
//        /*---------------------------------------
//        Crée le layout pour la création de tâche
//        ----------------------------------------*/
//
//        /*Nom de la tâche*/
//        taskEditText.setText(taskDefault);
//
//        //EditText pour la date
//        txtDate.setText(dateDefault);
//
//
//        //EditText pour l'heure
//        txtTime.setText(timeDefault);
//
//        /*RadioGroup pour la priorité*/
//        switch (priorityDefault){
//            case 1 : priority.check(faible.getId());
//                break;
//            case 2 : priority.check(moyenne.getId());
//                break;
//            case 3 : priority.check(forte.getId());
//                break;
//            default : priority.check(faible.getId());
//        }
//        //----------------------------
//
//        //Progression
//        progressEditText.setText(String.valueOf(progress));
//
//
//        alarmeCheck.setChecked(alarme);
//
//        /*---------------------------------------
//             Fin de la création du layout
//        ----------------------------------------*/
//
////        activity.setTxtDate(txtDate);
////        activity.setTxtTime(txtTime);
////        activity.setLinearLayout(linearLayout);
////        activity.setTaskEditText(taskEditText);
////        activity.setProgressEditText(progressEditText);
////        activity.setFaible(faible);
////        activity.setMoyenne(moyenne);
////        activity.setForte(forte);
////        activity.setAlarmeCheck(alarmeCheck);
//    }

    public ToDoListActivity getActivity() {
        return activity;
    }

    public void setActivity(ToDoListActivity activity) {
        this.activity = activity;
    }

    public EditText getTxtDate() {
        return txtDate;
    }

    public void setTxtDate(EditText txtDate) {
        this.txtDate = txtDate;
    }

    public EditText getTxtTime() {
        return txtTime;
    }

    public void setTxtTime(EditText txtTime) {
        this.txtTime = txtTime;
    }

    public LinearLayout getLinearLayout() {
        return linearLayout;
    }

    public void setLinearLayout(LinearLayout linearLayout) {
        this.linearLayout = linearLayout;
    }

    public EditText getTaskEditText() {
        return taskEditText;
    }

    public void setTaskEditText(EditText taskEditText) {
        this.taskEditText = taskEditText;
    }

    public EditText getProgressEditText() {
        return progressEditText;
    }

    public void setProgressEditText(EditText progressEditText) {
        this.progressEditText = progressEditText;
    }

    public RadioButton getFaible() {
        return faible;
    }

    public void setFaible(RadioButton faible) {
        this.faible = faible;
    }

    public RadioButton getMoyenne() {
        return moyenne;
    }

    public void setMoyenne(RadioButton moyenne) {
        this.moyenne = moyenne;
    }

    public RadioButton getForte() {
        return forte;
    }

    public void setForte(RadioButton forte) {
        this.forte = forte;
    }

    public CheckBox getAlarmeCheck() {
        return alarmeCheck;
    }

    public void setAlarmeCheck(CheckBox alarmeCheck) {
        this.alarmeCheck = alarmeCheck;
    }

    public RadioGroup getPriority() {
        return priority;
    }

    public void setPriority(RadioGroup priority) {
        this.priority = priority;
    }
}
