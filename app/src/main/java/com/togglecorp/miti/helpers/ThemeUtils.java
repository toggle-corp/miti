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
    public static final int DATE_ICONS[] = {
            R.drawable.date_icon_1,
            R.drawable.date_icon_2,
            R.drawable.date_icon_3,
            R.drawable.date_icon_4,
            R.drawable.date_icon_5,
            R.drawable.date_icon_6,
            R.drawable.date_icon_7,
            R.drawable.date_icon_8,
            R.drawable.date_icon_9,
            R.drawable.date_icon_10,
            R.drawable.date_icon_11,
            R.drawable.date_icon_12,
            R.drawable.date_icon_13,
            R.drawable.date_icon_14,
            R.drawable.date_icon_15,
            R.drawable.date_icon_16,
            R.drawable.date_icon_17,
            R.drawable.date_icon_18,
            R.drawable.date_icon_19,
            R.drawable.date_icon_20,
            R.drawable.date_icon_21,
            R.drawable.date_icon_22,
            R.drawable.date_icon_23,
            R.drawable.date_icon_24,
            R.drawable.date_icon_25,
            R.drawable.date_icon_26,
            R.drawable.date_icon_27,
            R.drawable.date_icon_28,
            R.drawable.date_icon_29,
            R.drawable.date_icon_30,
            R.drawable.date_icon_31,
            R.drawable.date_icon_32,
    };


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
            case "Black":
                activity.setTheme(R.style.BlackTheme);
                break;
            case "White":
                activity.setTheme(R.style.WhiteTheme);
                break;
            default:
                activity.setTheme(R.style.PurpleTheme);
        }
    }
}
