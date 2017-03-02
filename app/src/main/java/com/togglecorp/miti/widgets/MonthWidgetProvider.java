package com.togglecorp.miti.widgets;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.togglecorp.miti.R;
import com.togglecorp.miti.dateutils.Date;
import com.togglecorp.miti.dateutils.NepaliTranslator;

import java.util.Calendar;

public class MonthWidgetProvider extends BaseWidgetProvider {
    @Override
    protected int getWidgetLayout() {
        return R.layout.widget_month;
    }

    @Override
    protected void updateWidget(int appWidgetId, Context context, RemoteViews remoteViews) {
        Date today = new Date(Calendar.getInstance()).convertToNepali();
        String title = NepaliTranslator.getNumber(today.year + "") + " "
                + NepaliTranslator.getMonth(today.month);
        remoteViews.setTextViewText(R.id.title, title);

        Intent intent = new Intent(context, GridWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        remoteViews.setRemoteAdapter(R.id.grid, intent);
    }
}
