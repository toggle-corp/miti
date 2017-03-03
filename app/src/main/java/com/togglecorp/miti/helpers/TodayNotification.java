package com.togglecorp.miti.helpers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.togglecorp.miti.dateutils.Date;
import com.togglecorp.miti.dateutils.Translator;
import com.togglecorp.miti.dateutils.MitiDb;
import com.togglecorp.miti.ui.MainActivity;
import com.togglecorp.miti.ui.SettingsActivity;

import java.util.Calendar;


public class TodayNotification {

    public static void show(Context context) {
        Date today = new Date(Calendar.getInstance()).convertToNepali();

        // First create title and body of notification
        String title = Translator.getNumber(today.day+" ")
                + Translator.getMonth(today.month) + ", "
                + Translator.getNumber(today.year+"");
        String body = "";
        MitiDb.DateItem dateItem = new MitiDb(context).get(today.toString());
        if (dateItem != null) {
            body = dateItem.tithi + ((dateItem.extra.length() > 0) ? (", " + dateItem.extra) : "");
        }

        // Build a notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(ThemeUtils.DATE_ICONS[today.day-1])
                        .setContentTitle(title)
                        .setContentText(body)
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                        .setShowWhen(false)
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
