package com.sososeen09.apk.plugin.sample

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import dalvik.system.DexClassLoader

/**
 * Created by yunlong.su on 2018/3/29.
 */
class PluginManager private constructor() {


    companion object {
        val pluginManager: PluginManager = PluginManager()
        fun getInstance(): PluginManager {
            return pluginManager
        }

        private var applicationContext: Context? = null

        fun init(context: Context) {
            this.applicationContext = context.applicationContext
        }
    }

    private var packageInfo: PackageInfo? = null
    private var resources: Resources? = null
    private var dexClassLoader: DexClassLoader? = null


    fun loadPlugin(pluginPath: String) {
        val dexOptFile = applicationContext?.getDir("dex", Context.MODE_PRIVATE)

        dexClassLoader = DexClassLoader(pluginPath, dexOptFile?.absolutePath, null, applicationContext?.classLoader)


        val assetManager = AssetManager::class.java.newInstance()

        val addPathMethos = assetManager.javaClass.getMethod("addAssetPath", String::class.java)

        addPathMethos.invoke(assetManager, pluginPath)

        resources = Resources(assetManager, applicationContext?.resources?.displayMetrics, applicationContext?.resources?.configuration)

        val packageManager = applicationContext?.packageManager
        packageInfo = packageManager?.getPackageArchiveInfo(pluginPath, PackageManager.GET_ACTIVITIES)

    }

    fun getPackageInfo(): PackageInfo? {

        return packageInfo
    }

    fun getRecources(): Resources? {
        return resources
    }

    fun getDexClassLoader(): DexClassLoader? {
        return dexClassLoader
    }

}