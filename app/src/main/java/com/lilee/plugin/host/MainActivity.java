package com.lilee.plugin.host;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.didi.virtualapk.PluginManager;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "liTag";
    String apkName = "firstplugin.apk";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        Utils.extractAssets(this,apkName);
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

    }
}
