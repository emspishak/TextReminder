package com.hotcats.textreminder;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Receives text messages and alarm rings to alert the user if there are unread
 * texts.
 */
public class TextReceiver extends BroadcastReceiver {

    public static final String ALARM_RING = "com.hotcats.textreminder.TextReceiver.ALARM_RING";
    public static final String IDLE_PHONE_STATE = "IDLE";
    public static final String NOTIFICATION_CLICK = "NOTIFICATION_CLICK";
    public static final String PHONE_STATE = "android.intent.action.PHONE_STATE";
    public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    public static final int CANCEL_NOTIFICATION_ID = 132;

    public static final Uri SMS_INBOX = Uri.parse("content://sms/inbox");

    public static final long[] VIBRATE_PATTERN = { 0, 250, 250, 250 };

    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);

        PendingIntent pi = Utilities.constructAlarmPendingIntent(context);

        if (ALARM_RING.equals(intent.getAction())) {
            handleAlarm(context, am, pi);
        } else if (SMS_RECEIVED.equals(intent.getAction())) {
            handleText(context, am, pi);
        } else if (PHONE_STATE.equals(intent.getAction())) {
            handlePhoneState(context, intent);
        } else if (NOTIFICATION_CLICK.equals(intent.getAction())) {
            handleNotificationClick(am, pi);
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
            Utilities.cancelAlarm(am, pi);
            Log.i("alarm", "cancelled alarm");

            NotificationManager nManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            nManager.cancel(CANCEL_NOTIFICATION_ID);
            Log.i("notification", "cancelled notification");
        } else {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(context);
            String phoneState = prefs.getString(PHONE_STATE, IDLE_PHONE_STATE);

            if (IDLE_PHONE_STATE.equals(phoneState)) {
                Vibrator v = (Vibrator) context
                        .getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(VIBRATE_PATTERN, -1);
            } else {
                Log.i("alarm", "call active, not vibrating");
            }
        }
    }

    /**
     * Handle receiving a text: set an alarm to alert the user.
     */
    private void handleText(Context context, AlarmManager am, PendingIntent pi) {
        Log.i("text", "text message received!");

        boolean enabledDefault = context.getResources().getBoolean(
                R.bool.pref_enabled_default);

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        boolean enabled = prefs.getBoolean(Preferences.PREF_ENABLED,
                enabledDefault);
        if (enabled) {
            String repeatDelayDefault = context.getResources().getString(
                    R.string.pref_repeatDelay_default);

            int repeatDelay = 1000 * Integer.parseInt(prefs.getString(
                    Preferences.PREF_REPEAT_DELAY, repeatDelayDefault));

            Log.i("text", "setting alarm to repeat every " + repeatDelay
                    + " ms");
            am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                    + repeatDelay, repeatDelay, pi);

            Notification cancelNotification = createCancelNotification(context);
            NotificationManager nManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            Log.i("text", "setting notification");
            nManager.notify(CANCEL_NOTIFICATION_ID, cancelNotification);
        } else {
            Log.i("text", "disabled, not setting alarm");
        }
    }

    /**
     * Build up a notification for cancellation of the current text reminder
     */
    private Notification createCancelNotification(Context context) {
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(
                context);
        nBuilder.setContentTitle("Cancel current text reminder");
        nBuilder.setAutoCancel(true);
        nBuilder.setSmallIcon(R.drawable.ic_launcher);
        Intent i = new Intent(context, TextReceiver.class);
        i.setAction(NOTIFICATION_CLICK);
        PendingIntent npi = PendingIntent.getBroadcast(context, 0, i, 0);
        nBuilder.setContentIntent(npi);
        return nBuilder.getNotification();
    }

    /**
     * Handle a change in the phone state: store it in SharedPreferences so the
     * user won't be alerted when in a call.
     */
    private void handlePhoneState(Context context, Intent intent) {
        String phoneState = intent.getExtras().getString("state");
        Log.i("phone", "phone state changed to: " + phoneState);

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = prefs.edit();
        editor.putString(PHONE_STATE, phoneState);
        editor.commit();
    }

    /**
     * Handle a click on the "cancel" notification: disable alerts by cancelling
     * the alarm.
     */
    private void handleNotificationClick(AlarmManager am, PendingIntent pi) {
        Log.i("notification", "notification clicked, cancelling alarm");
        Utilities.cancelAlarm(am, pi);
    }
}
