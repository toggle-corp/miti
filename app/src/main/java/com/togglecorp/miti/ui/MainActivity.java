package com.togglecorp.miti.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.togglecorp.miti.R;
import com.togglecorp.miti.adapters.MonthPagerAdapter;
import com.togglecorp.miti.adapters.TithiListAdapter;
import com.togglecorp.miti.dateutils.Date;
import com.togglecorp.miti.dateutils.DateUtils;
import com.togglecorp.miti.dateutils.Translator;
import com.togglecorp.miti.dateutils.MitiDb;
import com.togglecorp.miti.dateutils.TithiGrabber;
import com.togglecorp.miti.helpers.DailyBroadcastReceiver;
import com.togglecorp.miti.helpers.ThemeUtils;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ViewPager mMonthPager;
    private MonthPagerAdapter mMonthPagerAdapter;
    private TithiListAdapter mTithiListAdapter;

    private int mCurrentYear, mCurrentMonth;
    private boolean mRefreshPending = false;

    private String mCurrentTheme;
    private String mCurrentFontSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.setActivityTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCurrentTheme = PreferenceManager.getDefaultSharedPreferences(this).getString(SettingsActivity.KEY_PREF_THEME, "Purple");
        mCurrentFontSize = PreferenceManager.getDefaultSharedPreferences(this).getString(SettingsActivity.KEY_PREF_FONT_SIZE, "0");

        DailyBroadcastReceiver.setupAlarm(this);

        // Fetch all tithi data
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                new TithiGrabber(MainActivity.this).fetchData(new TithiGrabber.Listener() {
                    @Override
                    public void onNewDataFetched() {

                        try {

                            if (mTithiListAdapter != null) {
                                mTithiListAdapter.notifyDataSetChanged();
                            }
                            if (mMonthPagerAdapter != null) {
                                mMonthPagerAdapter.notifyDataSetChanged();
                            }
                            selectDate(mSelectedDate, true);
                        } catch (Exception ignored) {}
                    }
                });
            }
        }, 1000);


        // Tithi list view in bottom sheet
        RecyclerView tithiListView = (RecyclerView)findViewById(R.id.tithi_list_view);
        tithiListView.setLayoutManager(new LinearLayoutManager(this));
        mTithiListAdapter = new TithiListAdapter(this);
        tithiListView.setAdapter(mTithiListAdapter);

        // Month view pager
        mMonthPager = (ViewPager) findViewById(R.id.month_pager);
        mMonthPagerAdapter = new MonthPagerAdapter(getSupportFragmentManager());
        mMonthPager.setAdapter(mMonthPagerAdapter);
        mMonthPager.addOnPageChangeListener(pagerChangeListener);

        // By default scroll to today's month
        scrollToToday();
        findViewById(R.id.today).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollToToday();
            }
        });

        // By default select today
        selectDate(new Date(Calendar.getInstance()).convertToNepali(), true);


        setupBottomSheet();

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if(mMonthPager != null){
            bundle.putInt("LAST_PAGE", mMonthPager.getCurrentItem());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(mMonthPager != null){
            mMonthPagerAdapter.notifyDataSetChanged();
            mMonthPager.setCurrentItem(savedInstanceState.getInt("LAST_PAGE", 0));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String theme = PreferenceManager.getDefaultSharedPreferences(this).getString(SettingsActivity.KEY_PREF_THEME, "Purple");;
        String fontSize = PreferenceManager.getDefaultSharedPreferences(this).getString(SettingsActivity.KEY_PREF_FONT_SIZE, "0");;

        if (!mCurrentTheme.equals(theme) || !mCurrentFontSize.equals(fontSize)) {
            mCurrentTheme = theme;
            mCurrentFontSize = fontSize;
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        refreshFontSize();

        if (getIntent().getIntExtra("day", -1) != -1) {
            Date date = new Date(Calendar.getInstance()).convertToNepali();
            date.day = getIntent().getIntExtra("day", date.day);
            selectDate(date, true);
            getIntent().removeExtra("day");
        } else {
            selectDate(mSelectedDate, true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    private ViewPager.OnPageChangeListener pagerChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(final int position) {
            int year = position/12 + DateUtils.startNepaliYear;
            int month = position%12 + 1;

            // Tithis
            if (mTithiListAdapter != null) {
                mTithiListAdapter.setDate(year, month);
            }

            // Month titles
            String nepali = Translator.getNumber(year + "") + " "
                    + Translator.getMonth(month);

            Date eDate1 = new Date(year, month, 1).convertToEnglish();
            Date eDate2 = new Date(year, month, 26).convertToEnglish();

            String english = DateUtils.getEnglishShort(eDate1.month) + "/"
                    + DateUtils.getEnglishShort(eDate2.month);
            english += " " + eDate1.year + (eDate1.year==eDate2.year?"":"/"+eDate2.year);

            ((TextView)findViewById(R.id.nepali_title)).setText(nepali);
            ((TextView)findViewById(R.id.english_title)).setText(english);


            // Today
            Date today = new Date(Calendar.getInstance()).convertToNepali();
            ((ImageButton)findViewById(R.id.today))
                    .setImageDrawable(ContextCompat.getDrawable(MainActivity.this, ThemeUtils.DATE_ICONS[today.day-1]));
//            ((TextView)findViewById(R.id.today)).setText(Translator.getNumber(today.day+""));

            mCurrentYear = year;
            mCurrentMonth = month;

            if (mRefreshPending) {
                mMonthPagerAdapter.notifyDataSetChanged();
                mRefreshPending = false;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };



    private void setupBottomSheet() {
        findViewById(R.id.bottom_sheet_padding).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                findViewById(R.id.header).dispatchTouchEvent(event);
                return false;
            }
        });

        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                findViewById(R.id.bottom_sheet_padding).setLayoutParams(
                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                (int)(slideOffset*dpToPixels(144-3)+dpToPixels(3)))
                );
            }
        });

    }

    public void nextMonth(View view) {
        mMonthPager.setCurrentItem(mMonthPager.getCurrentItem()+1, true);
    }

    public void previousMonth(View view) {
        mMonthPager.setCurrentItem(mMonthPager.getCurrentItem()-1, true);
    }


    private Date mSelectedDate = null;
    public void pickDate(View view) {
        new YearMonthPicker(new YearMonthPicker.Listener() {
            @Override
            public void onSelect(int year, int month) {
                mMonthPager.setCurrentItem(
                        (year - DateUtils.startNepaliYear) * 12 + (month - 1), true
                );
            }
        }, mCurrentYear, mCurrentMonth).show(getFragmentManager(), "datepicker");
    }

    public Date getSelectedDate() {
        return mSelectedDate;
    }

    public void selectDate(Date date, boolean refreshFragments) {

        if (mTithiListAdapter == null) {
            return;
        }

        mSelectedDate = date;
        if (date != null) {
            ((TextView)findViewById(R.id.tithi_day)).setText(Translator.getNumber(date.day+""));
            ((TextView)findViewById(R.id.tithi_month)).setText(Translator.getMonth(date.month));
            ((TextView)findViewById(R.id.tithi_year)).setText(Translator.getNumber(date.year + ""));

            MitiDb.DateItem dateItem = new MitiDb(this).get(date);

            if (dateItem == null || (dateItem.tithi.equals("") && dateItem.extra.equals(""))) {
                ((TextView)findViewById(R.id.tithi)).setText("");
                ((TextView)findViewById(R.id.tithi_extra)).setText("");
                return;
            }

            ((TextView)findViewById(R.id.tithi)).setText(dateItem.tithi);
            ((TextView)findViewById(R.id.tithi_extra)).setText(dateItem.extra);

            if (dateItem.extra.trim().length() == 0) {
                findViewById(R.id.tithi_extra).setVisibility(View.GONE);
            } else {
                findViewById(R.id.tithi_extra).setVisibility(View.VISIBLE);
            }

            findViewById(R.id.tithi_header).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.tithi_header).setVisibility(View.GONE);
        }

        if (refreshFragments) {
            mMonthPagerAdapter.notifyDataSetChanged();
            mRefreshPending = false;
        } else {
            mRefreshPending = true;
        }

        for (int i=0; i<mTithiListAdapter.getItemCount(); i++) {
            mTithiListAdapter.notifyItemChanged(i);
        }
//        mTithiListAdapter.notifyDataSetChanged();
    }

    public void refreshFontSize() {
        ((TextView)findViewById(R.id.tithi)).setTextSize(ThemeUtils.getFontSize(this, new float[]{10, 12, 14}));
        ((TextView)findViewById(R.id.tithi_extra)).setTextSize(ThemeUtils.getFontSize(this, new float[]{10, 12, 14}));
//        findViewById(R.id.tithi_header).setPadding();
    }

    public void scrollToToday() {
        Date today = new Date(Calendar.getInstance()).convertToNepali();
        if (today.year == mCurrentYear && today.month == mCurrentMonth) {
            selectDate(today, true);
        } else {
            mMonthPager.setCurrentItem(
                    (today.year - DateUtils.startNepaliYear) * 12 + (today.month - 1), true
            );
            mSelectedDate = today;
        }
    }

    public int dpToPixels(int dp) {
        Resources r = getResources();
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    public void openSettings(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }
}
