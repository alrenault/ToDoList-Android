package com.todolist.aladdalo.todolist;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.todolist.aladdalo.todolist.db.TaskContract;
import com.todolist.aladdalo.todolist.db.TaskDbHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ToDoListActivity extends AppCompatActivity implements
        View.OnClickListener {

    private static final String TAG = "ToDoListActivity";

    private TaskDbHelper mHelper;
    private ListView mTaskListView;

    private ArrayAdapter<String> mAdapter;

    Button btnDatePicker, btnTimePicker;
    EditText txtDate, txtTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);

        mHelper = new TaskDbHelper(this);
        mTaskListView = (ListView) findViewById(R.id.list_todo);

        updateUI();
    }

    private void updateUI() {
        ArrayList<String> taskList = new ArrayList<>();

        SQLiteDatabase db = mHelper.getReadableDatabase();

        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE,
                new String[]{TaskContract.TaskEntry._ID, TaskContract.TaskEntry.COL_TASK_TITLE},
                null, null, null, null, null);

        while (cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE);
            taskList.add(cursor.getString(idx));
        }

        if (mAdapter == null) {
            mAdapter = new ArrayAdapter<>(this,
                    R.layout.item_todo, // what view to use for the items
                    R.id.task_title, // where to put the String of data
                    taskList); // where to get all the data
            mTaskListView.setAdapter(mAdapter); // set it as the adapter of the ListView instance
        } else {
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
        }

        cursor.close();
        db.close();
    }

    public void deleteTask(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.task_title);
        String task = String.valueOf(taskTextView.getText());
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(TaskContract.TaskEntry.TABLE,
                TaskContract.TaskEntry.COL_TASK_TITLE + " = ?",
                new String[]{task});
        db.close();
        updateUI();
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

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addnewtask(){
        final EditText taskEditText = new EditText(this);
        taskEditText.setHint(R.string.desc_tache);

        //EditText pour la date
        txtDate= new EditText(this);
        txtDate.setWidth(200);
        txtDate.setHint(R.string.format_date);
        txtDate.setText("");

        //Bouton affichant le date picker
        btnDatePicker= new Button(this);
        btnDatePicker.setText(R.string.choix_date);

        //EditText pour l'heure
        txtTime=new EditText(this);
        txtTime.setWidth(200);
        txtTime.setHint(R.string.format_heure);
        txtTime.setText("");

        //Bouton affichant le time picker
        btnTimePicker= new Button(this);
        btnTimePicker.setText(R.string.choix_heure);

        //listener des boutons
        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);


        //Layout pour organiser l'AlerteDialog
        final LinearLayout linearLayout = new LinearLayout(this);
        final LinearLayout date = new LinearLayout(this);
        final LinearLayout time = new LinearLayout(this);

        date.setOrientation(LinearLayout.HORIZONTAL);
        date.addView(txtDate);
        date.addView(btnDatePicker);

        time.setOrientation(LinearLayout.HORIZONTAL);
        time.addView(txtTime);
        time.addView(btnTimePicker);

        linearLayout.addView(taskEditText);
        linearLayout.addView(date);
        linearLayout.addView(time);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        ViewGroup.LayoutParams params = taskEditText.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        taskEditText.setLayoutParams(params);

        //-----------------------------------------------------

        //Creation du AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.ajout_tache)
                .setMessage(R.string.faire_ensuite)
                .setView(linearLayout)
                .setPositiveButton(R.string.ajouter, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!taskEditText.getText().toString().equals("")){//si intitule de tache
                            if(txtDate.getText().toString().equals("")){ //si pas de date (peut importe si heure)
                                //TODO : mettre uniquement le string de la tache dans BDD
                            }
                            else{
                                if(txtTime.getText().toString().equals("")){ // si date mais pas heure
                                    txtDate.setText(R.string.heure_defaut);
                                }
                                //TODO : mettre le string de la tache + heure + date dans BDD
                            }

                            //met le string de la tache dans BDD
                            String task = String.valueOf(taskEditText.getText());
                            SQLiteDatabase db = mHelper.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put(TaskContract.TaskEntry.COL_TASK_TITLE, task);
                            db.insertWithOnConflict(TaskContract.TaskEntry.TABLE,
                                    null,
                                    values,
                                    SQLiteDatabase.CONFLICT_REPLACE);
                            db.close();
                            updateUI();
                        }


                    }
                })
                .setNegativeButton(R.string.annuler, null)
                .create();


        dialog.show();
    }


    @Override
    public void onClick(View v) {
        int mYear, mMonth, mDay, mHour, mMinute;

        if (v == btnDatePicker) {

            //Recupere la date actuelle
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            //Lance le Date Picker Dialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            //txtDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            txtDate.setText(String.format(getResources().getString(R.string.date),dayOfMonth,(monthOfYear + 1),year));


                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (v == btnTimePicker) {

            //Recupere l'heure actuelle
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Lance le Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            //txtTime.setText(hourOfDay + ":" + minute);
                            txtDate.setText(String.format(getResources().getString(R.string.heure),hourOfDay,minute));

                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }

}
