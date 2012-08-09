package com.hotcats.textreminder;

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
    }
}
