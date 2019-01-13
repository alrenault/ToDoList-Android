
package com.todolist.aladdalo.todolist;

import java.util.ArrayList;
import java.util.List;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.orm.query.Condition;
import com.orm.query.Select;
import com.todolist.aladdalo.todolist.db.Task;

public class StackWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private static int mCount = 10;
    private List<WidgetItem> mWidgetItems = new ArrayList<WidgetItem>();
    private Context mContext;
    private int mAppWidgetId;
    List<Task> tasks;

    public StackRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    public void onCreate() {
        //fait le lien entre les données et le widget
        tasks = Select.from(Task.class)
                .where(Condition.prop("priority").notEq(0))
                .orderBy("date")
                .list();
        mCount = tasks.size();
        for (int i = 0; i < mCount; i++) {
            if(tasks.get(i%tasks.size()).getTaskName().length() > 12){
                mWidgetItems.add(new WidgetItem(tasks.get(i%tasks.size()).getTaskName().substring(0, 10) + "...\n" + tasks.get(i%tasks.size()).getDateString() + "\n" + tasks.get(i%tasks.size()).getTimeString()));
            }else{
                mWidgetItems.add(new WidgetItem(tasks.get(i%tasks.size()).getTaskName() + "\n" + tasks.get(i%tasks.size()).getDateString() + "\n" + tasks.get(i%tasks.size()).getTimeString()));
            }

        }
    }

    public void onDestroy() {
        mWidgetItems.clear();
    }

    public int getCount() {
        return mCount;
    }

    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
        rv.setTextViewText(R.id.widget_item, mWidgetItems.get(position).text);

        Bundle extras = new Bundle();
        extras.putInt(StackWidgetProvider.EXTRA_ITEM, position);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        rv.setOnClickFillInIntent(R.id.widget_item, fillInIntent);

        return rv;
    }

    public RemoteViews getLoadingView() {
        return null;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDataSetChanged() {
    }
}