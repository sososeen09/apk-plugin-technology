package com.sososeen09.apk.plugin.sample

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import com.sososeen09.plugin.lib.PluginActivityInterface

/**
 * Created by yunlong.su on 2018/3/29.
 */
class ProxyActivity : Activity() {
    lateinit var activityInterface: PluginActivityInterface
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val className = intent.getStringExtra("className")
        val clazz = classLoader?.loadClass(className)
        activityInterface = clazz?.newInstance() as PluginActivityInterface
        activityInterface.attach(this)
        activityInterface.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        activityInterface.onStart()
    }

    override fun onResume() {
        super.onResume()
        activityInterface.onResume()
    }

    override fun onRestart() {
        super.onRestart()
        activityInterface.onRestart()
    }

    override fun onPause() {
        super.onPause()
        activityInterface.onPause()
    }

    override fun onStop() {
        super.onStop()
        activityInterface.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        activityInterface.onDestroy()
    }

    override fun getClassLoader(): ClassLoader? {
        return PluginManager.getInstance().getDexClassLoader()
    }

    /**
     * 加载布局，需要用到Resources文件
     */
    override fun getResources(): Resources? {
        return PluginManager.getInstance().getRecources()
    }


    override fun startActivity(intent: Intent) {
        val delegateIntent = Intent(this, ProxyActivity::class.java)
        delegateIntent.putExtra("className", intent.component.className)
        super.startActivity(delegateIntent)

    }


    override fun startService(service: Intent): ComponentName {
        val delegateIntent = Intent(this, ProxyService::class.java)
        delegateIntent.putExtra("serviceName", service.component.className)
        return super.startService(delegateIntent)
    }

}