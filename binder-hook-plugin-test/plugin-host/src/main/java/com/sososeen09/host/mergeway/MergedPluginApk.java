package com.sososeen09.host.mergeway;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;

/**
 * Created by yunlong.su on 2018/4/8.
 */

public class MergedPluginApk {
    private final MergedPluginHelper mergedPluginHelper;
    private PackageInfo mPackageInfo;
    private Resources mResources;
    private AssetManager mAssetManager;
    private Context mPluginContext;


    public MergedPluginApk(MergedPluginHelper mergedPluginHelper) {
        this.mergedPluginHelper = mergedPluginHelper;
        mPluginContext = new PluginContext(this);
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


    public Context getPluginContext() {
        return mPluginContext;
    }

    public Context getHostContext() {
        return mergedPluginHelper.getContext();
    }

    public AssetManager getAssets() {
        return mAssetManager;
    }

    public void setAssetManager(AssetManager assetManager) {
        this.mAssetManager = assetManager;
    }
}
