package com.sososeen09.host.hook;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.res.Resources;

import com.sososeen09.host.PluginClassLoader;

/**
 * Created by yunlong.su on 2018/4/8.
 */

public class PluginApk {
    private PackageInfo packageInfo;
    private Resources resources;
    private PluginClassLoader dexClassLoader;
    private ApplicationInfo applicationInfo;
    private Object loadedApk;

    public PluginApk() {
    }

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public void setPackageInfo(PackageInfo packageInfo) {
        this.packageInfo = packageInfo;
    }

    public Resources getResources() {
        return resources;
    }

    public void setResources(Resources resources) {
        this.resources = resources;
    }

    public PluginClassLoader getDexClassLoader() {
        return dexClassLoader;
    }

    public void setDexClassLoader(PluginClassLoader dexClassLoader) {
        this.dexClassLoader = dexClassLoader;
    }

    public ApplicationInfo getApplicationInfo() {
        return applicationInfo;
    }

    public void setApplicationInfo(ApplicationInfo applicationInfo) {
        this.applicationInfo = applicationInfo;
    }

    public void setLoadedApk(Object loadedApk) {
        this.loadedApk = loadedApk;
    }

    public Object getLoadedApk() {
        return loadedApk;
    }
}
