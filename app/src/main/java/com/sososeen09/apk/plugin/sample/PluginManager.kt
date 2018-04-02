package com.sososeen09.apk.plugin.sample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import dalvik.system.DexClassLoader
import java.io.File
import java.lang.reflect.Field
import java.lang.reflect.Method

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

        parseReceivers(applicationContext!!, pluginPath)

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

    /**
     * 解析插件中的静态BroadcastReceiver，然后进行注册，实际上就是静态广播动态注册化
     */
    fun parseReceivers(context: Context, path: String) {
        try {
            val packageParserClass = Class.forName("android.content.pm.PackageParser")
            val packageParser = packageParserClass.newInstance()
            val parsePackage = packageParserClass.getDeclaredMethod("parsePackage", File::class.java, Int::class.java) as Method
            //调用PackageParser的parsePackage方法，获得了PackageParser$Package对象
            val packageObj = parsePackage.invoke(packageParser, File(path), PackageManager.GET_ACTIVITIES)

            //获取PackageParser$Package类的receivers字段，是public final ArrayList<Activity> receivers = new ArrayList<Activity>(0);
            val fieldReceivers = packageObj.javaClass.getDeclaredField("receivers") as Field
            //获取receivers集合，类型是ArrayList<Activity>
            val receivers = fieldReceivers.get(packageObj) as List<*>

            // 此Activity非四大组件中的Activity，是表示的AndroidManifest.xml中的activity或者receiver结点
            val componentClass = Class.forName("android.content.pm.PackageParser\$Component")
            val intentsField = componentClass.getDeclaredField("intents") as Field
            val classNameField = componentClass.getDeclaredField("className") as Field

            for (receiver in receivers) {
                val intentFilters = intentsField.get(receiver) as ArrayList<IntentFilter>
                val receiverClassName = classNameField.get(receiver) as String
                val broadcastReceiver = dexClassLoader?.loadClass(receiverClassName)?.newInstance() as BroadcastReceiver
                for (intentFilter in intentFilters) {
                    applicationContext?.registerReceiver(broadcastReceiver, intentFilter)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}