package com.togglecorp.miti.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.TypedValue;

import com.togglecorp.miti.R;
import com.togglecorp.miti.ui.SettingsActivity;

/**
 * Helper class to work on theme related jobs.
 */
public class ThemeUtils {


    public static int getThemeColor(Context context, int attribute) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(attribute, value, true);
        return value.data;
    }

    public static void setActivityTheme(Activity activity) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        String themeName = preferences.getString(SettingsActivity.KEY_PREF_THEME, "Purple");

        switch (themeName) {
            case "Purple":
                activity.setTheme(R.style.PurpleTheme);
                break;
            case "Green":
                activity.setTheme(R.style.GreenTheme);
                break;
            case "Red":
                activity.setTheme(R.style.RedTheme);
                break;
        }
    }
}
