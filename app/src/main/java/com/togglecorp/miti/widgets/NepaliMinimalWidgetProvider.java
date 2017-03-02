package com.togglecorp.miti.widgets;

import android.content.Context;
import android.view.View;
import android.widget.RemoteViews;

import com.togglecorp.miti.R;
import com.togglecorp.miti.dateutils.Date;
import com.togglecorp.miti.dateutils.MitiDb;
import com.togglecorp.miti.dateutils.NepaliTranslator;

import java.util.Calendar;

public class NepaliMinimalWidgetProvider extends BaseWidgetProvider {

    @Override
    protected int getWidgetLayout() {
        return R.layout.widget_minimal_nepali;
    }

    @Override
    protected void updateWidget(int appWidgetId, Context context, RemoteViews remoteViews) {
        Date now = new Date(Calendar.getInstance()).convertToNepali();
        MitiDb.DateItem dateItem = new MitiDb(context).get(now);

        remoteViews.setTextViewText(R.id.year, NepaliTranslator.getNumber(now.year+""));
        remoteViews.setTextViewText(R.id.month, NepaliTranslator.getMonth(now.month));
        remoteViews.setTextViewText(R.id.day, NepaliTranslator.getNumber(now.day+""));

        remoteViews.setTextViewText(R.id.tithi, dateItem.tithi);
        remoteViews.setTextViewText(R.id.extra, dateItem.extra);
        if (dateItem.extra.length() == 0) {
            remoteViews.setViewVisibility(R.id.extra, View.GONE);
        }
    }
}
