package com.togglecorp.miti.adapters;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.togglecorp.miti.R;
import com.togglecorp.miti.dateutils.Date;
import com.togglecorp.miti.dateutils.NepaliTranslator;
import com.togglecorp.miti.dateutils.MitiDb;
import com.togglecorp.miti.helpers.ThemeUtils;
import com.togglecorp.miti.ui.MainActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TithiListAdapter extends RecyclerView.Adapter<TithiListAdapter.ViewHolder> {

    private Activity mActivity;
    private MitiDb mMitiDb;
    private int mYear, mMonth;

    private List<Integer> mDays = new ArrayList<>();
    private List<MitiDb.DateItem> mDateItems = new ArrayList<>();
    private int mToday;

    private boolean mNewDate = false;

    public TithiListAdapter(Activity activity) {
        mActivity = activity;
        mMitiDb = new MitiDb(activity);
    }

    public void setDate(final int year, final int month) {
        // Flag to cancel async task
        mNewDate = true;

        mDays.clear();
        mDateItems.clear();
        mYear = year;
        mMonth = month;

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                mNewDate = false;

                try {
                    List<Integer> days = new ArrayList<Integer>();
                    List<MitiDb.DateItem> items = new ArrayList<>();

                    for (int i=1; i<=32; i++) {

                        // If a new date request has come again, then stop everything
                        if (mNewDate) {
                            return null;
                        }

                        MitiDb.DateItem tithi = mMitiDb.get(year*10000+month*100+i);

                        if (tithi != null && !(tithi.tithi.equals("") && tithi.extra.equals(""))) {
                            days.add(i);
                            items.add(tithi);
                        }
                    }

                    if (mNewDate) {
                        return null;
                    }

                    mDays = days;
                    mDateItems = items;

                    mToday = -1;
                    Date today = new Date(Calendar.getInstance()).convertToNepali();
                    if (today.year == year && today.month == month) {
                        for (int i=0; i<mDays.size(); i++) {
                            if (mDays.get(i) == today.day) {
                                mToday = i;
                            }
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_tithi, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (mDays.size() == 0) {
            holder.day.setText("");
            holder.circle.setVisibility(View.GONE);
            holder.extra.setVisibility(View.GONE);
            holder.tithi.setText("यस महिनाको तिथि विवरण उपलब्ध छैन");
            return;
        }

        MitiDb.DateItem item = mDateItems.get(position);

        holder.day.setText(NepaliTranslator.getNumber(mDays.get(position).toString()));
        holder.tithi.setText(item.tithi);
        holder.extra.setText(item.extra);

        if (mToday == position) {
            holder.circle.setVisibility(View.VISIBLE);
            holder.circle.setColorFilter(ThemeUtils.getThemeColor(mActivity, R.attr.colorAccent));
        } else {
            holder.circle.setVisibility(View.INVISIBLE);
        }

        if (item.extra.trim().length() == 0) {
            holder.extra.setVisibility(View.GONE);
        } else {
            holder.extra.setVisibility(View.VISIBLE);
        }

        if (((MainActivity)mActivity).getSelectedDate().equals(new Date(mYear, mMonth, mDays.get(position)))) {
            holder.circle.setVisibility(View.VISIBLE);
            holder.circle.setColorFilter(ThemeUtils.getThemeColor(mActivity, R.attr.colorPrimary));
        }

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            ((MainActivity)mActivity).selectDate(new Date(mYear, mMonth, mDays.get(holder.getAdapterPosition())));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDays.size() > 0 ? mDays.size() : 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View root;
        private TextView day;
        private TextView tithi;
        private TextView extra;
        private ImageView circle;

        public ViewHolder(View itemView) {
            super(itemView);

            root = itemView;
            day = (TextView)itemView.findViewById(R.id.day);
            tithi = (TextView)itemView.findViewById(R.id.tithi);
            extra = (TextView)itemView.findViewById(R.id.extra);
            circle = (ImageView)itemView.findViewById(R.id.circle);
        }
    }
}
