package com.lilee.plugin.first;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class PluginService extends Service{

    private MyBinder binder = new MyBinder();
    private int count;

    public class MyBinder extends Binder implements IMyInterface {
        public int getCount() {
            return count;
        }
    }

    private static final String TAG = "TestService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "Service is binded");
        count = count + 1;
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG, "Service is onUnbinded");
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, TAG + " onDestroy");
    }
}
