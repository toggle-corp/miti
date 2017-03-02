package com.togglecorp.miti.adapters;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.togglecorp.miti.R;
import com.togglecorp.miti.dateutils.Date;
import com.togglecorp.miti.dateutils.NepaliTranslator;
import com.togglecorp.miti.dateutils.TithiDb;
import com.togglecorp.miti.helpers.ThemeUtils;
import com.togglecorp.miti.ui.MainActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TithiListAdapter extends RecyclerView.Adapter<TithiListAdapter.ViewHolder> {

    private Activity mActivity;
    private TithiDb mTithiDb;
    private int mYear, mMonth;

    private List<Integer> mDays = new ArrayList<>();
    private List<Pair<String, String>> mTithis = new ArrayList<>();
    private int mToday;

    private boolean mNewDate = false;

    public TithiListAdapter(Activity activity) {
        mActivity = activity;
        mTithiDb = new TithiDb(activity);
    }

    public void setDate(final int year, final int month) {
        // Flag to cancel async task
        mNewDate = true;

        mDays.clear();
        mTithis.clear();
        mYear = year;
        mMonth = month;

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                mNewDate = false;

                try {
                    List<Integer> days = new ArrayList<Integer>();
                    List<Pair<String, String>> tithis = new ArrayList<>();

                    for (int i=1; i<=32; i++) {

                        // If a new date request has come again, then stop everything
                        if (mNewDate) {
                            return null;
                        }

                        Pair<String, String> tithi = mTithiDb.get(year*10000+month*100+i);

                        if (tithi != null && !(tithi.first.equals("") && tithi.second.equals(""))) {
                            days.add(i);
                            tithis.add(tithi);
                        }
                    }

                    if (mNewDate) {
                        return null;
                    }

                    mDays = days;
                    mTithis = tithis;

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

        Pair<String, String> tithi = mTithis.get(position);

        holder.day.setText(NepaliTranslator.getNumber(mDays.get(position).toString()));
        holder.tithi.setText(tithi.first);
        holder.extra.setText(tithi.second);

        if (mToday == position) {
            holder.circle.setVisibility(View.VISIBLE);
            holder.circle.setColorFilter(ThemeUtils.getThemeColor(mActivity, R.attr.colorAccent));
        } else {
            holder.circle.setVisibility(View.INVISIBLE);
        }

        if (tithi.second.trim().length() == 0) {
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
