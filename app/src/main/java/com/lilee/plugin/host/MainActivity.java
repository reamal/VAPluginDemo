package com.lilee.plugin.host;

import android.app.Service;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.didi.virtualapk.PluginManager;
import com.didi.virtualapk.internal.LoadedPlugin;
import com.didi.virtualapk.internal.PluginContentResolver;
import com.lilee.plugin.lib.IMyInterface;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "liTag";
    String apkName = "firstplugin-beijing-release.apk";
    // demo ContentProvider 的URI

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

        findViewById(R.id.btn).setOnClickListener(v -> startActivity());
        findViewById(R.id.btn_startService).setOnClickListener(v -> startService());
        findViewById(R.id.btn_bindService).setOnClickListener(v -> bindService());
        findViewById(R.id.btn_unbindService).setOnClickListener(v -> unbindService());
        findViewById(R.id.btn_stopService).setOnClickListener(v -> stopService());
        findViewById(R.id.btn_cpInsert).setOnClickListener(v -> cpInsert());

    }

    private void cpInsert() {

        String pkg = "com.lilee.plugin.first";
        LoadedPlugin plugin = PluginManager.getInstance(this).getLoadedPlugin(pkg);
        Uri cpUri = Uri.parse("content://com.lilee.plugin.first.Lilee");
        cpUri = PluginContentResolver.wrapperUri(plugin, cpUri);
        Integer count = getContentResolver().delete(cpUri, "where", null);
        Toast.makeText(MainActivity.this, String.valueOf(count), Toast.LENGTH_LONG).show();
    }

    private void startActivity() {
        Intent intent = new Intent();
        intent.setClassName(MainActivity.this, "com.lilee.plugin.first.MainActivity");
        startActivity(intent);
    }

    private void startService() {
        Intent intent = new Intent();
        intent.setClassName(MainActivity.this, "com.lilee.plugin.first.PluginService");
        startService(intent);
    }

    public void bindService() {
        Intent intent = new Intent();
        intent.setClassName(MainActivity.this, "com.lilee.plugin.first.PluginService");
        bindService(intent, conn, Service.BIND_AUTO_CREATE);
    }

    public void unbindService() {
        unbindService(conn);
    }

    public void stopService() {
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
