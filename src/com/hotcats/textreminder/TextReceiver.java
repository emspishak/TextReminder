package com.hotcats.textreminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Receives text messages and alarm rings to alert the user if there are unread
 * texts.
 */
public class TextReceiver extends BroadcastReceiver {

    public static final String ALARM_RING = "com.hotcats.textreminder.TextReceiver.ALARM_RING";
    public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    public static final Uri SMS_INBOX = Uri.parse("content://sms/inbox");

    public static final long[] VIBRATE_PATTERN = { 0, 250, 250, 250 };

    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(ALARM_RING);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);

        if (ALARM_RING.equals(intent.getAction())) {
            handleAlarm(context, am, pi);
        } else if (SMS_RECEIVED.equals(intent.getAction())) {
            handleText(context, am, pi);
        } else {
            Log.w("all", "invalid intent received: " + intent.getAction());
        }
    }

    /**
     * Handle an alarm ring: if there are unread texts alert the user, otherwise
     * cancel the alarm.
     */
    private void handleAlarm(Context context, AlarmManager am, PendingIntent pi) {
        Log.i("alarm", "alarm ring received");

        Cursor c = context.getContentResolver().query(SMS_INBOX, null,
                "read = 0", null, null);
        int unread = c.getCount();
        c.close();
        Log.i("alarm", "found " + unread + " unread texts");
        if (unread == 0) {
            am.cancel(pi);
            Log.i("alarm", "cancelled alarm");
        } else {
            Vibrator v = (Vibrator) context
                    .getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(VIBRATE_PATTERN, -1);
        }
    }

    /**
     * Handle receiving a text: set an alarm to alert the user.
     */
    private void handleText(Context context, AlarmManager am, PendingIntent pi) {
        Log.i("text", "text message recieved!");

        // TODO: How can look this up only once instead of every time this
        // method is called?
        String repeatDelayDefault = context.getResources().getString(
                R.string.pref_repeatDelay_default);

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        int repeatDelay = 1000 * Integer.parseInt(prefs.getString(
                Preferences.PREF_REPEAT_DELAY, repeatDelayDefault));

        Log.i("text", "setting alarm to repeat every " + repeatDelay + " ms");
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                + repeatDelay, repeatDelay, pi);
    }
}
