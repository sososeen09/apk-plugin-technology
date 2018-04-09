package com.sososeen09.host.internal;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.content.res.Resources;

import com.sososeen09.host.hook.PluginApk;

/**
 * Created by yunlong.su on 2018/4/9.
 */

public class PluginContext extends ContextWrapper {
    private final PluginApk mPluginApk;

    public PluginContext(PluginApk pluginApk) {
        super(pluginApk.getHostContext());
        this.mPluginApk = pluginApk;
    }

    @Override
    public Context getApplicationContext() {
        return this.mPluginApk.getApplication();
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.mPluginApk.getClassLoader();
    }


    @Override
    public Resources getResources() {
        return this.mPluginApk.getResources();
    }

    @Override
    public AssetManager getAssets() {
        return this.mPluginApk.getAssets();
    }

    @Override
    public Resources.Theme getTheme() {
        return this.mPluginApk.getTheme();
    }
}
