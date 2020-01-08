package com.sudiyi.apps.cameracheck.util;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.sudiyi.apps.cameracheck.util.log.LogUtils;

/**
 * implement service polling.
 *
 * @author pine
 * @date 2014-8-14
 */
public class PollingUtils {

    /**
     * start polling
     *
     * @param context
     * @param seconds 时间
     */
    public static void startPollingService(Context context, int seconds, String action) {
        LogUtils.d("startPollingService:" + action);

        //get AlarmManager
        AlarmManager manager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);

        //create PendingIntent
        Intent intent = new Intent();
        intent.setAction(action);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 456,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);

        //set repeat and start AlarmManager
        /*registerExactAlarm(context,pendingIntent,seconds * 1000);*/
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), seconds * 1000, pendingIntent);
    }


    public static void startAlarmRepeat(Context context, PendingIntent pendingIntent, int seconds) {
        //get AlarmManager
        AlarmManager manager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        //set repeat and start AlarmManager
        /*registerExactAlarm(context,pendingIntent,seconds * 1000);*/
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), seconds * 1000, pendingIntent);
    }

    public static void startAlarm(Context context, int seconds, String action) {
        //create PendingIntent
        LogUtils.d("startPollingService:" + action);
        Intent intent = new Intent();
        intent.setAction(action);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 456,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);

        registerExactAlarm(context, pendingIntent, seconds * 1000);
    }

    private static void registerExactAlarm(Context context, PendingIntent sender, long delayInMillis) {
        final int SDK_INT = Build.VERSION.SDK_INT;
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long timeInMillis = (System.currentTimeMillis() + delayInMillis) / 1000 * 1000;

        if (SDK_INT < Build.VERSION_CODES.KITKAT) {
            am.set(AlarmManager.RTC_WAKEUP, timeInMillis, sender);
        } else if (Build.VERSION_CODES.KITKAT <= SDK_INT && SDK_INT < Build.VERSION_CODES.M) {
            am.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, sender);
        } else if (SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, sender);
        }
    }

    /**
     * stop polling
     *
     * @param context
     * @param action
     */
    public static void stopPollingService(Context context, String action) {

        //get AlarmManager
        AlarmManager manager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);

        //create PendingIntent
        Intent intent = new Intent();
        intent.setAction(action);
        PendingIntent pendingIntent = PendingIntent.getService(context, 456,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //stop polling
        manager.cancel(pendingIntent);

        LogUtils.d("stopPollingService:" + action);
    }
}