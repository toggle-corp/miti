package com.togglecorp.miti.helpers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Pair;

import com.togglecorp.miti.R;
import com.togglecorp.miti.dateutils.Date;
import com.togglecorp.miti.dateutils.NepaliTranslator;
import com.togglecorp.miti.dateutils.TithiDb;
import com.togglecorp.miti.ui.MainActivity;
import com.togglecorp.miti.ui.SettingsActivity;

import java.util.Calendar;


public class TodayNotification {

    public static void show(Context context) {
        Date today = new Date(Calendar.getInstance()).convertToNepali();

        // First create title and body of notification
        String title = NepaliTranslator.getNumber(today.day+" ")
                + NepaliTranslator.getMonth(today.month) + ", "
                + NepaliTranslator.getNumber(today.year+"");
        String body = "";
        Pair<String, String> tithi = new TithiDb(context).get(today.toString());
        if (tithi != null) {
            body = tithi.first + ((tithi.second.length() > 0) ? (", " + tithi.second) : "");
        }

        // Build a notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(ThemeUtils.DATE_ICONS[today.day-1])
                        .setContentTitle(title)
                        .setContentText(body)
                        .setOngoing(true);
        Intent resultIntent = new Intent(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(100, mBuilder.build());
    }

    public static void clear(Context context) {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(100);
    }

    public static void refresh(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getBoolean(SettingsActivity.KEY_PREF_SHOW_DAILY_NOTIFICATION, false)) {
            show(context);
        } else {
            clear(context);
        }
    }
}
