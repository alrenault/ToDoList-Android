package com.todolist.aladdalo.todolist;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.orm.SchemaGenerator;
import com.orm.SugarContext;
import com.orm.SugarDb;;
import com.todolist.aladdalo.todolist.db.AccountLauncher;
import com.todolist.aladdalo.todolist.db.OnlineDatabase;

import com.orm.query.Condition;
import com.orm.query.Select;

import com.todolist.aladdalo.todolist.db.SousTache;
import com.todolist.aladdalo.todolist.db.Task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ToDoSousTacheListActivity extends AppCompatActivity implements
        View.OnClickListener {

    private ListView mTaskListView;



    private SousTaskAdapter mAdapterST;

    //0 si dans tache , else val de la sous tache choisie
    private int idTorSTChoisi=0;

    //1 go ST, 0 else
    private int STaskTap=0;

    private EditText txtDate, txtTime;

    private TabLayout tabs;
    private SousTache sousTask;


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

    //to manage alarm for a task
    ToDoAlarm tDA;


    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);

        Log.d("Todo_"+this.toString(),"SCreate");
        idTorSTChoisi=getIntent().getIntExtra("IDST",idTorSTChoisi);

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
        setTitle(Task.findById(Task.class,idTorSTChoisi).getTaskName());
        setContentView(R.layout.activity_to_do_list);
        mTaskListView = (ListView) findViewById(R.id.list_todo);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        tabs = (TabLayout) findViewById(R.id.tabs);

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: updateUIST(true);
                        break;
                    case 1: updateUIST(false);
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

        tDA.restoreAlarm(intent,ToDoSousTacheListActivity.this.getApplicationContext());

        STaskTap=0;



    }


    private void updateUIST(boolean orderBy) {

        List<SousTache> tasks;


        if(enCours){/*affiche les taches en cours*/
            if(orderBy){

                tasks = Select.from(SousTache.class)
                        .where(Condition.prop("priority").notEq(0),Condition.prop("idtask").eq(idTorSTChoisi))
                        .orderBy("date")
                        .list();
                System.out.println("PREMIER CAS");
            }
            else{
                tasks = Select.from(SousTache.class)
                        .where(Condition.prop("priority").notEq(0),Condition.prop("idtask").eq(idTorSTChoisi))
                        .orderBy("priority desc")
                        .list();
                System.out.println("DEUXIEME CAS");
            }

        }
        else {/*affiche les taches terminées*/
            tasks = Select.from(SousTache.class)
                    .where(Condition.prop("priority").eq(0),Condition.prop("idtask").eq(idTorSTChoisi))
                    .orderBy("date")
                    .list();
            System.out.println("TROISIEME CAS");

        }


        if (mAdapterST == null) {
            mAdapterST = new SousTaskAdapter(this,
                    R.layout.item_task, // what view to use for the items
                    R.id.task_title, // where to put the String of data
                    tasks); // where to get all the data
            mTaskListView.setAdapter(mAdapterST); // set it as the adapter of the ListView instance

            mTaskListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                               int index, long arg3) {
                    String str="Tâche envoyée:"+ ((SousTache)mTaskListView.getItemAtPosition(index)).getTaskName();
                    Intent ShareIntent = new Intent(Intent.ACTION_SEND);
                    ShareIntent.setType("text/plain");
                    ShareIntent.putExtra(Intent.EXTRA_TEXT, str);
                    startActivity(Intent.createChooser(ShareIntent, "Partager avec:"));
                    return true;
                }


            });



        } else {
            mAdapterST.clear();
            mAdapterST.addAll(tasks);
            mAdapterST.notifyDataSetChanged();
        }
    }

    public void refreshList(){

        if(tabs.getTabAt(0).isSelected()){
            if(STaskTap==1){
                STaskTap=0;
                // Le premier paramètre est le nom de l'activité actuelle
                // Le second est le nom de l'activité de destination
                Intent premiereActivite = new Intent(this, ToDoListActivity.class);

                // On rajoute un extra

                // Puis on lance l'intent !
                startActivity(premiereActivite);
            }else{
                updateUIST(true);
            }
        }
        else{
            if(STaskTap==1){
                STaskTap=0;
                // Le premier paramètre est le nom de l'activité actuelle
                // Le second est le nom de l'activité de destination
                Intent premiereActivite = new Intent(this, ToDoListActivity.class);

                // On rajoute un extra

                // Puis on lance l'intent !
                startActivity(premiereActivite);


            }else{
                updateUIST(false);
            }

        }


    }

    public void deleteTask(SousTache task) {
        task.delete();

        refreshList();
    }


    public void finishTask(View view){
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.task_id);
        int taskId = Integer.valueOf(String.valueOf(taskTextView.getText()));
        System.out.println("--------------id : " + taskId);
        sousTask = SousTache.findById(SousTache.class, taskId);
        sousTask.setPriority(Priorite.Fini);
        sousTask.setProgress(100);
        System.out.println("--------------PRIORITE : " + sousTask.getPriority());
        sousTask.save();

        refreshList();
    }

    public void afficheParam(View view){
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.task_id);
        int taskId = Integer.valueOf(String.valueOf(taskTextView.getText()));
        sousTask = SousTache.findById(SousTache.class, taskId);

        Log.d("Todo_"+this.toString(),"aff:"+taskId+":"+sousTask.getTaskName()+"|"+sousTask.getAlarme());

        createTaskLayout(sousTask.getTaskName(), sousTask.getDateString(), sousTask.getTimeString(), sousTask.getPriority(),sousTask.getProgress(), sousTask.getAlarme());

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
                        deleteTask(sousTask);
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
                                sousTask.setTaskName(taskName);
                            }
                            else{
                                String[] strDate = txtDate.getText().toString().split("/");
                                String[] strTime = txtTime.getText().toString().split(":");
                                String year;

                                if(strDate[2].length() != 4)
                                    year = "20"+strDate[2];
                                else
                                    year = strDate[2];

                                date = Integer.valueOf(year)*10000 + Integer.valueOf(strDate[1]) * 100 + Integer.valueOf(strDate[0]) ;
                                time = 10000 + Integer.valueOf(strTime[0])*100 + Integer.valueOf(strTime[1]);
                                sousTask.setTaskName(taskName);
                                sousTask.setDate(date);
                                sousTask.setTime(time);


                                //Pour reset les vars
                                DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm",Locale.FRANCE);
                                Date dateReset = new Date();

                                mYear = Integer.valueOf(format.format(dateReset).substring(6,10));
                                mMonth = Integer.valueOf(format.format(dateReset).substring(3,5));
                                mDay = Integer.valueOf(format.format(dateReset).substring(0,2));
                                mHour = Integer.valueOf(format.format(dateReset).substring(11,13));
                                mMinute = Integer.valueOf(format.format(dateReset).substring(14,16));


                            }

                            if(faible.isChecked()){
                                sousTask.setPriority(Priorite.Faible);
                            }
                            if(moyenne.isChecked()){
                                sousTask.setPriority(Priorite.Moyenne);
                            }
                            if(forte.isChecked()) {
                                sousTask.setPriority(Priorite.Forte);
                            }

                            if(alarmeCheck.isChecked() && !sousTask.getAlarme()) {

                                sousTask.setAlarme(true);
                                Log.d("Todo_"+this.toString(),"ajouterAlarme");
                                tDA.addAlarm(1,ToDoSousTacheListActivity.this.getApplicationContext(),sousTask.getId().intValue(),sousTask.getTaskName(),intent,(int)sousTask.getDate(),(int)sousTask.getTime());
                                //new ToDoAlarm(ToDoListActivity.this.getApplicationContext(), task.getTaskName(), (int)task.getDate(), (int)task.getTime(), intent, task.getId().intValue());//Start alarm


                            }else if(!alarmeCheck.isChecked() && sousTask.getAlarme()){
                                sousTask.setAlarme(false);
                                Log.d("Todo_"+this.toString(),"retirer alarme");
                                tDA.removeAlarm(ToDoSousTacheListActivity.this.getApplicationContext(),sousTask.getTaskName(),intent,sousTask.getId().intValue());
                                //new ToDoAlarm(ToDoListActivity.this.getApplicationContext(), task.getTaskName(), (int)task.getDate(), (int)task.getTime(), intent, task.getId().intValue());//Start alarm


                            }else if(alarmeCheck.isChecked() && sousTask.getAlarme()){
                                Log.d("Todo_"+this.toString(),"Modify retirer alarme");
                                tDA.removeAlarm(ToDoSousTacheListActivity.this.getApplicationContext(),sousTask.getTaskName(),intent,sousTask.getId().intValue());
                                Log.d("Todo_"+this.toString(),"Modify ajouterAlarme");
                                tDA.addAlarm(1,ToDoSousTacheListActivity.this.getApplicationContext(),sousTask.getId().intValue(),sousTask.getTaskName(),intent,(int)sousTask.getDate(),(int)sousTask.getTime());

                            }
                            else{
                                Log.d("Todo_"+this.toString(),"don't modify alarm");
                            }

                            sousTask.setProgress(progress);
                            sousTask.save();
                            refreshList();




                        }


                    }
                })
                .setNegativeButton(R.string.annuler, null)
                .create();


        dialog.show();
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
                refreshList();
                return true;
            case R.id.action_sauvegarde_distant:
                //o = new OnlineDatabase(this);
                //o.writeTasks();
                return true;
            case R.id.action_recup_distant:
                //o = new OnlineDatabase(this);
                //o.fetchTasks();
                return true;
            case R.id.action_clear_accounts:
                //AccountLauncher.clearAccounts(this);
                return true;
            case R.id.action_authenticate:
               // o = new OnlineDatabase(this);
                //AccountLayout.addnewaccount(this, "utilisateurTest2.testComplet@gmail.com", "aaaaaa25");
               // AccountLayout.checkPhoneAccounts(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }



    private void createTaskLayout(String taskDefault, String dateDefault, String timeDefault, int priorityDefault,int progress, boolean alarme){
        /*---------------------------------------
        Crée le layout pour la création de tâche
        ----------------------------------------*/

        /*Nom de la tâche*/
        final EditText taskEditText = new EditText(this);
        taskEditText.setHint(R.string.desc_tache);
        taskEditText.setText(taskDefault);
        taskEditText.setMaxHeight(200);

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
        //faible.setChecked(true);
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
        textAlarme.setText(R.string.alarme);
        textAlarme.setPadding(120,10,0,0);

        final CheckBox alarmeCheck = new CheckBox(this);
        alarmeCheck.setChecked(alarme);

        LinearLayout.LayoutParams layoutParamsCheck = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsCheck.setMargins(120,0,0,0);


        //Layout pour organiser l'AlerteDialog
        final LinearLayout linearLayout = new LinearLayout(this);
        final LinearLayout date = new LinearLayout(this);
        final LinearLayout time = new LinearLayout(this);

        date.setOrientation(LinearLayout.HORIZONTAL);
        date.addView(txtDate);

        time.setOrientation(LinearLayout.HORIZONTAL);
        time.addView(txtTime);

        final LinearLayout progressLayout = new LinearLayout(this);
        final LinearLayout alarmeLayout = new LinearLayout(this);
        final LinearLayout progressAlarm = new LinearLayout(this);

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

    /**Créé et gère l'AlertDialog lors de la création de tâche**/
    public void addnewtask(){

        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm",Locale.FRANCE);
        Date date = new Date();

        String currentDate = format.format(date).substring(0,10);
        String currentHour = format.format(date).substring(11,16);

        createTaskLayout("",currentDate,currentHour, 1,0, false);
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
                            SousTache sousTask;
                            int date;
                            int time;
                            boolean alarmeBool;
                            String taskName = String.valueOf(taskEditText.getText());

                            if(txtDate.getText().toString().equals("")){ //si pas de date (peut importe si heure)

                                alarmeBool=false;

                                sousTask = new SousTache(taskName,progress, alarmeBool,idTorSTChoisi);
                            }
                            else{
                                date = mYear*10000 + mMonth * 100 + mDay ;
                                time = 10000 + mHour*100 + mMinute;
                                alarmeBool=alarmeCheck.isChecked();


                                //task = new Task(taskName, date, time);
                                sousTask = new SousTache(taskName, date, time, progress, alarmeBool,idTorSTChoisi);
                                Log.d("Todo_" + this.toString(), "alarm:" + alarmeCheck.isChecked());

                            }

                            if(faible.isChecked()){
                                sousTask.setPriority(Priorite.Faible);
                            }
                            if(moyenne.isChecked()){
                                sousTask.setPriority(Priorite.Moyenne);
                            }
                            if(forte.isChecked()) {
                                sousTask.setPriority(Priorite.Forte);
                            }







                            sousTask.save();

                            refreshList();

                            if(sousTask.getAlarme()) {
                                tDA.addAlarm(1,ToDoSousTacheListActivity.this.getApplicationContext(),sousTask.getId().intValue(),sousTask.getTaskName(),intent,(int)sousTask.getDate(),(int)sousTask.getTime());
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
