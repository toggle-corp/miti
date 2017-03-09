package com.togglecorp.miti.widgets;


import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.togglecorp.miti.R;
import com.togglecorp.miti.dateutils.Date;
import com.togglecorp.miti.dateutils.DateUtils;
import com.togglecorp.miti.dateutils.MitiDb;
import com.togglecorp.miti.dateutils.Translator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

        private MitiDb mMitiDb;
        private List<Integer> mDays = new ArrayList<>();
        private List<MitiDb.DateItem> mDateItems = new ArrayList<>();

        public GridWidgetFactory(Context context, Intent intent) {
            mContext = context;
            mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }


        @Override
        public void onCreate() {
            mMitiDb = new MitiDb(mContext);
        }

        @Override
        public void onDataSetChanged() {
            mToday = new Date(Calendar.getInstance()).convertToNepali();
            Date temp = new Date(mToday.year, mToday.month, 1);
            Calendar engCalendar = temp.convertToEnglish().getCalendar();
            mExtraDays = engCalendar.get(Calendar.DAY_OF_WEEK)-1 + 7;

            try {
                for (int i=1; i<=32; i++) {
                    MitiDb.DateItem tithi = mMitiDb.get(mToday.year*10000+mToday.month*100+i);

                    if (tithi != null && !(tithi.tithi.equals("") && tithi.extra.equals(""))) {
                        mDays.add(i);
                        mDateItems.add(tithi);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
                rv.setTextViewText(R.id.day_header, Translator.getShortDay(position));
                rv.setViewVisibility(R.id.day, View.GONE);
                rv.setViewVisibility(R.id.day_header, View.VISIBLE);
                rv.setViewVisibility(R.id.circle, View.GONE);
//                rv.setViewVisibility(R.id.divider, View.VISIBLE);
            }
            else {
                rv.setViewVisibility(R.id.day, View.VISIBLE);
                rv.setViewVisibility(R.id.day_header, View.GONE);

                boolean holiday = false;
                if (position >= mExtraDays) {
                    int dt = position + 1 - mExtraDays;
                    rv.setTextViewText(R.id.day, Translator.getNumber(dt+""));

                    // Check if holiday
                    int index = mDays.indexOf(dt);
                    if (index >= 0) {
                        holiday = mDateItems.get(index).holiday;
                    }

                    rv.setTextColor(R.id.day, ContextCompat.getColor(mContext, holiday?R.color.colorPrimaryDarkRed:android.R.color.white));

                } else {
                    rv.setTextViewText(R.id.day, "");
                }

                if (position == mToday.day-1+mExtraDays) {
                    rv.setViewVisibility(R.id.circle, View.VISIBLE);
                } else {
                    rv.setViewVisibility(R.id.circle, View.GONE);
                }
//                rv.setViewVisibility(R.id.divider, View.GONE);
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
