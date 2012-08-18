package com.hotcats.textreminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Various static utility functions.
 */
public class Utilities {
    private Utilities() {
        throw new AssertionError("don't construct me!");
    }

    /**
     * Creates a new {@link PendingIntent} used for the text reminder alarm.
     */
    public static PendingIntent constructAlarmPendingIntent(Context context) {
        Intent i = new Intent(TextReceiver.ALARM_RING);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        return pi;
    }

    /**
     * Cancels the {@link TextReceiver.ALARM_RING} alarm.
     */
    public static void cancelAlarm(Context context, AlarmManager am) {
        cancelAlarm(am, constructAlarmPendingIntent(context));
    }

    /**
     * Cancels the {@link TextReceiver.ALARM_RING} alarm. Note that the
     * PendingIntent must be created with the
     * {@link #constructAlarmPendingIntent(Context)} method.
     */
    public static void cancelAlarm(AlarmManager am, PendingIntent pi) {
        am.cancel(pi);
    }
}
