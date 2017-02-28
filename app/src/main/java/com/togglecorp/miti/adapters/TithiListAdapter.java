package com.togglecorp.miti.adapters;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TithiListAdapter extends RecyclerView.Adapter<TithiListAdapter.ViewHolder> {

    private Context mContext;
    private TithiDb mTithiDb;

    private List<Integer> mDays = new ArrayList<>();
    private List<Pair<String, String>> mTithis = new ArrayList<>();
    private int mToday;

    private boolean mNewDate = false;

    public TithiListAdapter(Context context) {
        mContext = context;
        mTithiDb = new TithiDb(context);
    }

    public void setDate(final int year, final int month) {
        // Flag to cancel async task
        mNewDate = true;

        mDays.clear();
        mTithis.clear();

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

                        String date = String.format("%04d-%02d-%02d", year, month, i);
                        Pair<String, String> tithi = mTithiDb.get(date);

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
            protected void onPostExecute(Void reesult) {
                notifyDataSetChanged();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public int getTodayPosition() {
        return mToday;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_tithi, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Pair<String, String> tithi = mTithis.get(position);

        holder.day.setText(NepaliTranslator.getNumber(mDays.get(position).toString()));
        holder.tithi.setText(tithi.first);
        holder.extra.setText(tithi.second);

        if (mToday == position) {
            holder.circle.setVisibility(View.VISIBLE);
            holder.circle.setColorFilter(ThemeUtils.getThemeColor(mContext, R.attr.colorPrimary));
        } else {
            holder.circle.setVisibility(View.INVISIBLE);
        }

        if (tithi.second.trim().length() == 0) {
            holder.extra.setVisibility(View.GONE);
        } else {
            holder.extra.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mDays.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView day;
        private TextView tithi;
        private TextView extra;
        private ImageView circle;

        public ViewHolder(View itemView) {
            super(itemView);

            day = (TextView)itemView.findViewById(R.id.day);
            tithi = (TextView)itemView.findViewById(R.id.tithi);
            extra = (TextView)itemView.findViewById(R.id.extra);
            circle = (ImageView)itemView.findViewById(R.id.circle);
        }
    }
}
