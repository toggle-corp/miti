package com.togglecorp.miti.widgets;

import android.content.Context;
import android.widget.RemoteViews;

import com.togglecorp.miti.R;
import com.togglecorp.miti.dateutils.Date;
import com.togglecorp.miti.dateutils.DateUtils;
import com.togglecorp.miti.dateutils.MitiDb;
import com.togglecorp.miti.dateutils.NepaliTranslator;

import java.util.Calendar;
import java.util.Locale;

public class NepaliEnglishMinimalWidgetProvider extends BaseWidgetProvider {
    @Override
    protected int getWidgetLayout() {
        return R.layout.widget_minimal_nepali_english;
    }

    @Override
    protected void updateWidget(int appWidgetId, Context context, RemoteViews remoteViews) {

        // Nepali
        Date nepaliNow = new Date(Calendar.getInstance()).convertToNepali();
        MitiDb.DateItem dateItem = new MitiDb(context).get(nepaliNow);

        remoteViews.setTextViewText(R.id.nepali_year, NepaliTranslator.getNumber(nepaliNow.year+""));
        remoteViews.setTextViewText(R.id.nepali_month, NepaliTranslator.getMonth(nepaliNow.month));
        remoteViews.setTextViewText(R.id.nepali_day, NepaliTranslator.getNumber(nepaliNow.day+""));
        remoteViews.setTextViewText(R.id.tithi, dateItem.tithi);

        // English
        Date englishNow = new Date(Calendar.getInstance());

        remoteViews.setTextViewText(R.id.english_year, englishNow.year+"");
        remoteViews.setTextViewText(R.id.english_month, DateUtils.getEnglishLongMonth(englishNow.month));
        remoteViews.setTextViewText(R.id.english_day, englishNow.day+"");
        remoteViews.setTextViewText(R.id.day,
                Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US));
    }
}
