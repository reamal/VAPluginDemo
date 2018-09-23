package com.lilee.plugin.first;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PluginReceiver extends BroadcastReceiver {
    private static final String TAG = "liTag";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"PluginReceiver onReceive ");
    }
}
