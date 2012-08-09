package com.hotcats.textreminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * A BroadcastReceiver that gets called when the alarm goes off.
 */
public class AlarmReceiver extends BroadcastReceiver {

    public static final Uri SMS_INBOX = Uri.parse("content://sms/inbox");

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("alarm", "alarm ring received");

        Cursor c = context.getContentResolver().query(SMS_INBOX, null, "read = 0", null, null);
        int unread = c.getCount();
        c.close();
        Log.i("alarm", "found " + unread + " unread texts");
        if (unread == 0) {
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(TextReceiver.ALARM_RING);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
            am.cancel(pi);
            Log.i("alarm", "cancelled alarm");
        }
    }
}
