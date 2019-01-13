package com.todolist.aladdalo.todolist;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.orm.SchemaGenerator;
import com.orm.SugarContext;
import com.orm.SugarDb;;
import com.todolist.aladdalo.todolist.db.AccountLauncher;
import com.todolist.aladdalo.todolist.db.OnlineDatabase;

import com.orm.query.Condition;
import com.orm.query.Select;

import com.todolist.aladdalo.todolist.db.Task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Classe créant l'activité prinipale de l'application où l'on peut voir toutes les taĉhes
 * @author RENAULT Alexis, BREGEON Dany, MONET Lois, PITROU Adrien
 */
public class ToDoListActivity extends AppCompatActivity implements
        View.OnClickListener {

    /**La listeView permettant d'afficher les tâches sous forme de liste */
    private ListView mTaskListView;

    /**L'adaptateur pour utiliser la base de données interne*/
    private TaskAdapter mAdapter;

    /**Tableau d'onglet de l'application*/
    private TabLayout tabs;

    /**Tache sur laquelle on travaille actuellement*/
    private Task task;

    /**DatePicker pour la création/modification de tache*/
    DatePickerDialog.OnDateSetListener datePicker;

    /**Calendrier pour la création/modification de tâche*/
    final Calendar c = Calendar.getInstance();

    /**Variable pour l'utilisation du temps*/
    private int mYear=0, mMonth=0, mDay=0, mHour=0, mMinute=0;

    /**true pour trier par date, false par priorité*/
    private boolean enCours = true;

    /**Pour que les alarmes et notif soient liées a un seul intent pour simplifier*/
    Intent intent;

    /**Pour gérer l'alarm d'une tâche*/
    ToDoAlarm tDA;

    /**Layout de l'alertDialog pour la création/modification d'une tâche*/
    CreateTaskLayout addModTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addModTask = new CreateTaskLayout(this);

        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm",Locale.FRANCE);
        Date dateReset = new Date();

        mYear = Integer.valueOf(format.format(dateReset).substring(6,10));
        mMonth = Integer.valueOf(format.format(dateReset).substring(3,5));
        mDay = Integer.valueOf(format.format(dateReset).substring(0,2));
        mHour = Integer.valueOf(format.format(dateReset).substring(11,13));
        mMinute = Integer.valueOf(format.format(dateReset).substring(14,16));

        // create table if not exists
        SugarContext.init(getApplicationContext());
        SchemaGenerator schemaGenerator = new SchemaGenerator(this);
        schemaGenerator.createDatabase(new SugarDb(this).getDB());

        //vue
        setContentView(R.layout.activity_to_do_list);
        mTaskListView = (ListView) findViewById(R.id.list_todo);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

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

        tDA=new ToDoAlarm();

        tDA.restoreAlarm(intent,ToDoListActivity.this.getApplicationContext());


    }

    /**
     * Permet de mettre à jour l'affichage de l'activité à chaque appel
     * @param orderBy Permet de savoir quel ordre utiliser
     */
    private void updateUI(boolean orderBy) {

        List<Task> tasks;



        if(enCours){/*affiche les taches en cours*/
            if(orderBy){

                tasks = Select.from(Task.class)
                        .where(Condition.prop("priority").notEq(0))
                        .orderBy("date")
                        .list();
            }
            else{
                tasks = Select.from(Task.class)
                        .where(Condition.prop("priority").notEq(0))
                        .orderBy("priority desc")
                        .list();
            }

        }
        else {/*affiche les taches terminées*/
            tasks = Select.from(Task.class)
                    .where(Condition.prop("priority").eq(0))
                    .orderBy("date")
                    .list();

        }


        if (mAdapter == null) {
            mAdapter = new TaskAdapter(this,
                    R.layout.item_task, // what view to use for the items
                    R.id.task_title, // where to put the String of data
                    tasks); // where to get all the data
            mTaskListView.setAdapter(mAdapter); // set it as the adapter of the ListView instance

            mTaskListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                               int index, long arg3) {
                    String str="Tâche envoyée:"+ ((Task)mTaskListView.getItemAtPosition(index)).getTaskName();
                    //String str="Tâche envoyée:"+ Task.findById(Task.class,index+1).getTaskName();
                    //Toast.makeText(getApplicationContext(), "partager:"+str, Toast.LENGTH_SHORT).show();

                    Intent ShareIntent = new Intent(Intent.ACTION_SEND);
                    ShareIntent.setType("text/plain");
                    ShareIntent.putExtra(Intent.EXTRA_TEXT, str);
                    startActivity(Intent.createChooser(ShareIntent, "Partager avec:"));
                    return true;
                }
            });

        } else {
            mAdapter.clear();
            mAdapter.addAll(tasks);
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Permet d'appeller updateUI en fonction de l'onglet en cours
     */
    public void refreshList(){

        if(tabs.getTabAt(0).isSelected()){
            updateUI(true);
        }
        else{
            updateUI(false);
        }


    }

    /**
     * Supprime une tâche
     * @param task La tâche à supprimer
     */
    public void deleteTask(Task task) {
        task.delete();

        refreshList();
    }

    /**
     * Fini une tâche en cours
     * @param view La vue correspondant à la tâche
     */
    public void finishTask(View view){
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.task_id);
        int taskId = Integer.valueOf(String.valueOf(taskTextView.getText()));
        task = Task.findById(Task.class, taskId);
        task.setPriority(Priorite.Fini);
        task.setProgress(100);
        task.save();

        refreshList();
    }

    /**
     * Permet d'afficher l'arlertDialog pour la modification des paramètres d'une tâche
     * @param view La vue correspondant à la tâche
     */
    public void afficheParam(View view){
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.task_id);
        int taskId = Integer.valueOf(String.valueOf(taskTextView.getText()));
        task = Task.findById(Task.class, taskId);

        Log.d("Todo_"+this.toString(),"aff:"+taskId+":"+task.getTaskName()+"|"+task.getAlarme());

        addModTask.createTaskLayout(task.getTaskName(), task.getDateString(), task.getTimeString(), task.getPriority(),task.getProgress(), task.getAlarme());

        /*---------------------------------------
        Crée l'AlertDialog pour l'édition de tâche
        ----------------------------------------*/

        //Creation du AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.modif_tache)
                .setMessage(R.string.faire_ensuite)
                .setView(addModTask.getLinearLayout())
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
                        if(!addModTask.getProgressEditText().getText().toString().equals("")){
                            progress = Integer.parseInt(addModTask.getProgressEditText().getText().toString())%100;
                        }
                        if(!addModTask.getTaskEditText().getText().toString().equals("")){

                            int date;
                            int time;
                            String taskName = String.valueOf(addModTask.getTaskEditText().getText());

                            if(addModTask.getTxtDate().getText().toString().equals("")){ //si pas de date (peu importe si heure)
                                task.setTaskName(taskName);
                            }
                            else{
                                String[] strDate = addModTask.getTxtDate().getText().toString().split("/");
                                String[] strTime = addModTask.getTxtTime().getText().toString().split(":");
                                String year;

                                if(strDate[2].length() != 4)
                                    year = "20"+strDate[2];
                                else
                                    year = strDate[2];

                                date = Integer.valueOf(year)*10000 + Integer.valueOf(strDate[1]) * 100 + Integer.valueOf(strDate[0]) ;
                                time = 10000 + Integer.valueOf(strTime[0])*100 + Integer.valueOf(strTime[1]);
                                task.setTaskName(taskName);
                                task.setDate(date);
                                task.setTime(time);


                                //Pour reset les vars
                                DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm",Locale.FRANCE);
                                Date dateReset = new Date();

                                mYear = Integer.valueOf(format.format(dateReset).substring(6,10));
                                mMonth = Integer.valueOf(format.format(dateReset).substring(3,5));
                                mDay = Integer.valueOf(format.format(dateReset).substring(0,2));
                                mHour = Integer.valueOf(format.format(dateReset).substring(11,13));
                                mMinute = Integer.valueOf(format.format(dateReset).substring(14,16));


                            }

                            if(addModTask.getFaible().isChecked()){
                                task.setPriority(Priorite.Faible);
                            }
                            if(addModTask.getMoyenne().isChecked()){
                                task.setPriority(Priorite.Moyenne);
                            }
                            if(addModTask.getForte().isChecked()) {
                                task.setPriority(Priorite.Forte);
                            }

                            if(addModTask.getAlarmeCheck().isChecked() && !task.getAlarme()) {

                                task.setAlarme(true);
                                Log.d("Todo_"+this.toString(),"ajouterAlarme");
                                tDA.addAlarm(1,ToDoListActivity.this.getApplicationContext(),task.getId().intValue(),task.getTaskName(),intent,(int)task.getDate(),(int)task.getTime());
                                //new ToDoAlarm(ToDoListActivity.this.getApplicationContext(), task.getTaskName(), (int)task.getDate(), (int)task.getTime(), intent, task.getId().intValue());//Start alarm


                            }else if(!addModTask.getAlarmeCheck().isChecked() && task.getAlarme()){
                                task.setAlarme(false);
                                Log.d("Todo_"+this.toString(),"retirer alarme");
                                tDA.removeAlarm(ToDoListActivity.this.getApplicationContext(),task.getTaskName(),intent,task.getId().intValue());
                                //new ToDoAlarm(ToDoListActivity.this.getApplicationContext(), task.getTaskName(), (int)task.getDate(), (int)task.getTime(), intent, task.getId().intValue());//Start alarm


                            }else if(addModTask.getAlarmeCheck().isChecked() && task.getAlarme()){
                                Log.d("Todo_"+this.toString(),"Modify retirer alarme");
                                tDA.removeAlarm(ToDoListActivity.this.getApplicationContext(),task.getTaskName(),intent,task.getId().intValue());
                                Log.d("Todo_"+this.toString(),"Modify ajouterAlarme");
                                tDA.addAlarm(1,ToDoListActivity.this.getApplicationContext(),task.getId().intValue(),task.getTaskName(),intent,(int)task.getDate(),(int)task.getTime());

                            }
                            else{
                                Log.d("Todo_"+this.toString(),"don't modify alarm");
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

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(AccountLauncher.isAuthenticated()) {
            MenuItem saveTask = menu.findItem(R.id.action_sauvegarde_distant);
            MenuItem receiveTask = menu.findItem(R.id.action_sauvegarde_distant);
            saveTask.setVisible(false);
            receiveTask.setVisible(false);
        }

        /*if (menu instanceof MenuBuilder) {
            MenuBuilder builder = ((MenuBuilder) menu);
            builder.setOptionalIconsVisible(true);
        }*/
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        OnlineDatabase o= null;

        switch (item.getItemId()) {
            case R.id.action_add_task:
                addnewtask();
                return true;
            case R.id.afficher_cacher_prio0:
                enCours = !enCours;
                if(enCours){
                    item.setTitle(R.string.afficher_tache_fini);
                }
                else
                    item.setTitle(R.string.cacher_tache_fini);
                refreshList();
                return true;
            case R.id.action_sauvegarde_distant:
                o = new OnlineDatabase(this);
                o.writeTasks();
                return true;
            case R.id.action_recup_distant:
                o = new OnlineDatabase(this);
                o.fetchTasks();
                return true;
            case R.id.action_clear_accounts:
                AccountLauncher.clearAccounts(this);
                return true;
            case R.id.action_authenticate:
                o = new OnlineDatabase(this);
                //AccountLayout.addnewaccount(this, "utilisateurTest2.testComplet@gmail.com", "aaaaaa25");
                AccountLayout.checkPhoneAccounts(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**Créé et gère l'AlertDialog lors de la création de tâche**/
    public void addnewtask(){

        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm",Locale.FRANCE);
        Date date = new Date();

        String currentDate = format.format(date).substring(0,10);
        String currentHour = format.format(date).substring(11,16);

        addModTask.createTaskLayout("",currentDate,currentHour, 1,0, false);
        /*---------------------------------------
        Crée l'AlertDialog pour la création de tâche
        ----------------------------------------*/

        //Creation du AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.ajout_tache)
                .setMessage(R.string.faire_ensuite)
                .setView(addModTask.getLinearLayout())
                .setPositiveButton(R.string.ajouter, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int progress = 0;
                        if(!addModTask.getProgressEditText().getText().toString().equals("")){
                            progress = Integer.parseInt(addModTask.getProgressEditText().getText().toString())%100;
                        }
                        if(!addModTask.getTaskEditText().getText().toString().equals("")){//si intitule de
                            Task task;
                            int date;
                            int time;
                            boolean alarmeBool;
                            String taskName = String.valueOf(addModTask.getTaskEditText().getText());

                            if(addModTask.getTxtDate().getText().toString().equals("")){ //si pas de date (peut importe si heure)

                                alarmeBool=false;

                                task = new Task(taskName,progress, alarmeBool);
                            }
                            else{
                                date = mYear*10000 + mMonth * 100 + mDay ;
                                time = 10000 + mHour*100 + mMinute;
                                alarmeBool=addModTask.getAlarmeCheck().isChecked();


                                //task = new Task(taskName, date, time);
                                task = new Task(taskName, date, time, progress, alarmeBool);
                                Log.d("Todo_" + this.toString(), "alarm:" + addModTask.getAlarmeCheck().isChecked());

                            }

                            if(addModTask.getFaible().isChecked()){
                                task.setPriority(Priorite.Faible);
                            }
                            if(addModTask.getMoyenne().isChecked()){
                                task.setPriority(Priorite.Moyenne);
                            }
                            if(addModTask.getForte().isChecked()) {
                                task.setPriority(Priorite.Forte);
                            }

                            task.save();

                            refreshList();

                            if(task.getAlarme()) {
                                tDA.addAlarm(1,ToDoListActivity.this.getApplicationContext(),task.getId().intValue(),task.getTaskName(),intent,(int)task.getDate(),(int)task.getTime());
                                //new ToDoAlarm(ToDoListActivity.this.getApplicationContext(), task.getTaskName(), (int)task.getDate(), (int)task.getTime(), intent, task.getId().intValue());//Start alarm

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

        if (v == addModTask.getTxtDate()) {
            datePicker = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    //Recupere la date actuelle
                    mYear = year;
                    mMonth = monthOfYear+1;
                    mDay = dayOfMonth;

                    addModTask.getTxtDate().setText(String.format(getResources().getString(R.string.date), dayOfMonth, (monthOfYear + 1), year));
                }
            };

            new DatePickerDialog(this, datePicker, c
                    .get(Calendar.YEAR), c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)).show();

        }
        if (v == addModTask.getTxtTime()) {

            // Lance le Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            mHour = hourOfDay;
                            mMinute = minute;

                            addModTask.getTxtTime().setText(String.format(getResources().getString(R.string.heure),hourOfDay,minute));

                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }

    }

    @SuppressLint("RestrictedApi")
    public void refreshIcon(boolean verite) {
        ActionMenuItemView item = findViewById(R.id.action_authenticate);
        MenuItemImpl mii = item.getItemData();

        if(verite){
            //account.setActive(false);
            mii.setIcon(R.drawable.icon2);
        }else{
            //account.setActive(true);
            mii.setIcon(R.drawable.icon);
        }
    }



}
