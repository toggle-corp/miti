package com.togglecorp.miti.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.togglecorp.miti.R;
import com.togglecorp.miti.adapters.DaysGridAdapter;
import com.togglecorp.miti.adapters.DaysHeaderAdapter;
import com.togglecorp.miti.adapters.TithiListAdapter;
import com.togglecorp.miti.dateutils.Date;

public class MonthFragment extends Fragment {
    private final static String TAG = "MonthFragment";

    private DaysGridAdapter mDaysGridAdapter;

    private Date mCurrentDate = new Date(2000, 1, 1);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_month,
                container, false);

        RecyclerView daysHeaderView = (RecyclerView)rootView.findViewById(R.id.days_header_view);
        daysHeaderView.setLayoutManager(new GridLayoutManager(getActivity(), 7));
        daysHeaderView.setAdapter(new DaysHeaderAdapter(getActivity()));

        RecyclerView daysGridView = (RecyclerView)rootView.findViewById(R.id.days_grid_view);
        daysGridView.setLayoutManager(new GridLayoutManager(getActivity(), 7));
        mDaysGridAdapter = new DaysGridAdapter(getActivity());
        daysGridView.setAdapter(mDaysGridAdapter);

        setMonth(mCurrentDate.year, mCurrentDate.month);
        return rootView;
    }

    public void setMonth(int year, int month) {
        mCurrentDate.year = year;
        mCurrentDate.month = month;

        if (mDaysGridAdapter != null) {
            mDaysGridAdapter.setDate(mCurrentDate);
        }
    }

}
