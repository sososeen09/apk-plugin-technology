package com.sososeen09.host.hook;

import android.app.Application;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;

import com.sososeen09.host.PluginClassLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yunlong.su on 2018/4/8.
 */

public class PluginApk {
    private PackageInfo mPackageInfo;
    private Resources mResources;
    private PluginClassLoader mClassLoader;
    private ApplicationInfo mApplicationInfo;
    private Object mLoadedApk;
    private Map<ComponentName, ServiceInfo> mServiceInfoMap = new HashMap<>();
    private Application mApplication;
    private PluginManager mPluginManager;
    public PluginApk(PluginManager pluginManager) {
        this.mPluginManager = pluginManager;
    }

    public PackageInfo getPackageInfo() {
        return mPackageInfo;
    }

    public void setPackageInfo(PackageInfo packageInfo) {
        this.mPackageInfo = packageInfo;
    }

    public Resources getResources() {
        return mResources;
    }

    public void setResources(Resources resources) {
        this.mResources = resources;
    }

    public PluginClassLoader getClassLoader() {
        return mClassLoader;
    }

    public void setClassLoader(PluginClassLoader classLoader) {
        this.mClassLoader = classLoader;
    }

    public ApplicationInfo getApplicationInfo() {
        return mApplicationInfo;
    }

    public void setApplicationInfo(ApplicationInfo applicationInfo) {
        this.mApplicationInfo = applicationInfo;
    }

    public void setLoadedApk(Object loadedApk) {
        this.mLoadedApk = loadedApk;
    }

    public Object getLoadedApk() {
        return mLoadedApk;
    }

    public void setServiceInfoMap(Map<ComponentName, ServiceInfo> serviceInfoMap) {
        this.mServiceInfoMap = serviceInfoMap;
    }

    public Map<ComponentName, ServiceInfo> getServiceInfoMap() {
        return mServiceInfoMap;
    }

    private ServiceInfo findServiceInfo(Intent pluginIntent) {
        for (ComponentName componentName : mServiceInfoMap.keySet()) {
            if (componentName.equals(pluginIntent.getComponent())) {
                return mServiceInfoMap.get(componentName);
            }
        }
        return null;
    }


    public Application makeApplication() {
        return makeApplication(false, mPluginManager.getInstrumentation());
    }

    private Application makeApplication(boolean forceDefaultAppClass, Instrumentation instrumentation) {
        if (null != this.mApplication) {
            return this.mApplication;
        }

        String appClass = this.mApplicationInfo.className;
        if (forceDefaultAppClass || null == appClass) {
            appClass = "android.app.Application";
        }

        try {
            this.mApplication = instrumentation.newApplication(this.mClassLoader, appClass, this.getPluginContext());
            instrumentation.callApplicationOnCreate(this.mApplication);
            return this.mApplication;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Context getPluginContext() {
        return mPluginManager.getContext();
    }

}
