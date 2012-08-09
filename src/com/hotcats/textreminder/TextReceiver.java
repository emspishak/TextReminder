package com.hotcats.textreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * A BroadcastReceiver that gets called when a text message is received.
 */
public class TextReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("text", "text message recieved!");
    }
}
