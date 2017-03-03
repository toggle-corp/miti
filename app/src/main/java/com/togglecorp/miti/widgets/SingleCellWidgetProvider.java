package com.togglecorp.miti.widgets;

import android.content.Context;
import android.widget.RemoteViews;

import com.togglecorp.miti.R;
import com.togglecorp.miti.dateutils.Date;
import com.togglecorp.miti.dateutils.DateUtils;
import com.togglecorp.miti.dateutils.MitiDb;
import com.togglecorp.miti.dateutils.Translator;

import java.util.Calendar;
import java.util.Locale;

public class SingleCellWidgetProvider extends BaseWidgetProvider {
    @Override
    protected int getWidgetLayout() {
        return R.layout.widget_single_cell;
    }

    @Override
    protected void updateWidget(int appWidgetId, Context context, RemoteViews remoteViews) {

        // Nepali
        Date nepaliNow = new Date(Calendar.getInstance()).convertToNepali();
        MitiDb.DateItem dateItem = new MitiDb(context).get(nepaliNow);

        remoteViews.setTextViewText(R.id.year, Translator.getNumber(nepaliNow.year+""));
        remoteViews.setTextViewText(R.id.month, Translator.getMonth(nepaliNow.month));
        remoteViews.setTextViewText(R.id.day, Translator.getNumber(nepaliNow.day+""));
    }
}
