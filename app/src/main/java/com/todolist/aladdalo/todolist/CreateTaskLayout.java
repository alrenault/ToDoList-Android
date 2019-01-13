package com.todolist.aladdalo.todolist;

import android.text.InputType;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * Classe créant l'alertDialog lors de la création ou la modification d'une tâche
 */
public class CreateTaskLayout {

    /**Activité qui représente le contexte */
    private ToDoListActivity activity;

    /**EditText pour l'intitulé de la tâche*/
    private EditText taskEditText;

    /**EditText pour la date d'une tâche */
    private EditText txtDate;

    /**EditText pour l'heure d'une tâche*/
    private EditText txtTime;

    /**EditText pour la progression d'une tâche*/
    private EditText progressEditText;

    /**RadioGroup pour la gestion de la priorité*/
    private RadioGroup priority;

    /**Si coché, la priorité de la tâche sera faible*/
    private RadioButton faible;

    /**Si coché, la priorité de la tâche sera moyenne*/
    private RadioButton moyenne;

    /**Si coché, la priorité de la tâche sera forte*/
    private RadioButton forte;

    /**CheckBox pour activer une alarme*/
    private CheckBox alarmeCheck;

    /**Le layout final*/
    private LinearLayout linearLayout;


    public CreateTaskLayout(ToDoListActivity activity){
        this.activity = activity;
    }


    /**
     * Création du layout de l'alertDialog
     * @param taskDefault L'intitulé de la tâche
     * @param dateDefault La date de la deadline de la tâche
     * @param timeDefault L'heure de la deadline de la tâche
     * @param priorityDefault La priorité de la tâche
     * @param progress La progression de la tâche
     * @param alarme Si une alarme est activé ou non
     */
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
        priority = new RadioGroup(activity);
        priority.setOrientation(LinearLayout.HORIZONTAL);

        /*Les RadioButton du RadioGroup*/
        faible = new RadioButton(activity);
        faible.setText(R.string.prioFaible);
        //faible.setChecked(true);
        moyenne = new RadioButton(activity);
        moyenne.setText(R.string.prioMoyenne);
        forte = new RadioButton(activity);
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
        this.alarmeCheck=alarmeCheck;
    }



    public EditText getTxtDate() {
        return txtDate;
    }

    public EditText getTxtTime() {
        return txtTime;
    }

    public LinearLayout getLinearLayout() {
        return linearLayout;
    }

    public EditText getTaskEditText() {
        return taskEditText;
    }

    public EditText getProgressEditText() {
        return progressEditText;
    }

    public RadioButton getFaible() {
        return faible;
    }

    public RadioButton getMoyenne() {
        return moyenne;
    }

    public RadioButton getForte() {
        return forte;
    }

    public CheckBox getAlarmeCheck() {
        return alarmeCheck;
    }

    public RadioGroup getPriority() {
        return priority;
    }

}
