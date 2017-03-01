package com.togglecorp.miti.ui;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.togglecorp.miti.R;
import com.togglecorp.miti.adapters.MonthPagerAdapter;
import com.togglecorp.miti.adapters.TithiListAdapter;
import com.togglecorp.miti.dateutils.Date;
import com.togglecorp.miti.dateutils.DateUtils;
import com.togglecorp.miti.dateutils.NepaliTranslator;
import com.togglecorp.miti.dateutils.TithiDb;
import com.togglecorp.miti.dateutils.TithiGrabber;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ViewPager mMonthPager;
    private MonthPagerAdapter mMonthPagerAdapter;
    private TithiListAdapter mTithiListAdapter;

    private int mCurrentYear, mCurrentMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Fetch all tithi data
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new TithiGrabber(MainActivity.this).fetchData(new TithiGrabber.Listener() {
                    @Override
                    public void onNewDataFetched() {
                        if (mTithiListAdapter != null) {
                            mTithiListAdapter.notifyDataSetChanged();
                        }
                        if (mMonthPagerAdapter != null) {
                            mMonthPagerAdapter.notifyDataSetChanged();
                        }
                        selectTithi(mSelectedTithi);
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
        selectTithi(new Date(Calendar.getInstance()).convertToNepali());


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
            String nepali = NepaliTranslator.getNumber(year + "") + " "
                    + NepaliTranslator.getMonth(month);

            Date eDate1 = new Date(year, month, 1).convertToEnglish();
            Date eDate2 = new Date(year, month, 26).convertToEnglish();

            String english = DateUtils.getEnglishMonth(eDate1.month) + "/"
                    + DateUtils.getEnglishMonth(eDate2.month);
            english += " " + eDate1.year + (eDate1.year==eDate2.year?"":"/"+eDate2.year);

            ((TextView)findViewById(R.id.nepali_title)).setText(nepali);
            ((TextView)findViewById(R.id.english_title)).setText(english);


            // Today
            Date today = new Date(Calendar.getInstance()).convertToNepali();
            ((TextView)findViewById(R.id.today)).setText(NepaliTranslator.getNumber(today.day+""));

            mCurrentYear = year;
            mCurrentMonth = month;
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
                                (int)(slideOffset*dpToPixels(160-3)+dpToPixels(3)))
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


    private Date mSelectedTithi = null;
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

    public Date getSelectedTithi() {
        return mSelectedTithi;
    }

    public void selectTithi(Date date) {

        if (mTithiListAdapter == null) {
            return;
        }

        mSelectedTithi = date;
        if (date != null) {
            ((TextView)findViewById(R.id.tithi_day)).setText(NepaliTranslator.getNumber(date.day+""));
            ((TextView)findViewById(R.id.tithi_month)).setText(NepaliTranslator.getMonth(date.month));
            ((TextView)findViewById(R.id.tithi_year)).setText(NepaliTranslator.getNumber(date.year + ""));

            Pair<String, String> tithi = new TithiDb(this).get(String.format(Locale.US, "%04d-%02d-%02d", date.year, date.month, date.day));

            if (tithi == null || (tithi.first.equals("") && tithi.second.equals(""))) {
                ((TextView)findViewById(R.id.tithi)).setText("");
                ((TextView)findViewById(R.id.tithi_extra)).setText("");
                return;
            }

            ((TextView)findViewById(R.id.tithi)).setText(tithi.first);
            ((TextView)findViewById(R.id.tithi_extra)).setText(tithi.second);

            if (tithi.second.trim().length() == 0) {
                findViewById(R.id.tithi_extra).setVisibility(View.GONE);
            } else {
                findViewById(R.id.tithi_extra).setVisibility(View.VISIBLE);
            }

            findViewById(R.id.tithi_header).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.tithi_header).setVisibility(View.GONE);
        }

        mMonthPagerAdapter.notifyDataSetChanged();
    }

    public void scrollToToday() {
        Date today = new Date(Calendar.getInstance()).convertToNepali();
        mMonthPager.setCurrentItem(
                (today.year - DateUtils.startNepaliYear) * 12 + (today.month - 1), true
        );
    }

    public int dpToPixels(int dp) {
        Resources r = getResources();
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }
}
