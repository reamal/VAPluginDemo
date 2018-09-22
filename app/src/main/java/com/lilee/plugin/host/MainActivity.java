package com.lilee.plugin.host;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.didi.virtualapk.PluginManager;
import com.lilee.plugin.lib.IMyInterface;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "liTag";
    String apkName = "firstplugin.apk";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        Utils.extractAssets(this, apkName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        File apk = getFileStreamPath(apkName);

        if (apk.exists()) {
            try {
                PluginManager.getInstance(this).loadPlugin(apk);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        } else {
            Toast.makeText(this, "plugin apk not exists !!!", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "plugin apk not exists !!!");
        }

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(MainActivity.this, "com.lilee.plugin.first.MainActivity");
                startActivity(intent);
            }
        });


        findViewById(R.id.btn_startService).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(MainActivity.this, "com.lilee.plugin.first.PluginService");
                startService(intent);
            }
        });


        findViewById(R.id.btn_bindService).setOnClickListener(v ->
                bindService(v)
        );
        findViewById(R.id.btn_unbindService).setOnClickListener(v ->
                unbindService(v)
        );
        findViewById(R.id.btn_stopService).setOnClickListener(v ->
                stopService(v)
        );
    }

    public void bindService(View view) {
        Intent intent = new Intent();
        intent.setClassName(MainActivity.this, "com.lilee.plugin.first.PluginService");
        bindService(intent, conn, Service.BIND_AUTO_CREATE);
    }

    public void unbindService(View view) {
        unbindService(conn);
    }

    public void stopService(View view) {
        Intent intent = new Intent();
        intent.setClassName(MainActivity.this, "com.lilee.plugin.first.PluginService");
        stopService(intent);
    }


    ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            IMyInterface a = (IMyInterface) service;
            int result = a.getCount();
            Log.e(TAG, String.valueOf(result));
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "onServiceDisconnected");
        }
    };
}
