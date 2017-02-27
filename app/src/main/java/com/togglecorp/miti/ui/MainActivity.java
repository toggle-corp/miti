package com.togglecorp.miti.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.togglecorp.miti.R;
import com.togglecorp.miti.dateutils.TithiGrabber;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Fetch all tithi data
        new TithiGrabber(this).fetchData(new TithiGrabber.Listener() {
            @Override
            public void onNewDataFetched() {
            }
        });
    }
}
