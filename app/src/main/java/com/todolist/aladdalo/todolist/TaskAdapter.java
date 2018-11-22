package com.todolist.aladdalo.todolist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.todolist.aladdalo.todolist.db.Task;

import java.util.List;

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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_todo, parent, false);
        }

        // Lookup view for data population
        TextView tvId = (TextView) convertView.findViewById(R.id.task_id);
        TextView tvTitle = (TextView) convertView.findViewById(R.id.task_title);
        TextView tvDeadLine = (TextView) convertView.findViewById(R.id.deadline);

        // Populate the data into the template view using the data object
        tvId.setText(String.valueOf(task.getId()));
        tvTitle.setText(task.getTaskName());
        tvDeadLine.setText(task.getTimeString()+" "+task.getDateString());

        // Return the completed view to render on screen
        return convertView;
    }
}
