package com.sososeen09.host.mergeway;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.ContextThemeWrapper;

import com.sososeen09.host.utils.ReflectUtil;

/**
 * Created by yunlong on 2018/4/10.
 */

public class PluginInstrumentation extends Instrumentation {
    private Instrumentation mBase;

    public PluginInstrumentation(Instrumentation base) {
        mBase = base;
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        final Intent intent = activity.getIntent();
        Context base = activity.getBaseContext();
        try {
            MergedPluginApk plugin = MergedPluginHelper.getInstance().getMergedPluginApk(intent.getComponent().getPackageName());
            if (plugin != null) {
                ReflectUtil.setField(base.getClass(), base, "mResources", plugin.getResources());
                ReflectUtil.setField(ContextWrapper.class, activity, "mBase", plugin.getPluginContext());
                ReflectUtil.setFieldNoException(ContextThemeWrapper.class, activity, "mBase", plugin.getPluginContext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mBase.callActivityOnCreate(activity, icicle);
    }

}
