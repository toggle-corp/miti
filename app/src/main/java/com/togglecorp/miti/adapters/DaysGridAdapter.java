package com.togglecorp.miti.adapters;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.togglecorp.miti.R;
import com.togglecorp.miti.dateutils.Date;
import com.togglecorp.miti.dateutils.DateUtils;
import com.togglecorp.miti.dateutils.MitiDb;
import com.togglecorp.miti.dateutils.Translator;
import com.togglecorp.miti.helpers.ThemeUtils;
import com.togglecorp.miti.ui.MainActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DaysGridAdapter extends RecyclerView.Adapter<DaysGridAdapter.ViewHolder> {

    private Activity mActivity;
    private Date mDate;
    private Date mToday;
    private int mExtraDays;

    private MitiDb mMitiDb;
    private List<Integer> mDays = new ArrayList<>();
    private List<MitiDb.DateItem> mDateItems = new ArrayList<>();

    public DaysGridAdapter(Activity activity) {
        mActivity = activity;
        mMitiDb = new MitiDb(activity);
    }

    public void setDate(Date date) {
        mDate = date;
        Date temp = new Date(mDate.year, mDate.month, 1);
        Calendar engCalendar = temp.convertToEnglish().getCalendar();
        mExtraDays = engCalendar.get(Calendar.DAY_OF_WEEK)-1;

        mToday = new Date(Calendar.getInstance()).convertToNepali();

        notifyDataSetChanged();

        mDays.clear();
        mDateItems.clear();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    for (int i=1; i<=32; i++) {
                        MitiDb.DateItem tithi = mMitiDb.get(mDate.year*10000+mDate.month*100+i);

                        if (tithi != null && !(tithi.tithi.equals("") && tithi.extra.equals(""))) {
                            mDays.add(i);
                            mDateItems.add(tithi);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                notifyDataSetChanged();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

        boolean holiday = false;

        // Fill in after the extra DAYS_NEPALI
        if (position >= mExtraDays) {
            int dt = position + 1 - mExtraDays;
            Date nepaliDate = new Date(mDate.year, mDate.month, dt);
            holder.textView.setText(Translator.getNumber(dt+""));
            holder.textView2.setText(nepaliDate.convertToEnglish().day + "");

            // Check if holiday
            int index = mDays.indexOf(dt);
            if (index >= 0) {
                holiday = mDateItems.get(index).holiday;
            }

            // Selected
            if (nepaliDate.equals(((MainActivity)mActivity).getSelectedDate())) {
                holder.circle.setVisibility(View.VISIBLE);
                holder.circle.setColorFilter(ThemeUtils.getThemeColor(mActivity, /*holiday?R.attr.colorHoliday:*/R.attr.colorSelection));
            }

            // Font size
            holder.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, ThemeUtils.getFontSize(mActivity, new float[]{12, 16, 20}));
            holder.textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, ThemeUtils.getFontSize(mActivity, new float[]{8, 10, 11}));


        } else {
            holder.textView.setText("");
            holder.textView2.setText("");
        }

        // Satuday and holiday

        if (holiday || position%7 == 6) {
            holder.textView.setTextColor(ThemeUtils.getThemeColor(mActivity, R.attr.colorHoliday));
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
                    ((MainActivity)mActivity).selectDate(nepaliDate, false);
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
