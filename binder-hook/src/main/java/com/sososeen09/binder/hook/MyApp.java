package com.sososeen09.binder.hook;

import android.app.Application;

import com.sososeen09.binder.hook.utils.SPHelper;

/**
 * Created by yunlong.su on 2018/4/3.
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SPHelper.init(this);
        HookUtils hookUtils = new HookUtils();
        hookUtils.initHook(this);
    }
}
