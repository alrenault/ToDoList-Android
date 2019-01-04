package com.todolist.aladdalo.todolist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.todolist.aladdalo.todolist.db.Task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends ArrayAdapter<Task> {

    public TaskAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<Task> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Get the data item for this position
        Task task = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_task, parent, false);
        }

        // Lookup view for data population
        TextView tvId = (TextView) convertView.findViewById(R.id.task_id);
        TextView tvTitle = (TextView) convertView.findViewById(R.id.task_title);
        TextView tvDeadLine = (TextView) convertView.findViewById(R.id.deadline);

        // Populate the data into the template view using the data object
        tvId.setText(String.valueOf(task.getId()));
        tvTitle.setText(task.getTaskName());
        tvDeadLine.setText(task.getDateString()+"\n  "+task.getTimeString());


        ImageView vignette1 = convertView.findViewById(R.id.imgPrio1);
        ImageView vignette2 = convertView.findViewById(R.id.imgPrio2);
        ImageView vignette3 = convertView.findViewById(R.id.imgPrio3);

        switch (task.getPriority()){
            case 0 :
                vignette1.setVisibility(View.INVISIBLE);
                vignette2.setVisibility(View.INVISIBLE);
                vignette3.setVisibility(View.INVISIBLE);
                break;

            case 1:
                vignette1.setVisibility(View.VISIBLE);
                vignette2.setVisibility(View.INVISIBLE);
                vignette3.setVisibility(View.INVISIBLE);
                break;
            case 2 :
                vignette1.setVisibility(View.INVISIBLE);
                vignette2.setVisibility(View.VISIBLE);
                vignette3.setVisibility(View.INVISIBLE);
                break;
            case 3:
                vignette1.setVisibility(View.INVISIBLE);
                vignette2.setVisibility(View.INVISIBLE);
                vignette3.setVisibility(View.VISIBLE);
        }

        ProgressBar progress = convertView.findViewById(R.id.progress);
        progress.setProgress(task.getProgress());

        TextView progressPercent = convertView.findViewById(R.id.progressPercent);
        progressPercent.setText(task.getProgress()+"%");

        Button finish = convertView.findViewById(R.id.task_finish);

        if(task.getPriority() == 0){
            finish.setVisibility(View.INVISIBLE);
            finish.setClickable(false);
        }
        else{
            finish.setVisibility(View.VISIBLE);
            finish.setClickable(true);
        }

        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm",Locale.FRANCE);
        Date date = new Date();

        int currentDate = Integer.valueOf(format.format(date).substring(6,10))*10000 + Integer.valueOf(format.format(date).substring(3,5)) * 100 + Integer.valueOf(format.format(date).substring(0,2)) ;
        int currentTime = 10000 + Integer.valueOf(format.format(date).substring(11,13))*100 + Integer.valueOf(format.format(date).substring(14,16));

        if(task.getPriority() != 0 && task.isFinish(currentTime,currentDate) && task.getDate() != 0){
            tvDeadLine.setTextColor(getContext().getResources().getColor(R.color.delete));
        }
        else{
            tvDeadLine.setTextColor(getContext().getResources().getColor(R.color.browser_actions_title_color));
        }


        // Return the completed view to render on screen
        return convertView;
    }
}
