package com.togglecorp.miti.ui;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.togglecorp.miti.R;
import com.togglecorp.miti.adapters.MonthPagerAdapter;
import com.togglecorp.miti.adapters.TithiListAdapter;
import com.togglecorp.miti.dateutils.Date;
import com.togglecorp.miti.dateutils.DateUtils;
import com.togglecorp.miti.dateutils.NepaliTranslator;
import com.togglecorp.miti.dateutils.TithiGrabber;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ViewPager mMonthPager;
    private PagerAdapter mMonthPagerAdapter;
    private TithiListAdapter mTithiListAdapter;

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
                        mMonthPagerAdapter.notifyDataSetChanged();
                    }
                });
            }
        }, 5000);


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
        Date today = new Date(Calendar.getInstance()).convertToNepali();
        mMonthPager.setCurrentItem(
                (today.year - DateUtils.startNepaliYear) * 12 + (today.month - 1)
        );

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
            String todayStringNepali =  NepaliTranslator.getNumber(today.day+" ")
                    + NepaliTranslator.getMonth(today.month) + ", "
                    + NepaliTranslator.getNumber(today.year + "");
            ((TextView)findViewById(R.id.today)).setText(todayStringNepali);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };



//    private float mBottomSheetTouchY;
    private void setupBottomSheet() {
        findViewById(R.id.bottom_sheet_padding).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                findViewById(R.id.month_pager).dispatchTouchEvent(event);
                return true;
            }
        });
//        View bottomSheet = findViewById(R.id.bottom_sheet);
//        bottomSheet.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                mBottomSheetTouchY = event.getY();
//                return true;
//            }
//        });
//
//        final BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
//        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//            @Override
//            public void onStateChanged(@NonNull View bottomSheet, int newState) {
//                Log.d(TAG, newState+"");
//                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
//                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                }
//            }
//
//            @Override
//            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//
//            }
//        });

    }
}
