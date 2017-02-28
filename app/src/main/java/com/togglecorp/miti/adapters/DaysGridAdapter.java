package com.togglecorp.miti.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.togglecorp.miti.R;
import com.togglecorp.miti.dateutils.Date;
import com.togglecorp.miti.dateutils.DateUtils;
import com.togglecorp.miti.dateutils.NepaliTranslator;
import com.togglecorp.miti.helpers.ThemeUtils;

import java.util.Calendar;

public class DaysGridAdapter extends RecyclerView.Adapter<DaysGridAdapter.ViewHolder> {

    private Context mContext;
    private Date mDate;
    private Date mToday;
    private int mExtraDays;

    public DaysGridAdapter(Context context) {
        mContext = context;
    }

    public void setDate(Date date) {
        mDate = date;
        Date temp = new Date(mDate.year, mDate.month, 1);
        Calendar engCalendar = temp.convertToEnglish().getCalendar();
        mExtraDays = engCalendar.get(Calendar.DAY_OF_WEEK)-1;

        mToday = new Date(Calendar.getInstance()).convertToNepali();

        notifyDataSetChanged();
    }

    @Override
    public DaysGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DaysGridAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_day_cell, parent, false));
    }

    @Override
    public void onBindViewHolder(DaysGridAdapter.ViewHolder holder, int position) {
        if (mDate == null) {
            holder.textView.setText("");
            return;
        }

        // Fill in after the extra days
        if (position >= mExtraDays) {
            int dt = position + 1 - mExtraDays;
            Date nepaliDate = new Date(mDate.year, mDate.month, dt);
            holder.textView.setText(NepaliTranslator.getNumber(dt+""));
            holder.textView2.setText(nepaliDate.convertToEnglish().day + "");
        } else {
            holder.textView.setText("");
            holder.textView2.setText("");
        }


        // Today
        if (mDate.year == mToday.year && mDate.month == mToday.month && position == mToday.day-1+mExtraDays) {
            holder.circle.setVisibility(View.VISIBLE);
            holder.circle.setColorFilter(ThemeUtils.getThemeColor(mContext, R.attr.colorPrimary));
        } else {
            holder.circle.setVisibility(View.GONE);
        }

        // Satuday
        if (position%7 == 6) {
            holder.textView.setTextColor(ThemeUtils.getThemeColor(mContext, R.attr.colorPrimary));
        }
    }

    @Override
    public int getItemCount() {
        if (mDate == null) {
            return 0;
        } else {
            return DateUtils.getNumDays(mDate.year, mDate.month) + mExtraDays;
        }
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView textView;
        protected TextView textView2;
        protected ImageView circle;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView)itemView.findViewById(R.id.text_view);
            textView2 = (TextView)itemView.findViewById(R.id.text_view2);
            circle = (ImageView)itemView.findViewById(R.id.circle);
        }
    }
}
