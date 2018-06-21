package jianqiang.com.activityhook1;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.view.View;

import java.io.File;

import jianqiang.com.activityhook1.ams_hook.AMSHookHelper;
import jianqiang.com.activityhook1.classloder_hook.BaseDexClassLoaderHookHelper;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private static final String apkName = "testservice1.apk";

    private ServiceConnection conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_main);

        findViewById(R.id.btnStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setComponent(
                        new ComponentName("jianqiang.com.testservice1",
                                "jianqiang.com.testservice1.MyService1"));

                startService(intent);
            }
        });

        findViewById(R.id.btnStop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent();
                intent.setComponent(
                        new ComponentName("jianqiang.com.testservice1",
                                "jianqiang.com.testservice1.MyService1"));
                stopService(intent);
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        try {
            Utils.extractAssets(newBase, apkName);

            File dexFile = getFileStreamPath(apkName);
            File optDexFile = getFileStreamPath("testservice1.dex");
            BaseDexClassLoaderHookHelper.patchClassLoader(getClassLoader(), dexFile, optDexFile);

            // 解析插件中的Service组件
            ServiceManager.getInstance().preLoadServices(dexFile);

            AMSHookHelper.hookAMN();

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
