package com.togglecorp.miti.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.togglecorp.miti.R;
import com.togglecorp.miti.dateutils.NepaliTranslator;
import com.togglecorp.miti.helpers.ThemeUtils;

public class DaysHeaderAdapter extends RecyclerView.Adapter<DaysHeaderAdapter.ViewHolder> {

    private Context mContext;

    public DaysHeaderAdapter(Context context) {
        mContext = context;
    }

    @Override
    public DaysHeaderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_day_header, parent, false));
    }

    @Override
    public void onBindViewHolder(DaysHeaderAdapter.ViewHolder holder, int position) {
        holder.textView.setText(NepaliTranslator.getShortDay(position));

        if (position == 6) {
//            holder.textView.setTextColor(ThemeUtils.getThemeColor(mContext, R.attr.colorPrimary));
        }
    }

    @Override
    public int getItemCount() {
        return 7;
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView)itemView.findViewById(R.id.text_view);
        }
    }
}
