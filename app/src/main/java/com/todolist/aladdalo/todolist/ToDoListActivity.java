package com.todolist.aladdalo.todolist;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.todolist.aladdalo.todolist.db.AccountLauncher;
import com.todolist.aladdalo.todolist.db.AuthenticatorService;
import com.todolist.aladdalo.todolist.db.OnlineDatabase;

import com.orm.query.Condition;
import com.orm.query.Select;

import com.todolist.aladdalo.todolist.db.Task;

import java.util.Calendar;
import java.util.List;

public class ToDoListActivity extends AppCompatActivity implements
        View.OnClickListener {

    // private static final String TAG = "ToDoListActivity";


    //private TaskDbHelper mHelper;
    private ListView mTaskListView;

    private TaskAdapter mAdapter;

    private EditText txtDate, txtTime;

    private TabLayout tabs;
    private Task task;

    DatePickerDialog.OnDateSetListener datePicker;
    final Calendar c = Calendar.getInstance();

    private int mYear=0, mMonth=0, mDay=0, mHour=0, mMinute=0;

    /**true pour trier par date, false par priorité*/
    private boolean enCours = true;

    LinearLayout linearLayout;
    EditText taskEditText;
    EditText progressEditText;
    RadioButton faible;
    RadioButton moyenne;
    RadioButton forte;
    CheckBox alarmeCheck;

    //pour que les alarmes et notif soient liées a un seul intent pour simplifier
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_to_do_list);
        mTaskListView = (ListView) findViewById(R.id.list_todo);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //mHelper = new TaskDbHelper(this);
        mTaskListView = (ListView) findViewById(R.id.list_todo);

        tabs = (TabLayout) findViewById(R.id.tabs);

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: updateUI(true);
                        break;
                    case 1: updateUI(false);
                        break;


                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        refreshList();

        intent=new Intent();
    }

    private void updateUI(boolean orderBy) {

        //List<Task> tasks = Task.listAll(Task.class);
        List<Task> tasks;



        if(enCours){/*affiche les taches en cours*/
            if(orderBy){

                tasks = Select.from(Task.class)
                        .where(Condition.prop("priority").notEq(0))
                        .orderBy("date")
                        .list();
                System.out.println("PREMIER CAS");
            }
            else{
                tasks = Select.from(Task.class)
                        .where(Condition.prop("priority").notEq(0))
                        .orderBy("priority desc")
                        .list();
                System.out.println("DEUXIEME CAS");
            }

        }
        else{/*affiche les taches terminées*/
            tasks = Select.from(Task.class)
                    .where(Condition.prop("priority").eq(0))
                    .orderBy("date")
                    .list();
            System.out.println("TROISIEME CAS");

        }


        //List<Task> tasks = SugarRecord.listAll(Task.class);
       /* for(Task task : tasks){
            //taskList.add(task.getTaskName());
            System.out.println(task.getId() + " : " + task.getTaskName() + ", date : " + task.getDate());
        }*/


        if (mAdapter == null) {
            mAdapter = new TaskAdapter(this,
                    R.layout.item_task, // what view to use for the items
                    R.id.task_title, // where to put the String of data
                    tasks); // where to get all the data
            mTaskListView.setAdapter(mAdapter); // set it as the adapter of the ListView instance
        } else {
            mAdapter.clear();
            mAdapter.addAll(tasks);
            mAdapter.notifyDataSetChanged();
        }

    }

    public void refreshList(){
        if(tabs.getTabAt(0).isSelected()){
            updateUI(true);
        }
        else{
            updateUI(false);
        }
    }

    public void deleteTask(Task task) {
       // View parent = (View) view.getParent();
        //TextView taskTextView = (TextView) parent.findViewById(R.id.task_title);
        //TextView taskTextView = (TextView) parent.findViewById(R.id.task_id);
        //int taskId = Integer.valueOf(String.valueOf(taskTextView.getText()));
        //System.out.println("--------------id : " + taskId);
       // task = Task.findById(Task.class, taskId);
        task.delete();

        refreshList();
    }

    public void finishTask(View view){
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.task_id);
        int taskId = Integer.valueOf(String.valueOf(taskTextView.getText()));
        System.out.println("--------------id : " + taskId);
        task = Task.findById(Task.class, taskId);
        task.setPriority(Priorite.Fini);
        System.out.println("--------------PRIORITE : " + task.getPriority());
        task.save();

        refreshList();
    }

    public void afficheParam(View view){
        //TODO Affiche une page ou l'on peu modifier les params de la tache (premières lignes identique a deleteTask pour trouver la tache)
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.task_id);
        int taskId = Integer.valueOf(String.valueOf(taskTextView.getText()));
        task = Task.findById(Task.class, taskId);
        //Log.d("Todo_"+this.toString(),"aff:"+taskId+":"+task.getTaskName());
        createTaskLayout(task.getTaskName(), task.getDateString(), task.getTimeString(), task.getPriority(),task.getProgress());

        createTaskLayout(task.getTaskName(), task.getDateString(), task.getTimeString(), task.getPriority(),task.getAlarme());

        /*---------------------------------------
        Crée l'AlertDialog pour l'édition de tâche
        ----------------------------------------*/

        //Creation du AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.modif_tache)
                .setMessage(R.string.faire_ensuite)
                .setView(linearLayout)
                .setNeutralButton("Supprimer", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteTask(task);
                    }
                })

                .setPositiveButton(R.string.modifier, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int progress = 0;
                        if(!progressEditText.getText().toString().equals("")){
                            progress = Integer.parseInt(progressEditText.getText().toString())%100;
                        }
                        if(!taskEditText.getText().toString().equals("")){
                            int date;
                            int time;
                            String taskName = String.valueOf(taskEditText.getText());

                            if(txtDate.getText().toString().equals("")){ //si pas de date (peu importe si heure)
                                task.setTaskName(taskName);
                            }
                            else{
                                //if(mDay != 0)
                                //mMonth++;//car commence à 0
                                date = mYear*10000 + mMonth * 100 + mDay ;
                                time = 10000 + mHour*100 + mMinute;
                                task.setTaskName(taskName);
                                task.setDate(date);
                                task.setTime(time);
                            }

                            if(faible.isChecked()){
                                task.setPriority(Priorite.Faible);
                            }
                            if(moyenne.isChecked()){
                                task.setPriority(Priorite.Moyenne);
                            }
                            if(forte.isChecked()) {
                                task.setPriority(Priorite.Forte);
                            }

                            task.setProgress(progress);
                            task.save();
                            refreshList();
                        }


                    }
                })
                .setNegativeButton(R.string.annuler, null)
                .create();


        dialog.show();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_add_task:
                addnewtask();
                return true;
            case R.id.afficher_cacher_prio0:
                enCours = !enCours;
                refreshList();
                return true;


            case R.id.action_authenticate:
                //AccountLauncher.authenticate(this);
                OnlineDatabase o = new OnlineDatabase(this);
                o.getInfos("email.lll@gmail.com","azerty");
                return true;

            /*case R.id.afficher_prio0:
                updateUIPrio0();
                return true;*/

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createTaskLayout(String taskDefault, String dateDefault, String timeDefault, int priorityDefault,int progress){
    private void createTaskLayout(String taskDefault, String dateDefault, String timeDefault, int priorityDefault,boolean alarme){
        /*---------------------------------------
        Crée le layout pour la création de tâche
        ----------------------------------------*/

        /*Nom de la tâche*/
        final EditText taskEditText = new EditText(this);
        taskEditText.setHint(R.string.desc_tache);
        taskEditText.setText(taskDefault);

        //EditText pour la date
        txtDate= new EditText(this);
        txtDate.setWidth(400);
        txtDate.setHint(R.string.format_date);
        txtDate.setText(dateDefault);
        txtDate.setFocusable(false);
        txtDate.setClickable(true);

        //EditText pour l'heure
        txtTime=new EditText(this);
        txtTime.setWidth(400);
        txtTime.setHint(R.string.format_heure);
        txtTime.setText(timeDefault);
        txtTime.setFocusable(false);
        txtTime.setClickable(true);


        //listener des EditText
        txtDate.setOnClickListener(this);
        txtTime.setOnClickListener(this);

        /*TextView pour priorité*/
        final TextView textPriority = new TextView(this);
        textPriority.setText(R.string.txtPriorite);
        textPriority.setPadding(0,10,0,0);

        /*RadioGroup pour la priorité*/
        RadioGroup priority = new RadioGroup(this);
        priority.setOrientation(LinearLayout.HORIZONTAL);

        /*Les RadioButton du RadioGroup*/
        final RadioButton faible = new RadioButton(this);
        faible.setText(R.string.prioFaible);
        final RadioButton moyenne = new RadioButton(this);
        moyenne.setText(R.string.prioMoyenne);
        final RadioButton forte = new RadioButton(this);
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
        final TextView textProgress = new TextView(this);
        textProgress.setText(R.string.desc_progress);
        textProgress.setPadding(0,10,0,0);
        final EditText progressEditText = new EditText(this);
        progressEditText.setHint(R.string.progress_hint);
        progressEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        progressEditText.setText(String.valueOf(progress));

        /*Checkbox pour activer ou non l'alarme*/
        final TextView textAlarme=new TextView(this);
        textAlarme.setText("Alarme active:");

        final CheckBox alarmeCheck = new CheckBox(this);
        alarmeCheck.setChecked(alarme);


        //Layout pour organiser l'AlerteDialog
        final LinearLayout linearLayout = new LinearLayout(this);
        final LinearLayout date = new LinearLayout(this);
        final LinearLayout time = new LinearLayout(this);

        date.setOrientation(LinearLayout.HORIZONTAL);
        date.addView(txtDate);

        time.setOrientation(LinearLayout.HORIZONTAL);
        time.addView(txtTime);

        linearLayout.addView(taskEditText);
        linearLayout.addView(date);
        linearLayout.addView(time);
        linearLayout.addView(textPriority);
        linearLayout.addView(priority);
        linearLayout.addView(textProgress);
        linearLayout.addView(progressEditText);
        linearLayout.addView(textAlarme);
        linearLayout.addView(alarmeCheck);
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

    /**Créé et gère l'AlertDialog lors de la création de tâche**/
    public void addnewtask(){

        createTaskLayout("","","", 1,0);
        createTaskLayout("","","", 1,false);

        /*---------------------------------------
        Crée l'AlertDialog pour la création de tâche
        ----------------------------------------*/

        //Creation du AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.ajout_tache)
                .setMessage(R.string.faire_ensuite)
                .setView(linearLayout)
                .setPositiveButton(R.string.ajouter, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int progress = 0;
                        if(!progressEditText.getText().toString().equals("")){
                            progress = Integer.parseInt(progressEditText.getText().toString())%100;
                        }
                        if(!taskEditText.getText().toString().equals("")){//si intitule de
                            Task task;
                            int date;
                            int time;
                            boolean alarmeBool;
                            String taskName = String.valueOf(taskEditText.getText());

                            if(txtDate.getText().toString().equals("")){ //si pas de date (peut importe si heure)
                                //task = new Task(taskName);

                                alarmeBool=false;

                                task = new Task(taskName,alarmeBool);
                                task = new Task(taskName,progress);
                            }
                            else{
                                //if(mDay != 0)
                                // mMonth++;//car commence à 0
                                date = mYear*10000 + mMonth * 100 + mDay ;
                                time = 10000 + mHour*100 + mMinute;
                                alarmeBool=alarmeCheck.isChecked();

                                //task = new Task(taskName, date, time);
                                task = new Task(taskName, date, time, alarmeBool);
                                Log.d("Todo_" + this.toString(), "alarm:" + alarmeCheck.isChecked());

                                task = new Task(taskName, date, time,progress);
                                //TODO : mettre le string de la tache + heure + date dans BDD
                            }

                            if(faible.isChecked()){
                                task.setPriority(Priorite.Faible);
                            }
                            if(moyenne.isChecked()){
                                task.setPriority(Priorite.Moyenne);
                            }
                            if(forte.isChecked()) {
                                task.setPriority(Priorite.Forte);
                            }







                            task.save();

                            refreshList();

                            if(task.getAlarme()) {
                                new ToDoAlarm(ToDoListActivity.this.getApplicationContext(), task.getTaskName(), (int)task.getDate(), (int)task.getTime(), intent, task.getId().intValue());//Start alarm

                                //Log.d("Todo_" + this.toString(), "get:" + task.getId().intValue() + ":" + task.getTaskName());
                            }
                        }


                    }
                })
                .setNegativeButton(R.string.annuler, null)
                .create();


        dialog.show();
    }


    @Override
    public void onClick(View v) {

        if (v == txtDate) {
            datePicker = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    //Recupere la date actuelle
                    mYear = year;
                    mMonth = monthOfYear+1;
                    mDay = dayOfMonth;

                    txtDate.setText(String.format(getResources().getString(R.string.date), dayOfMonth, (monthOfYear + 1), year));
                }
            };

            new DatePickerDialog(this, datePicker, c
                    .get(Calendar.YEAR), c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)).show();

        }
        if (v == txtTime) {

            // Lance le Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            mHour = hourOfDay;
                            mMinute = minute;

                            txtTime.setText(String.format(getResources().getString(R.string.heure),hourOfDay,minute));

                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }

    }

}
