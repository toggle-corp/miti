package com.togglecorp.miti.widgets;

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

public class TestWidgetProvider extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);

        ComponentName thisWidget = new ComponentName(context, TestWidgetProvider.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        for (int widgetId : allWidgetIds) {

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_test);
            updateWidget(context, remoteViews);

            // Register an onClickListener to launch MainActivity
            Intent intent1 = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.widget, pendingIntent);


            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    private void updateWidget(Context context, RemoteViews remoteViews) {
        Calendar now = Calendar.getInstance();

        remoteViews.setTextViewText(R.id.test, String.format("%1$tA %1$tb %1$td %1$tY at %1$tI:%1$tM %1$Tp", now));
    }
}
