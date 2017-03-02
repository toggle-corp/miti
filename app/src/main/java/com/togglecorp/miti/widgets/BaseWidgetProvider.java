package com.togglecorp.miti.widgets;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.togglecorp.miti.R;
import com.togglecorp.miti.ui.MainActivity;

import java.util.Calendar;

public class BaseWidgetProvider extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);

        ComponentName thisWidget = new ComponentName(context, getClass());
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        for (int widgetId : allWidgetIds) {

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    getWidgetLayout());

            updateWidget(widgetId, context, remoteViews);
            setAlarm(context);

            // Register an onClickListener to launch MainActivity
            Intent startActivityIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, startActivityIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.widget, pendingIntent);


            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    protected int getWidgetLayout() {
        return 0;
    }

    protected void updateWidget(int appWidgetId, Context context, RemoteViews remoteViews) {
    }

    protected void setAlarm(Context context) {
        // Set alarm to wake up next day midnight
        Calendar midnight = Calendar.getInstance();
        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.SECOND, 1);
        midnight.set(Calendar.MILLISECOND, 0);
        midnight.add(Calendar.DAY_OF_YEAR, 1);

        Intent intent = new Intent(context, getClass());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.set(AlarmManager.RTC_WAKEUP, midnight.getTimeInMillis(), pendingIntent);
    }
}
