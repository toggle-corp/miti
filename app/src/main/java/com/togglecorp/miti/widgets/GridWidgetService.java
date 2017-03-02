package com.togglecorp.miti.widgets;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.togglecorp.miti.R;
import com.togglecorp.miti.dateutils.Date;
import com.togglecorp.miti.dateutils.DateUtils;
import com.togglecorp.miti.dateutils.NepaliTranslator;
import com.togglecorp.miti.ui.MainActivity;

import java.util.Calendar;

public class GridWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridWidgetFactory(getApplicationContext(), intent);
    }

    public static class GridWidgetFactory implements RemoteViewsFactory {
        private Context mContext;
        private int mAppWidgetId;

        private int mExtraDays;
        private Date mToday;

        public GridWidgetFactory(Context context, Intent intent) {
            mContext = context;
            mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }


        @Override
        public void onCreate() {
            mToday = new Date(Calendar.getInstance()).convertToNepali();
            Date temp = new Date(mToday.year, mToday.month, 1);
            Calendar engCalendar = temp.convertToEnglish().getCalendar();
            mExtraDays = engCalendar.get(Calendar.DAY_OF_WEEK)-1 + 7;
        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return DateUtils.getNumDays(mToday.year, mToday.month) + mExtraDays;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.layout_month_widget_item);

            if (position < 7) {
                rv.setTextViewText(R.id.day, NepaliTranslator.getShortDay(position));
                rv.setViewVisibility(R.id.circle, View.GONE);
            }
            else {

                if (position >= mExtraDays) {
                    int dt = position + 1 - mExtraDays;
                    rv.setTextViewText(R.id.day, NepaliTranslator.getNumber(dt+""));

                } else {
                    rv.setTextViewText(R.id.day, "");
                }

                if (position == mToday.day-1+mExtraDays) {
                    rv.setViewVisibility(R.id.circle, View.VISIBLE);
                } else {
                    rv.setViewVisibility(R.id.circle, View.GONE);
                }
            }

            Intent fillInIntent = new Intent();

            if (position >= mExtraDays) {
                fillInIntent.putExtra("day", position+1-mExtraDays);
            }
            rv.setOnClickFillInIntent(R.id.grid_cell, fillInIntent);

            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
