package com.togglecorp.miti.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.togglecorp.miti.dateutils.DateUtils;
import com.togglecorp.miti.ui.MonthFragment;

public class MonthPagerAdapter extends FragmentStatePagerAdapter {
    public MonthPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        int year = position/12 + DateUtils.startNepaliYear;
        int month = position%12 + 1;
        MonthFragment monthFragment = new MonthFragment();
        monthFragment.setMonth(year, month);
        return monthFragment;
    }

    @Override
    public int getCount() {
        return 12*91;
    }

    @Override
    public int getItemPosition(Object object) {
        // POSITION_NONE makes it possible to reload the PagerAdapter
        return POSITION_NONE;
    }

}
