package com.hotcats.textreminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * A BroadcastReceiver that gets called when a text message is received.
 */
public class TextReceiver extends BroadcastReceiver {

	public static final int SECOND = 3000;
	public static final int MINUTE = 1000 * 60;
	public static final String ALARM_RING = "com.hotcats.textreminder.TextReceiver.ALARM_RING";

	/**
	 * Receive the text message and set an alarm.
	 */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("text", "text message recieved!");

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(ALARM_RING);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + SECOND, SECOND, pi);
    }
}
