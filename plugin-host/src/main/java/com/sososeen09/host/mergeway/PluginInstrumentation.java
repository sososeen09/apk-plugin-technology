package com.sososeen09.host.mergeway;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextThemeWrapper;

import com.sososeen09.host.internal.Constants;
import com.sososeen09.host.utils.ReflectUtil;

import java.lang.reflect.Field;

import static com.sososeen09.host.internal.Constants.LAUNCH_ACTIVITY;

/**
 * Created by yunlong on 2018/4/10.
 */

public class PluginInstrumentation extends Instrumentation implements Handler.Callback{
    private Instrumentation mBase;

    public PluginInstrumentation(Instrumentation base) {
        mBase = base;
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        final Intent intent = activity.getIntent();
        Context base = activity.getBaseContext();

        try {
            Class contextThemeWrapperClass = Class.forName("android.view.ContextThemeWrapper");
            MergedPluginApk plugin = MergedPluginHelper.getInstance().getMergedPluginApk(intent.getComponent().getPackageName());
            if (plugin != null) {
                ReflectUtil.setField(contextThemeWrapperClass, activity, "mResources", plugin.getResources());
                ReflectUtil.setField(ContextWrapper.class, activity, "mBase", plugin.getPluginContext());
                ReflectUtil.setFieldNoException(ContextThemeWrapper.class, activity, "mBase", plugin.getPluginContext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mBase.callActivityOnCreate(activity, icicle);
    }


    @Override
    public boolean handleMessage(Message msg) {
        // 跳转过程中发现如果被跳转的Activity继承自AppCompatActivity会报异常android.content.pm.PackageManager$NameNotFoundException:
        // 这是因为被跳转的Activity没有在AndroidManifest.xml中注册
        // 而AppCompatActivity在onCreate方法中会调用NavUtils.getParentActivityName方法，在这个方法中PackageManager会调用getActivityInfo
        // 实际上会再检查一次Activity的合法性
        if (msg.what == LAUNCH_ACTIVITY) {
            handleLaunchActivity(msg);
        }
        return false;
    }

    private void handleLaunchActivity(Message msg) {
        //替换我们真正想要的intent
        try {
            Object activityClientRecord = msg.obj;
            Field intentField = activityClientRecord.getClass().getDeclaredField("intent");
            intentField.setAccessible(true);
            //如果与宿主包名一样，就直接跳转
            Intent interceptedIntent = (Intent) intentField.get(activityClientRecord);

            //包名不一样，把ProxyActivity替换为真正想要跳转的 SecondActivity
            Intent realWanted = interceptedIntent.getParcelableExtra(Constants.EXTRA_TARGET);
            if (realWanted != null) {
                interceptedIntent.setComponent(realWanted.getComponent());

                // 由于需要获取插件对应的LoadedApk，需要传正确的key值，也就是packageName
                Field activityInfoField = activityClientRecord.getClass().getDeclaredField("activityInfo");
                activityInfoField.setAccessible(true);

                // 根据 getPackageInfo 根据这个 包名获取 LoadedApk的信息; 因此这里我们需要手动填上, 从而能够命中缓存
                ActivityInfo activityInfo = (ActivityInfo) activityInfoField.get(activityClientRecord);

                activityInfo.applicationInfo.packageName = realWanted.getPackage() == null ?
                        realWanted.getComponent().getPackageName() : realWanted.getPackage();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
