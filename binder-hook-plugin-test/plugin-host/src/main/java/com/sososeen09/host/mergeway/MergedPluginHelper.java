package com.sososeen09.host.mergeway;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.ArrayMap;

import com.sososeen09.host.PluginClassLoader;
import com.sososeen09.host.hook.HookHelper;
import com.sososeen09.host.utils.ReflectUtil;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * Created by yunlong.su on 2018/4/10.
 */

public class MergedPluginHelper {
    private static Context applicationContext;
    private static MergedPluginHelper sMergedPluginHelper;
    private ArrayMap<String, DexClassLoader> mLoaderArrayMap = new ArrayMap<>(4);
    private ArrayMap<String, MergedPluginApk> loadedApk = new ArrayMap<>();


    public static void init(Context context) {
        applicationContext = context.getApplicationContext();
    }

    private MergedPluginHelper() {
        HookHelper.initHook(applicationContext);
    }

    public static MergedPluginHelper getInstance() {
        if (sMergedPluginHelper == null) {
            sMergedPluginHelper = new MergedPluginHelper();
        }
        return sMergedPluginHelper;
    }

    public void mergePlugin(String pluginPath) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException, NoSuchFieldException {

        // 融合ClassLoader，插件中的dex插入到速度Element数组的前部
        PathClassLoader pathClassLoader = (PathClassLoader) MergedPluginHelper.class.getClassLoader();
        File dexOptFile = applicationContext.getDir("dex", Context.MODE_PRIVATE);
        PluginClassLoader pluginClassLoader = new PluginClassLoader(pluginPath, dexOptFile.getAbsolutePath(), null, applicationContext.getClassLoader());

        ReflectUtil.mergeClassLoader(pathClassLoader, pluginClassLoader);


        AssetManager pluginAssetManager = AssetManager.class.newInstance();
        Method addAssetPathMethod = pluginAssetManager.getClass().getDeclaredMethod("addAssetPath", String.class);
        addAssetPathMethod.setAccessible(true);
        addAssetPathMethod.invoke(pluginAssetManager, pluginPath);
//        手动实例化
        Method ensureStringBlocks = AssetManager.class.getDeclaredMethod("ensureStringBlocks");
        ensureStringBlocks.setAccessible(true);
        ensureStringBlocks.invoke(pluginAssetManager);

//        // 为什么不能把插件的资源融合到宿主中呢？
//
//        Class<?> stringBlockField = Class.forName("android.content.res.StringBlock");
//        Field mStringBlocksField = ReflectUtil.findField(pluginAssetManager, "mStringBlocks");
//        Object mStringBlocks =  mStringBlocksField.get(pluginAssetManager);
//
//        AssetManager assets = applicationContext.getAssets();
//        Object o = Array.newInstance(mStringBlocks.getClass().getComponentType(), 1);
//        Method makeStringBlocks = ReflectUtil.findMethod(assets, "makeStringBlocks", o.getClass());
//        makeStringBlocks.invoke(assets, mStringBlocks);
//

//            插件的StringBloac被实例化了
        Resources supResource = applicationContext.getResources();
        Resources pluginResource = new Resources(pluginAssetManager, supResource.getDisplayMetrics(), supResource.getConfiguration());


        MergedPluginApk pluginApk = loadedApk.get(pluginPath);
        if (pluginApk == null) {
            pluginApk = new MergedPluginApk(this);


            pluginApk.setAssetManager(pluginAssetManager);
            pluginApk.setResources(pluginResource);

            PackageManager packageManager = applicationContext.getPackageManager();
            pluginApk.setPackageInfo(packageManager.getPackageArchiveInfo(pluginPath, PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES | PackageManager.GET_PROVIDERS));

            loadedApk.put(pluginApk.getPackageInfo().packageName, pluginApk);

        }
//        HookHelper.hookPluginAssetAndResources(pluginAssetManager, pluginResource);


    }

    public Context getContext() {
        return applicationContext;
    }
}
