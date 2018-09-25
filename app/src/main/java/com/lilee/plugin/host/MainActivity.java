package com.lilee.plugin.host;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Process;
import android.provider.Settings;
import android.support.annotation.NonNull;
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

    private static final int PERMISSION_REQUEST_CODE_STORAGE = 2018;
    private static final String TAG = "liTag";
    String apkName = "firstplugin-beijing-release.apk";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        Utils.extractAssets(this, apkName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (hasPermission()) {
            Log.d(TAG, "loadPlugin");
            loadPlugin();
        } else {
            requestPermission();
        }

        findViewById(R.id.btn).setOnClickListener(v -> startActivity());
        findViewById(R.id.btn_startService).setOnClickListener(v -> startService());
        findViewById(R.id.btn_bindService).setOnClickListener(v -> bindService());
        findViewById(R.id.btn_unbindService).setOnClickListener(v -> unbindService());
        findViewById(R.id.btn_stopService).setOnClickListener(v -> stopService());
        findViewById(R.id.btn_cpInsert).setOnClickListener(v -> cpInsert());
        findViewById(R.id.btn_sendBroadcastReceiver).setOnClickListener(v -> sendBroadcastReceiver());

    }

    private void loadPlugin() {
        File apk = getFileStreamPath(apkName);

        if (apk.exists()) {
            try {
                PluginManager.getInstance(this).loadPlugin(apk);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }
        } else {
            Toast.makeText(this, "plugin apk not exists !!!", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "plugin apk not exists !!!");
        }
    }

    private boolean hasPermission() {
        Log.d(TAG, "hasPermission");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }


    private void requestPermission() {

        Log.d(TAG, "requestPermission");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean b = shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (b) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE_STORAGE);
            } else {
                Toast.makeText(this, "需要读取SD卡权限", Toast.LENGTH_SHORT).show();
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle("权限申请提示：")
                        .setMessage("需要读取SD卡权限，前往设置打开")
                        .setPositiveButton("确定", (dialog, which) -> goSettingPage())
                        .setNegativeButton("退出", (dialog, which) -> Process.killProcess(Process.myPid())).create();
                alertDialog.show();
            }
        }
    }

    private void goSettingPage() {
        Context context = MainActivity.this.getApplicationContext();
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        MainActivity.this.startActivityForResult(intent, PERMISSION_REQUEST_CODE_STORAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_STORAGE:
                if (hasPermission()) {
                    Log.d(TAG, "loadPlugin");
                    loadPlugin();
                } else {
                    requestPermission();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (PERMISSION_REQUEST_CODE_STORAGE == requestCode) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                requestPermission();
            } else {
                loadPlugin();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void sendBroadcastReceiver() {
        sendBroadcast(new Intent("plugin_receiver_one"));
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
