package com.togglecorp.miti.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.togglecorp.miti.R;
import com.togglecorp.miti.helpers.DailyBroadcastReceiver;
import com.togglecorp.miti.helpers.ThemeUtils;
import com.togglecorp.miti.helpers.TodayNotification;

public class SettingsActivity extends AppCompatActivity {
    public static final String KEY_PREF_SHOW_DAILY_NOTIFICATION = "pref_show_daily_notification";
    public static final String KEY_PREF_THEME = "pref_theme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.setActivityTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // The toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        setTitle("Settings");

        // Settings fragment
        getFragmentManager().beginTransaction()
                .replace(R.id.content, new SettingsFragment())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(KEY_PREF_SHOW_DAILY_NOTIFICATION)) {
                DailyBroadcastReceiver.setupAlarm(getActivity());
            }
            else if (key.equals(KEY_PREF_THEME)) {
                getActivity().startActivity(new Intent(getActivity(), SettingsActivity.class));
                getActivity().finish();
            }
        }
    }

}
