package com.togglecorp.miti.adapters;

import android.app.Activity;
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
import com.togglecorp.miti.ui.MainActivity;

import java.util.Calendar;

public class DaysGridAdapter extends RecyclerView.Adapter<DaysGridAdapter.ViewHolder> {

    private Activity mActivity;
    private Date mDate;
    private Date mToday;
    private int mExtraDays;

    public DaysGridAdapter(Activity activity) {
        mActivity = activity;
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
    public void onBindViewHolder(final DaysGridAdapter.ViewHolder holder, int position) {
        if (mDate == null) {
            holder.textView.setText("");
            return;
        }

        // Today
        if (mDate.year == mToday.year && mDate.month == mToday.month && position == mToday.day-1+mExtraDays) {
            holder.circle.setVisibility(View.VISIBLE);
            holder.circle.setColorFilter(ThemeUtils.getThemeColor(mActivity, R.attr.colorAccent));

        } else {
            holder.circle.setVisibility(View.GONE);
        }

        // Fill in after the extra days
        if (position >= mExtraDays) {
            int dt = position + 1 - mExtraDays;
            Date nepaliDate = new Date(mDate.year, mDate.month, dt);
            holder.textView.setText(NepaliTranslator.getNumber(dt+""));
            holder.textView2.setText(nepaliDate.convertToEnglish().day + "");

            // Selected
            if (nepaliDate.equals(((MainActivity)mActivity).getSelectedDate())) {
                holder.circle.setVisibility(View.VISIBLE);
                holder.circle.setColorFilter(ThemeUtils.getThemeColor(mActivity, R.attr.colorPrimary));
            }

        } else {
            holder.textView.setText("");
            holder.textView2.setText("");
        }

        // Satuday
        if (position%7 == 6) {
            holder.textView.setTextColor(ThemeUtils.getThemeColor(mActivity, R.attr.colorPrimary));
        } else {
            holder.textView.setTextColor(ThemeUtils.getThemeColor(mActivity, android.R.attr.textColor));
        }

        // Click to select
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getAdapterPosition() >= mExtraDays) {
                    int dt = holder.getAdapterPosition() + 1 - mExtraDays;
                    Date nepaliDate = new Date(mDate.year, mDate.month, dt);
                    ((MainActivity)mActivity).selectDate(nepaliDate);
                    notifyDataSetChanged();
                }
            }
        });
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
        protected View root;
        protected TextView textView;
        protected TextView textView2;
        protected ImageView circle;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView)itemView.findViewById(R.id.text_view);
            textView2 = (TextView)itemView.findViewById(R.id.text_view2);
            circle = (ImageView)itemView.findViewById(R.id.circle);
            root = itemView;
        }
    }
}
